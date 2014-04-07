/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package STF;

import static STF.TFTPMessage.GetTFTPStringFromString;
import javax.crypto.IllegalBlockSizeException;

/**
 *
 * @author A
 */
public class TFTPData extends TFTPMessage
{
    public TFTPData(int blockNumber, byte[] datas, int length) throws IllegalBlockSizeException
    {
        if(datas == null || length > 512)
            throw new IllegalBlockSizeException();
        
        this.blockNumber = blockNumber;
        
        if(datas.length == length)
            this.datas = datas;
        else
        {
            this.datas = new byte[length];
            System.arraycopy(datas, 0, this.datas, 0, length);
        }
    }
    TFTPData(byte[] datas) throws WrongDataFormatException
    {
        if(datas.length <= 4)
            throw new WrongDataFormatException();
        
        this.blockNumber = GetUShortFromBytes(datas, 2);
        this.datas = new byte[datas.length - 4];
        System.arraycopy(datas, 4, this.datas, 0, this.datas.length);
    }
    
    // Pas de short car le short est signé
    private int blockNumber;
    public int getBlockNumber()
    {
        return blockNumber;
    }
    
    private byte[] datas;
    public byte[] getDatas()
    {
        return datas.clone();
    }
    
    public boolean isEndBlock()
    {
        return datas.length < 512;
    }

    @Override
    public byte[] getDataFormated() throws Exception
    {
        byte[] type = this.type.getBytes();
        byte[] blocknumber = GetBytesFromUShort(getBlockNumber());
        byte[] datas = getDatas();

        return ConcatBytes(new byte[][]
        {
            type,
            blocknumber,
            datas
        });
    }
    
}
