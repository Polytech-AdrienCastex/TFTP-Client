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
public class TFTPGetFile extends TFTPMessage
{
    public TFTPGetFile()
    {
        
    }
    
    TFTPGetFile(byte[] datas)
    {
        
    }
    
    
    
    private String fileName;
    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    

    @Override
    public byte[] getData()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
