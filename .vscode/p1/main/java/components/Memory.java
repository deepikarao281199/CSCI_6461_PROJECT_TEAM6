package components;

/**
 * Memory Class - Defining the memory block here
 *
 * @author Avish Kaushik
 * @version 1.0
 */
public class Memory
{
    public short[] Data;
    static int MEMORY_SIZE = 1024 * 2;
    public Memory(){

        Data = new short[MEMORY_SIZE];
        for(int i=0;i<MEMORY_SIZE;i++){
            Data[i] = 0;
        }
    }
}