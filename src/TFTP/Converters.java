/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TFTP;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrien
 */
public class Converters
{
    public static String GetStringFromSTFTPtring(byte[] datas, int offset)
    {
        int end;
        for(end = offset; end < datas.length && datas[end] != 0; end++)
            ;
        int len = end - offset;
        
        if(end == datas.length) // Pas de 0 pour terminer la chaine = erreur
            return null;
        
        try
        {
            return new String(datas, offset, len, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }
    public static byte[] GetTFTPStringFromString(String str)
    {
        try
        {
            if(str == null)
                return new byte[] { 0 };
            
            byte[] strByteArray = str.getBytes("UTF-8");
            byte[] datas = new byte[strByteArray.length + 1]; // + 1 pour le 0 de fin
            
            System.arraycopy(strByteArray, 0, datas, 0, strByteArray.length);
            datas[datas.length - 1] = 0;
            
            return datas;
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }
    
    
    
    public static byte[] GetBytesFromUShort(int value)
    {
        byte[] datas = new byte[2];
        datas[0] = (byte)((value >> 8) & 0xff); // Poids fort
        datas[1] = (byte)(value & 0xff); // Poids faible
        
        return datas;
    }
    public static int GetUShortFromBytes(byte[] datas, int offset)
    {
        int value;
        
        value = (int)datas[offset + 1]; // Poids faible
        value += (int)datas[offset] << 8; // Poids fort
        
        return value;
    }
    
    
    public static byte[] ConcatBytes(byte[][] array)
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
}
