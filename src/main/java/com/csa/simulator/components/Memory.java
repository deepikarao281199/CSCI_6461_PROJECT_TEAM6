package com.csa.simulator.components;

/**
 * Memory Class - Defining the memory block here
 *
 * Authored by Owen
 * version 1.0
 */
public class Memory
{
    public short[] data; // Data is Storing an array of words
    /**
     * 4KB Memory - 1024 * 2 (Memory of 2048 Words)
     * 8KB Memory - 1024 * 4 (Memory of 4096 Words)
     **/
    static int MEMORY_SIZE2 = 1024 * 2;
    static int MEMORY_SIZE4 = 1024 * 4;
    public Memory(){

        data = new short[MEMORY_SIZE2];
        /** ^ - Java Does not support unsigned primitive type
         * so we are expanding with int to cover 
         * max unsigned value that can be stored in a largest 16bit number.
         * Value resets to 0
         **/
        for(int i=0;i<MEMORY_SIZE2;i++){
            data[i] = 0;
        }
    }
    /**
     * Reset the Memory
     */
    public void Reset(){
        for(int i=0;i<MEMORY_SIZE2;i++){
            data[i] = 0;
        }
    }
}
