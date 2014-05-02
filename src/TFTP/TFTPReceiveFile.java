/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TFTP;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Adrien
 */
public class TFTPReceiveFile extends TFTPDialog
{
    public TFTPReceiveFile(String address, String remoteFileName, String destinationFileName)
    {
        super(address);
        
        this.remoteFileName = remoteFileName;
        this.destinationFileName = destinationFileName;
    }
    
    private final String remoteFileName;
    private final String destinationFileName;
    
    private DataOutputStream stream;
    private int block_number;
    
    @Override
    protected TFTPPacket.Error Initialization()
    {
        block_number = 0;
        
        File destinationFile = new File(destinationFileName);
        
        // Test if file local exists
        if(destinationFile.exists())
            return TFTPPacket.Error.FILE_ALREADY_EXISTS;
        
        // Create and open the local destination file
        stream = super.CreateLocalWriteStream(destinationFile);
        
        return TFTPPacket.Error.NO_ERROR;
    }
    
    @Override
    protected TFTPPacket getInitialPacket()
    {
        return new TFTPPacket(remoteFileName, TFTPPacket.Direction.Reception);
    }
    
    @Override
    protected TFTPPacket.Error Runtime(TFTPPacket packet_response) throws IOException
    {
        TFTPPacket.Error result = TFTPPacket.Error.NO_ERROR;
        
        switch(packet_response.getType())
        {
            case DATA:
                if(block_number + 1 == packet_response.getBlockNumber())
                { // GOOD BLOCK #
                    block_number = packet_response.getBlockNumber();

                    // Write in the file
                    stream.write(packet_response.getData());

                    // Send ACK
                    TFTPPacket packet_send = new TFTPPacket(block_number);
                    super.SendPacket(packet_send);

                    // Loop leave condition
                    if(packet_response.isTerminalData())
                        Terminate();
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
        if(error != TFTPPacket.Error.FILE_ALREADY_EXISTS)
        {
            File file = new File(destinationFileName);
            if(file.exists())
                file.delete();
        }
    }
}
