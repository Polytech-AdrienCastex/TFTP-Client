/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package STF;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author p1002239
 */
public class TFTPDialog
{
    public TFTPDialog(String address)
    {
        this.address = address;
    }
    
    private String address;
    private InetAddress inetAddress;
    
    protected DatagramSocket socket = null;
    
    public void Open() throws SocketException, UnknownHostException
    {
        if(socket == null)
        {
            inetAddress = InetAddress.getByName(address);
            
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        }
    }
    
    public void Close()
    {
        if(socket != null)
        {
            socket.close();
            socket = null;
        }
    }
    
    private int timeout;
    public void setTimeOut(int timeout) throws SocketException
    {
        this.timeout = timeout;
        if(socket != null)
            socket.setSoTimeout(timeout);
    }
    
    protected void SendDtg(TFTPMessage msg, int port) throws IOException
    {
        byte[] datas = msg.getData();
        
        DatagramPacket packet = new DatagramPacket(datas, datas.length, inetAddress, port);
        
        socket.send(packet);
    }
    
    protected byte[] ReceiveDtg() throws IOException, SocketTimeoutException
    {
        byte[] buffer = new byte[516];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        socket.receive(packet);
        
        return packet.getData();
    }
}
