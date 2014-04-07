/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package STF;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;

/**
 *
 * @author p1002239
 */
public abstract class TFTPMessage
{
    protected enum Header
    {
        RRQ((byte)1),
        WRQ((byte)2),
        DATA((byte)3),
        ACK((byte)4),
        ERROR((byte)5);
        
        private final byte value;
        
        private Header(byte value)
        {
            this.value = value;
        }
        
        protected byte[] getBytes()
        {
            return new byte[] { 0, value };
        }
        
        public static Header getFromByte(byte value)
        {
            Header header = null;
            
            header = _recHeader(value, RRQ, header);
            header = _recHeader(value, WRQ, header);
            header = _recHeader(value, DATA, header);
            header = _recHeader(value, ACK, header);
            header = _recHeader(value, ERROR, header);
            
            return header;
        }
        private static Header _recHeader(byte value, Header header, Header current)
        {
            if(current != null)
                return current;
            
            if(value == header.value)
                return header;
            
            return current;
        }
    }
    
    protected static byte[] ConcatBytes(byte[][] array)
    {
        int totalLen = 0;
        for(byte[] b : array)
            totalLen += b.length;
        
        byte[] datas = new byte[totalLen];
        int cursor = 0;
        
        for(byte[] b : array)
        {
            System.arraycopy(b, 0, datas, cursor, b.length);
            cursor += b.length;
        }
        
        return datas;
    }
    
    
    protected static byte[] GetBytesFromUShort(int value)
    {
        byte[] datas = new byte[2];
        datas[0] = (byte)(value & 0xff); // Poid faible
        datas[1] = (byte)((value >> 8) & 0xff); // Poid fort
        
        return datas;
    }
    protected static int GetUShortFromBytes(byte[] datas, int offset)
    {
        int value;
        
        value = (int)datas[offset]; // Poid faible
        value += (int)datas[offset + 11] << 8; // Poid fort
        
        return value;
    }
            
    
    @SuppressWarnings("empty-statement")
    protected static String GetStringFromSTFTPtring(byte[] datas, int offset) throws WrongDataFormatException, UnsupportedEncodingException
    {
        int end;
        for(end = offset; end < datas.length && datas[end] != 0; end++)
            ;
        int len = end - offset;
        
        if(end == datas.length) // Pas de 0 pour terminer la chaine = erreur
            throw new WrongDataFormatException();
        
        return new String(datas, offset, len, "UTF-8");
    }
    protected static byte[] GetTFTPStringFromString(String str) throws UnsupportedEncodingException
    {
        byte[] strByteArray = str.getBytes("UTF-8");
        byte[] datas = new byte[strByteArray.length + 1]; // + 1 pour le 0 de fin
        
        System.arraycopy(strByteArray, 0, datas, 0, strByteArray.length);
        datas[datas.length - 1] = 0;
        
        return datas;
    }
    
    protected Header type;
    
    private int port;
    public int getPort()
    {
        return port;
    }
    
    public abstract byte[] getDataFormated() throws Exception;
    
    public static TFTPMessage CreateFromDtg(DatagramPacket packet) throws WrongDataFormatException, UnsupportedEncodingException
    {
        byte[] datas = packet.getData();
        
        if(datas.length <= 1) // Taille trop petite
            return null;
        
        TFTPMessage msg = null;
        
        Header type = Header.getFromByte(datas[1]);
        switch(type)
        {
            case WRQ:
            case RRQ:
                msg = new TFTPRequest(datas);
                break;
                
            case DATA:
                msg = new TFTPData(datas);
                break;
                
            case ACK:
                msg = new TFTPAck(datas);
                break;
                
            case ERROR:
                msg = new TFTPError(datas);
                break;
                
            default:
                break;
        }
        
        if(msg != null)
        {
            msg.type = type;
            msg.port = packet.getPort();
        }
        
        return msg;
    }
    
    
    public static class WrongDataFormatException extends Exception
    {
        public WrongDataFormatException()
        {
            super("Wrong data format");
        }
    }
}
