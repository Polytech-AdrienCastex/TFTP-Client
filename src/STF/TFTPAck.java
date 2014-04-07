/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package STF;

import static STF.TFTPMessage.GetUShortFromBytes;
import javax.crypto.IllegalBlockSizeException;

/**
 *
 * @author A
 */
public class TFTPAck extends TFTPMessage
{
    public TFTPAck(int blockNumber)
    {
        this.blockNumber = blockNumber;
    }
    TFTPAck(byte[] datas) throws WrongDataFormatException
    {
        if(datas.length != 4)
            throw new WrongDataFormatException();
        
        this.blockNumber = GetUShortFromBytes(datas, 2);
    }
    
    private int blockNumber;
    public int getBlockNumber()
    {
        return blockNumber;
    }

    @Override
    public byte[] getDataFormated() throws Exception
    {
        byte[] type = this.type.getBytes();
        byte[] blocknumber = GetBytesFromUShort(getBlockNumber());

        return ConcatBytes(new byte[][]
        {
            type,
            blocknumber
        });
    }
}
