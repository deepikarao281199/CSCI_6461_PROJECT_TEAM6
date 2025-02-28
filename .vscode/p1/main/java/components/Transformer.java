package components;

/**
 *Trtransform perform numerical.
 */
public class Transformer
{
    public Transformer(){
    }
    /**
     * Transform Decimal number to Binary number.
     */
    public void DecimalToBinary(short decimal, char[] pin,int cout){
        int i=0;
        int j=decimal;
        if(j<0){
            pin[0]=1;
            j+=Short.MAX_VALUE+1;
        }
        while(j>=0){
            if((j & 1) == 1)
                pin[cout -1 -i] = 1;
            else
                pin[cout -1 -i] = 0;
            j >>=1;
            i++;
            if(decimal <0) pin[0]=1;
            if(i==cout) break;
        }
    }
    /**
     * Transform Hex number to Decimal number.
     */
    public short HexToDecimal(String string){
        String strings = "0123456789ABCDEF";
        string = string.toUpperCase();
        short output = 0;
        for (int i = 0; i < string.length(); i++) {
            char s = string.charAt(i);
            short j = (short)strings.indexOf(s);
            output = (short)(16*output + j);
        }
        return output;
    }

    /**
     * Transform Octal number to Decimal number.
     */
    public short OctToDecimal(String string){
        String strings = "01234567";
        string = string.toUpperCase();
        short output = 0;
        for (int i = 0; i < string.length(); i++) {
            char s = string.charAt(i);
            short j = (short)strings.indexOf(s);
            output = (short)(8*output + j);
        }
        return output;
    }
    /**
     * Transform Binary number to Decimal number.
     */
    public short BinaryToDecimal(char[] pin, int cout){
        short output=0;
        short start=1;
        for(int i=0; i <cout ; i++){
            if(pin[cout-1-i]==1)
                output += start;
            start *= 2;
        }
        return output;
    }

}
