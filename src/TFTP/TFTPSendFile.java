/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TFTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Adrien
 */
public class TFTPSendFile extends TFTPDialog
{
    public TFTPSendFile(String address, String remoteFileName, String sourceFileName)
    {
        super(address);
        
        this.remoteFileName = remoteFileName;
        this.sourceFileName = sourceFileName;
    }
    
    private final String remoteFileName;
    private final String sourceFileName;
    
    private DataInputStream stream;
    private int block_number;
    private boolean file_end;
    
    @Override
    protected TFTPPacket.Error Initialization()
    {
        block_number = 0;
        file_end = false;
        
        File sourceFile = new File(sourceFileName);
        
        // Test if file local exists
        if(!sourceFile.exists())
            return TFTPPacket.Error.FILE_NOT_FOUND;
        
        // Create and open the local destination file
        stream = super.CreateLocalReadStream(sourceFile);
        
        return TFTPPacket.Error.NO_ERROR;
    }
    
    @Override
    protected TFTPPacket getInitialPacket()
    {
        return new TFTPPacket(remoteFileName, TFTPPacket.Direction.Emission);
    }
    
    @Override
    protected TFTPPacket.Error Runtime(TFTPPacket packet_response) throws IOException
    {
        TFTPPacket.Error result = TFTPPacket.Error.NO_ERROR;
        
        switch(packet_response.getType())
        {
            case ACK:
                if(block_number == packet_response.getBlockNumber())
                { // GOOD BLOCK #
                    if(file_end)
                    { // Last packet was a end file packet
                        Terminate();
                    }
                    else
                    { // Last packet was not a end file packet
                        block_number++;

                        // Read from the file
                        int len = 512;
                        byte[] datas = new byte[len];
                        int real_len = stream.read(datas, 0, len);

                        byte[] real_datas;
                        if(real_len != len)
                        { // End of the file
                            real_datas = new byte[real_len];
                            System.arraycopy(datas, 0, real_datas, 0, real_len);

                            // We will wait the next ACK for terminate
                            file_end = true;
                        }
                        else
                        { // File not ended
                            real_datas = datas;
                        }

                        // Send DATA
                        TFTPPacket packet_send = new TFTPPacket(block_number, real_datas);
                        super.SendPacket(packet_send);
                    }
                }
                else
                { // WRONG BLOCK #
                    // Ignore the packet
                }
                break;

            default: // Abnormal answer
                // Ignore the packet
                break;
        }
        
        return result;
    }
    
    
    @Override
    protected void Closing() throws Exception
    {
        stream.close();
    }

    @Override
    protected void ErrorOccured(TFTPPacket.Error error)
    {
        // Nothing to do
    }
}
