/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TFTP;

import java.net.DatagramPacket;
import java.net.InetAddress;

/** Packet for TFTP dialog */
public class TFTPPacket
{
    /** Direction of a transfer */
    public enum Direction
    {
        Emission,
        Reception
    }
    /** Type of a packet */
    public enum Type
    {
        RRQ, WRQ, ACK, DATA, ERROR,
        UNKNOWN
    }
    /** Type of an error */
    public enum Error
    {
        NOT_IDENTIFIED,
        FILE_NOT_FOUND,
        ACCESS_VIOLATION,
        DISK_FULL,
        ILLEGAL_OPERATION,
        UNKNOWN_TID,
        FILE_ALREADY_EXISTS,
        NO_SUCH_USER,
        
        UNKNOWN,
        NO_ERROR,
        
        SOCKET_USE_IMPOSSIBLE,
        NO_RESPONSE
    }
    
    /** Create a manager for a received packet */
    public TFTPPacket(DatagramPacket packet) // Reception
    {
        this.packet = packet;
        
        int length = packet.getLength();
        datas = new byte[length];
        System.arraycopy(packet.getData(), 0, datas, 0, length);
    }
    /** Create a RRQ packet or WRS packet */
    public TFTPPacket(String remoteFileName, Direction direction, String mode) // RRQ/WRQ
    {
        int dir = 1;
        if(direction == Direction.Emission)
            dir = 2;
        
        setDatas(new byte[][]
        {
            Converters.GetBytesFromUShort(dir),
            Converters.GetTFTPStringFromString(remoteFileName),
            Converters.GetTFTPStringFromString(mode)
        });
    }
    /** Create a RRQ packet or WRS packet */
    public TFTPPacket(String remoteFileName, Direction direction) // RRQ/WRQ
    {
        this(remoteFileName, direction, "netascii");
    }
    /** Create a DATA packet */
    public TFTPPacket(int BlockNumber, byte[] datas) // DATA
    {
        setDatas(new byte[][]
        {
            Converters.GetBytesFromUShort(3),
            Converters.GetBytesFromUShort(BlockNumber),
            datas
        });
    }
    /** Create an ACK packet */
    public TFTPPacket(int BlockNumber) // ACK
    {
        setDatas(new byte[][]
        {
            Converters.GetBytesFromUShort(4),
            Converters.GetBytesFromUShort(BlockNumber)
        });
    }
    /** Create an ERROR packet */
    public TFTPPacket(Error error, String message) // ERROR
    {
        int errorId = 0;
        switch(error)
        {
            default:
            case NOT_IDENTIFIED:
                errorId = 0;
                
            case FILE_NOT_FOUND:
                errorId = 1;
                
            case ACCESS_VIOLATION:
                errorId = 2;
                
            case DISK_FULL:
                errorId = 3;
                
            case ILLEGAL_OPERATION:
                errorId = 4;
                
            case UNKNOWN_TID:
                errorId = 5;
                
            case FILE_ALREADY_EXISTS:
                errorId = 6;
                
            case NO_SUCH_USER:
                errorId = 7;
        }
        
        setDatas(new byte[][]
        {
            Converters.GetBytesFromUShort(5),
            Converters.GetBytesFromUShort(errorId),
            Converters.GetTFTPStringFromString(message)
        });
    }
    /** Create an ERROR packet */
    public TFTPPacket(Error error) // ERROR
    {
        this(error, null);
    }
    
    private void setDatas(byte[][] datas)
    {
        this.datas = Converters.ConcatBytes(datas);
    }
    
    
    public boolean isTerminalData()
    {
        return isTerminalData(516);
    }
    public boolean isTerminalData(int maxTotalLength)
    {
        return this.datas.length < maxTotalLength;
    }
    
    
    
    private DatagramPacket packet;
    private byte[] datas;
    
    private int readTwoByte(int offset)
    {
        int lower = datas[offset + 1];
        if(lower < 0)
            lower = 256 + lower;
        
        int upper = datas[offset];
        if(upper < 0)
            upper = 256 + lower;
        
        return upper * 256 + lower;
    }
    
    /** Get type from a packet */
    public Type getType()
    {
        switch(readTwoByte(0))
        {
            case 1:
                return Type.RRQ;
                
            case 2:
                return Type.WRQ;
                
            case 3:
                return Type.DATA;
                
            case 4:
                return Type.ACK;
                
            case 5:
                return Type.ERROR;
                
            default:
                return Type.UNKNOWN;
        }
    }
    
    /** Get error type from an ERROR packet */
    public Error getError()
    {
        switch(readTwoByte(2))
        {
            case 0:
                return Error.NOT_IDENTIFIED;
                
            case 1:
                return Error.FILE_NOT_FOUND;
                
            case 2:
                return Error.ACCESS_VIOLATION;
                
            case 3:
                return Error.DISK_FULL;
                
            case 4:
                return Error.ILLEGAL_OPERATION;
                
            case 5:
                return Error.UNKNOWN_TID;
                
            case 6:
                return Error.FILE_ALREADY_EXISTS;
                
            case 7:
                return Error.NO_SUCH_USER;
                
            default:
                return Error.UNKNOWN;
        }
    }
    /** Get error message from an ERROR packet */
    public String getErrorMessage()
    {
        return Converters.GetStringFromSTFTPtring(datas, 4);
    }
    
    /** Get block number from a DATA or ACK packet */
    public int getBlockNumber()
    {
        return readTwoByte(2);
    }
    
    /** Get data part from a DATA packet */
    public byte[] getData()
    {
        byte[] datasOut = new byte[datas.length - 4];
        System.arraycopy(datas, 4, datasOut, 0, datasOut.length);
        return datasOut;
    }
    
    /** Give the byte array format of the packet */
    public byte[] toByteArray()
    {
        return this.datas;
    }
    
    /** Get port number from a packet */
    public int getPort()
    {
        return packet.getPort();
    }
    
    /** Get IP address from a packet */
    public InetAddress getAddress()
    {
        return packet.getAddress();
    }
}
