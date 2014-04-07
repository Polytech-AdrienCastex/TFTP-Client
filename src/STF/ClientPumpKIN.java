/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package STF;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author p1002239
 */
public class ClientPumpKIN
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    
    
    public int Sendfile(String localFilePath, InetAddress remoteAddress) throws FileNotFoundException
    {
        int crem = -1;
        
        File f = new File(localFilePath);
        if(f.exists() && !f.isDirectory())
        {
            DatagramSocket socket;
        }
        else
            throw new FileNotFoundException(localFilePath);
        
        return crem;
    }
    
}
