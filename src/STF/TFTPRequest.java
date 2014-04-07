/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package STF;

import static STF.TFTPMessage.GetStringFromSTFTPtring;
import static STF.TFTPMessage.GetTFTPStringFromString;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author p1002239
 */
public class TFTPRequest extends TFTPMessage // = RRQ et WRQ
{
    public TFTPRequest(Header type, String fileName, String mode)
    {
        this.type = type;
        this.fileName = fileName;
        this.mode = mode;
    }
    
    TFTPRequest(byte[] datas) throws WrongDataFormatException, UnsupportedEncodingException
    {
        if(datas.length < 4)
            throw new WrongDataFormatException();
        
        fileName = GetStringFromSTFTPtring(datas, 2);
        mode = GetStringFromSTFTPtring(datas, fileName.length() + 3);
    }
    
    
    
    private String fileName;
    public String getFileName()
    {
        return fileName;
    }
    
    private String mode;
    public String getMode()
    {
        return mode;
    }
    

    @Override
    public byte[] getDataFormated() throws Exception
    {
        byte[] type = this.type.getBytes();
        byte[] filename = GetTFTPStringFromString(getFileName());
        byte[] mode = GetTFTPStringFromString(getMode());

        return ConcatBytes(new byte[][]
        {
            type,
            filename,
            mode
        });
    }
    
}
