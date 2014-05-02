/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TFTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class TFTPDialog
{
    public TFTPDialog(String address)
    {
        this.address = address;
        
        Reset();
    }
    
    private String address;
    private InetAddress inetAddress;
    
    private DatagramSocket socket = null;
    
    protected void Open() throws SocketException, UnknownHostException
    {
        if(socket == null)
        {
            inetAddress = InetAddress.getByName(address);
            
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        }
    }
    
    protected void Close()
    {
        if(socket != null)
        {
            socket.close();
            Reset();
        }
    }
    
    private void Reset()
    {
        socket = null;
        timeout = 0;
        _current_TID = -1;
    }
    
    private int timeout = 0;
    protected void setTimeOut(int timeout) throws SocketException
    {
        this.timeout = timeout;
        if(socket != null)
            socket.setSoTimeout(timeout);
    }
    
    protected void SendPacket(TFTPPacket tftp_packet, int port) throws IOException
    {
        byte[] datas = tftp_packet.toByteArray();
        
        DatagramPacket dtg_packet = new DatagramPacket(datas, datas.length, inetAddress, port);
        
        socket.send(dtg_packet);
    }
    protected void SendPacket(TFTPPacket tftp_packet) throws IOException
    {
        SendPacket(tftp_packet, _current_TID);
    }
    
    protected TFTPPacket ReceivePacket() throws IOException, SocketTimeoutException
    {
        byte[] buffer = new byte[2048];
        DatagramPacket dtg_packet = new DatagramPacket(buffer, buffer.length);
        
        socket.receive(dtg_packet);
        
        TFTPPacket tftp_packet = new TFTPPacket(dtg_packet);
        
        return tftp_packet;
    }
    
    
    private int _current_TID = -1;
    protected boolean CheckTID(TFTPPacket tftp_packet)
    {
        if(_current_TID == -1)
        {
            _current_TID = tftp_packet.getPort();
            return true;
        }
        else
            return tftp_packet.getPort() == _current_TID;
    }
    
    protected DataInputStream CreateLocalReadStream(File localFile)
    {
        try
        {
            FileInputStream fstream = new FileInputStream(localFile);
            DataInputStream in = new DataInputStream(fstream);
            
            return in;
        } catch (FileNotFoundException ex)
        {
            return null;
        }
    }
    protected DataOutputStream CreateLocalWriteStream(File localFile)
    {
        try
        {
            FileOutputStream fstream = new FileOutputStream(localFile);
            DataOutputStream out = new DataOutputStream(fstream);
            
            return out;
        } catch (FileNotFoundException ex)
        {
            return null;
        }
    }
    
    
    
    
    
    
    protected abstract TFTPPacket.Error Initialization();
    protected abstract TFTPPacket getInitialPacket();
    protected abstract TFTPPacket.Error Runtime(TFTPPacket packet_response) throws IOException;
    protected abstract void Closing() throws Exception;
    protected abstract void ErrorOccured(TFTPPacket.Error error);
    
    
    private boolean terminate;
    protected void Terminate()
    {
        terminate = true;
    }
    
    public TFTPPacket.Error Execute()
    {
        TFTPPacket.Error result = Execution();
        if(result == null)
            result = TFTPPacket.Error.NO_ERROR;
        
        if(result != TFTPPacket.Error.NO_ERROR)
            ErrorOccured(result);
        
        return result;
    }
    private TFTPPacket.Error Execution()
    {
        TFTPPacket.Error result = null;
        
        result = Initialization();
        if(!(result == null || result == TFTPPacket.Error.NO_ERROR))
            return result;
        
        try
        {
            // Open the local port
            Open();
            setTimeOut(1000);
            
            // Initialize dialog
            TFTPPacket packet_response;
            TFTPPacket packet_send = getInitialPacket();
            SendPacket(packet_send, 69);
            
            // Dialog
            terminate = false;
            boolean firstPass = true;
            
            do
            {
                try
                {
                    packet_response = ReceivePacket();
                }
                catch(SocketTimeoutException ex)
                { // First loss of the packet in the network
                    try // Try again
                    {
                        if(firstPass)
                            SendPacket(packet_send, 69);
                        else
                            SendPacket(packet_send);
                        packet_response = ReceivePacket();
                    }
                    catch(SocketTimeoutException ex2)
                    { // Second loss of the packet in the network -> Stop the transmission
                        result = TFTPPacket.Error.NO_RESPONSE;
                        throw ex2;
                    }
                }
                
                if(CheckTID(packet_response))
                { // GOOD TID
                    switch(packet_response.getType())
                    {
                        case ERROR: // Error packet
                            result = packet_response.getError(); // Return the error

                            Terminate();
                            break;

                        default: // Other packet types
                            result = Runtime(packet_response);
                            break;
                    }
                }
                else
                { // WRONG TID

                }
                
                firstPass = false;
            } while(!terminate);
        }
        catch(IOException ex)
        { // Error from socket using
            if(result == null || result == TFTPPacket.Error.NO_ERROR)
                result = TFTPPacket.Error.SOCKET_USE_IMPOSSIBLE;
        }
        finally
        { // In every case : close the socket and the stream
            Close();
            try
            {
                Closing();
            } catch (Exception ex) { }
        }
        
        if(result == null)
        { // Transfer well done!
            result = TFTPPacket.Error.NO_ERROR;
        }
        
        return result;
    }
}
