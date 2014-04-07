/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package STF;

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
            if(value == header.value)
                return header;
            return current;
        }
    }
    
    protected Header Type;
    
    public abstract byte[] getData();
    
    public static TFTPMessage CreateFromDtgData(byte[] datas)
    {
        if(datas.length <= 1) // Taille trop petite
            return null;
        
        TFTPMessage msg = null;
        
        switch(Header.getFromByte(datas[1]))
        {
            case RRQ:
                msg = new TFTPGetFile(datas);
                break;
                
            default:
                break;
        }
        
        return msg;
    }
}
