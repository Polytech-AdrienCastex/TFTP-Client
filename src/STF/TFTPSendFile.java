/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package STF;

import STF.TFTPMessage.Header;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author A
 */
public class TFTPSendFile extends TFTPDialog
{
    public TFTPSendFile(String address)
    {
        super(address);
    }
    
    public void Sendfile(String localFilePath) throws FileNotFoundException, IOException, Exception
    {
        File f = new File(localFilePath);
        if(f.exists() && !f.isDirectory())
        {
            FileInputStream fstream = new FileInputStream(f);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            
            byte[] buffer = new byte[512];
            int length;
            int port;
            
            SendMsg(new TFTPRequest(Header.WRQ, f.getName(), "netascii"), 69);
            TFTPMessage resp = ReceiveMsg();
            
            if(resp == null || !(resp instanceof TFTPAck) || ((TFTPAck)resp).getBlockNumber() != 0)
                ; // ERROR
            
            port = resp.getPort();
                
            TFTPData dataMsg;
            int blockNumber = 1;
            
            while((length = in.read(buffer, 0, 512)) != 0)
            {
                dataMsg = new TFTPData(blockNumber, buffer, length);
                SendMsg(dataMsg, port);
                
                resp = ReceiveMsg();
                if(resp == null || !(resp instanceof TFTPAck) || ((TFTPAck)resp).getBlockNumber() != blockNumber)
                    ; // ERROR
                 
                
                blockNumber++;
            }
        }
    }
}
