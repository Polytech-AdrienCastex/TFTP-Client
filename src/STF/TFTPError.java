/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package STF;

import static STF.TFTPMessage.GetStringFromSTFTPtring;
import static STF.TFTPMessage.GetTFTPStringFromString;
import static STF.TFTPMessage.GetUShortFromBytes;
import static STF.TFTPMessage.Header.RRQ;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author A
 */
public class TFTPError extends TFTPMessage
{
    public enum Error
    {
        NOT_IDENTIFIED((byte)0),
        FILE_NOT_FOUND((byte)1),
        ACCESS_VIOLATION((byte)2),
        DISK_FULL((byte)3),
        ILLEGAL_OPERATION((byte)4),
        UNKNOWN_TID((byte)5),
        FILE_ALREADY_EXISTS((byte)6),
        NO_SUCH_USER((byte)7);
        
        private final byte value;
        
        private Error(byte value)
        {
            this.value = value;
        }
        
        protected byte[] getBytes()
        {
            return new byte[] { 0, value };
        }
        
        public static Error getFromByte(byte value)
        {
            Error error = null;
            
            error = _recError(value, NOT_IDENTIFIED, error);
            error = _recError(value, FILE_NOT_FOUND, error);
            error = _recError(value, ACCESS_VIOLATION, error);
            error = _recError(value, DISK_FULL, error);
            error = _recError(value, ILLEGAL_OPERATION, error);
            error = _recError(value, UNKNOWN_TID, error);
            error = _recError(value, FILE_ALREADY_EXISTS, error);
            error = _recError(value, NO_SUCH_USER, error);
            
            return error;
        }
        private static Error _recError(byte value, Error error, Error current)
        {
            if(current != null)
                return current;
            
            if(value == error.value)
                return error;
            
            return current;
        }
    }
    
    public TFTPError(Error error, String msg)
    {
        this.error = error;
        this.msg = msg;
    }
    TFTPError(byte[] datas) throws WrongDataFormatException, UnsupportedEncodingException
    {
        if(datas.length < 5)
            throw new WrongDataFormatException();
        
        error = Error.getFromByte(datas[3]);
        msg = GetStringFromSTFTPtring(datas, 4);
    }
    
    private Error error;
    public Error getError()
    {
        return error;
    }
    
    private String msg;
    public String getMessage()
    {
        return msg;
    }

    @Override
    public byte[] getDataFormated() throws Exception
    {
        byte[] type = this.type.getBytes();
        byte[] errorcode = this.error.getBytes();
        byte[] msg = GetTFTPStringFromString(this.msg);

        return ConcatBytes(new byte[][]
        {
            type,
            errorcode,
            msg
        });
    }
}
