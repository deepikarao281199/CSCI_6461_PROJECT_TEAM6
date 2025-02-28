package components;

import java.util.Arrays;

/**
 * CPU Class - Defining CPU and its operations.
 * Executing the instructions. Handling registers, memory operations and memory faults.
 */
public class CPU extends Transformer {

    static final short LOAD_REGISTER_OPCODE = 0x01;
    static final short STORE_REGISTER_OPCODE = 0x02;
    static final short LOAD_ADDRESS_OPCODE = 0x03;
    static final short LOAD_INDEX_OPCODE = 0x21;
    static final short STORE_INDEX_OPCODE = 0x22;
    public static final short HALT_OPCODE = 0x00;
    
    public char[] gpr0Register;
    public char[] gpr1Register;
    public char[] gpr2Register;
    public char[] gpr3Register;
    public char[] instructionRegister;    
    public char[] memoryFaultRegister;
    public char[] memoryBufferRegister;
    public char[] memoryAddressRegister;
    public char[] conditionCodeRegister;
    public char[] indexRegister1, indexRegister2, indexRegister3;

    public char[] programCounter;

    public char[] getGeneralPurposeRegister(short registerCode) {
        switch (registerCode) {
            case 1:
                return gpr1Register;
            case 2:
                return gpr2Register;
            case 3:
                return gpr3Register;
            default:
                return gpr0Register;
        }
    }

    public void setProgramCounter(short value) {
        DecimalToBinary(value, programCounter, 12);
    }

    public void setMemoryAddressRegister(short value) {
        DecimalToBinary(value, memoryAddressRegister, 12);
    }

    public void setMemoryBufferRegister(char array[], int length) {
        SrcToDes(array, memoryBufferRegister, 16);
    }

    public void setGpr0Register(short value) {
        DecimalToBinary(value, gpr0Register, 16);
    }

    public void setGpr1Register(char[] array, int length) {
        SrcToDes(array, gpr1Register, 16);
    }

    public void setGpr2Register(char[] array, int length) {
        SrcToDes(array, gpr2Register, 16);
    }

    public void setGpr3Register(char[] array, int length) {
        SrcToDes(array, gpr3Register, 16);
    }

    public void setIndexRegister1(char[] array, int length) {
        SrcToDes(array, indexRegister1, 16);
    }

    public void setIndexRegister2(char[] array, int length) {
        SrcToDes(array, indexRegister2, 16);
    }

    public void setIndexRegister3(char[] array, int length) {
        SrcToDes(array, indexRegister3, 16);
    }

    /**
     * CPU class - Initializing registers and memory addresses
     */
    public CPU() {
        gpr0Register = new char[16];
        gpr1Register = new char[16];
        gpr2Register = new char[16];
        gpr3Register = new char[16];
        indexRegister1 = new char[16];
        indexRegister2 = new char[16];
        indexRegister3 = new char[16];
        instructionRegister = new char[16];
        memoryFaultRegister = new char[4];
        memoryBufferRegister = new char[16];
        memoryAddressRegister = new char[12];
        conditionCodeRegister = new char[4];

        programCounter = new char[12];

        for (int i = 0; i < 4; i++) {
            memoryFaultRegister[i] = 0;
            conditionCodeRegister[i] = 0;
        }

        for (int i = 0; i < 12; i++) {
            programCounter[i] = 0;
            memoryAddressRegister[i] = 0;
        }

        for (int i = 0; i < 16; i++) {
            gpr0Register[i] = 0;
            gpr1Register[i] = 0;
            gpr2Register[i] = 0;
            gpr3Register[i] = 0;
            indexRegister1[i] = 0;
            indexRegister2[i] = 0;
            indexRegister3[i] = 0;
            instructionRegister[i] = 0;
            memoryBufferRegister[i] = 0;
        }
        
    }
    
    /**
     * LoadRegister - Loading registers from effective addresses
     */
    private void LoadRegister(char[] registerCode, short effectiveAddress, Memory memory) {
        byte value = (byte) BinaryToDecimal(registerCode, 2);
        DecimalToBinary(effectiveAddress, memoryAddressRegister, 12);
        DecimalToBinary(memory.Data[effectiveAddress], memoryBufferRegister, 16);
        switch (value) {
            case 0:
                SrcToDes(memoryBufferRegister, gpr0Register, 16);
                break;
            case 1:
                SrcToDes(memoryBufferRegister, gpr1Register, 16);
                break;
            case 2:
                SrcToDes(memoryBufferRegister, gpr2Register, 16);
                break;
            case 3:
                SrcToDes(memoryBufferRegister, gpr3Register, 16);
                break;
        }
    }

    /**
     * LoadIndexRegister - Loading index registers from effective addresses
     */
    private void LoadIndexRegister(char[] indexRegisterCode, short effectiveAddress, Memory memory) {
        byte value = (byte) BinaryToDecimal(indexRegisterCode, 2);
        DecimalToBinary(effectiveAddress, memoryAddressRegister, 12);
        DecimalToBinary(memory.Data[effectiveAddress], memoryBufferRegister, 16);
        switch (value) {
            case 1:
                SrcToDes(memoryBufferRegister, indexRegister1, 16);
                break;
            case 2:
                SrcToDes(memoryBufferRegister, indexRegister2, 16);
                break;
            case 3:
                SrcToDes(memoryBufferRegister, indexRegister3, 16);
                break;
            default:
                break;
        }
    }

    /**
     * SrcToDes - Copy source array to destination array
     * @param src - Source array
     * @param des - Destination array
     * @param len - Length of array
     */
    public void SrcToDes(char[] src, char[] des, int len) {
        if (len >= 0) System.arraycopy(src, 0, des, 0, len);
    }

    /**
     * ReverseSrcToDes - Copy source array to destination array with reverse order
     * @param src - Source array
     * @param des - Destination array
     * @param length - Length of array
     * @param srcLenth - Length of source array
     */
    public void ReverseSrcToDes(char[] src, char[] des, int length, int srcLenth) {
        for (int i = 0; i < srcLenth; i++)
            des[length - i - 1] = src[srcLenth - i - 1];
    }


    /**
     * StoreRegister - Store registers to memory
     */
    private void StoreRegister(char[] registerCode, short effectiveAddress, Memory memory) {
        byte RVal = (byte) BinaryToDecimal(registerCode, 2);
        char[] R_x = getGeneralPurposeRegister((short) RVal);
        DecimalToBinary(effectiveAddress, memoryAddressRegister, 12);
        SrcToDes(R_x, memoryBufferRegister, 16);
        if (effectiveAddress >= 0 && effectiveAddress <= 9) {
            memoryFaultRegister[3] = 1;
            memory.Data[4] = BinaryToDecimal(programCounter, 12);
            return;
        }
        memory.Data[effectiveAddress] = BinaryToDecimal(memoryBufferRegister, 16);
    }

    /**
     * StoreIndexRegister - Stores index registers to memory
     */
    private void StoreIndexRegister(char[] indexRegisterCode, short effectiveAddress, Memory memory) {
        byte XVal = (byte) BinaryToDecimal(indexRegisterCode, 2);
        DecimalToBinary(effectiveAddress, memoryAddressRegister, 12);
        switch (XVal) {
            case 0:
                break;
            case 1:
                SrcToDes(indexRegister1, memoryBufferRegister, 16);
                break;
            case 2:
                SrcToDes(indexRegister2, memoryBufferRegister, 16);
                break;
            case 3:
                SrcToDes(indexRegister3, memoryBufferRegister, 16);
                break;
        }
        if (effectiveAddress >= 0 && effectiveAddress <= 9) {
            memoryFaultRegister[3] = 1;
            memory.Data[4] = BinaryToDecimal(programCounter, 12);
            return;
        }
        memory.Data[effectiveAddress] = BinaryToDecimal(memoryBufferRegister, 16);
    }

    /**
     * StoreRegisterwithEA - Store registers to effective addresses
     */
    private void StoreRegisterwithEA(char[] registerCode, short effectiveAddress) {
        byte value = (byte) BinaryToDecimal(registerCode, 2);
        char[] R_x = getGeneralPurposeRegister((short) value);
        DecimalToBinary(effectiveAddress, memoryAddressRegister, 12);
        ReverseSrcToDes(memoryAddressRegister, R_x, 16, 12);
    }

    /**
     * Reset - Initialize CPU and memory
     */
    public void Reset(Memory memory) {
        for (int i = 0; i < 16; i++) {
            gpr0Register[i] = 0;
            gpr1Register[i] = 0;
            gpr2Register[i] = 0;
            gpr3Register[i] = 0;
            indexRegister1[i] = 0;
            indexRegister2[i] = 0;
            indexRegister3[i] = 0;
            instructionRegister[i] = 0;
            memoryBufferRegister[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            memoryAddressRegister[i] = 0;
            programCounter[i] = 0;
        }
        for (int i = 0; i < 4; i++) {
            memoryFaultRegister[i] = 0;
            conditionCodeRegister[i] = 0;
        }
        Arrays.fill(memory.Data, (short) 0);
    }

    /**
     * GetEffectiveAddress - Get the effective address
     * @param I - I's bit
     */
    private short GetEffectiveAddress(char[] indexRegisterCode, char[] address, Memory memory, char I) {
        byte IX_Val = (byte) BinaryToDecimal(indexRegisterCode, 2);
        short effectiveAddress = BinaryToDecimal(address, 5);
        switch (IX_Val) {
            case 0:
                break;
            case 1:
                effectiveAddress += BinaryToDecimal(indexRegister1, 16);
                break;
            case 2:
                effectiveAddress += BinaryToDecimal(indexRegister2, 16);
                break;
            case 3:
                effectiveAddress += BinaryToDecimal(indexRegister3, 16);
                break;
            default:
                break;
        }
        if (I == 1) {
            memory.Data[6] = effectiveAddress;
            return memory.Data[effectiveAddress];
        }
        return effectiveAddress;
    }

    /**
     * Execute - Execute instruction
     */
    public void Execute(Memory memory) {
        char[] shiftCount = new char[4];
        System.arraycopy(instructionRegister, 12, shiftCount, 0, 4);
        char[] binaryOpCode = new char[6];
        System.arraycopy(instructionRegister, 0, binaryOpCode, 0, 6);
        char[] registerCode = new char[2];
        System.arraycopy(instructionRegister, 6, registerCode, 0, 2);
        char[] memoryAddress = new char[5];
        System.arraycopy(instructionRegister, 11, memoryAddress, 0, 5);
        char[] indexRegisterCode = new char[2];
        System.arraycopy(instructionRegister, 8, indexRegisterCode, 0, 2);
        char indirectFlag = instructionRegister[10];
        short operationCode = BinaryToDecimal(binaryOpCode, 6); // Converts binary opcode to decimal OpCode Value
        short effectiveAddress = GetEffectiveAddress(indexRegisterCode, memoryAddress, memory, indirectFlag);
        
        try {
            switch (operationCode) {
                case HALT_OPCODE:
                    break;
                case LOAD_REGISTER_OPCODE:
                    LoadRegister(registerCode, effectiveAddress, memory);
                    break;
                case STORE_REGISTER_OPCODE:
                    StoreRegister(registerCode, effectiveAddress, memory);
                    break;
                case LOAD_ADDRESS_OPCODE:
                    StoreRegisterwithEA(registerCode, effectiveAddress);
                    break;
                case LOAD_INDEX_OPCODE:
                    LoadIndexRegister(indexRegisterCode, effectiveAddress, memory);
                    break;
                case STORE_INDEX_OPCODE:
                    StoreIndexRegister(indexRegisterCode, effectiveAddress, memory);
                    break;
                default:
                    memoryFaultRegister[1] = 1;
                    memory.Data[4] = BinaryToDecimal(programCounter, 12);
                    break;
            }
        } 
        
        catch (IndexOutOfBoundsException ioobe) {
            memoryFaultRegister[0] = 1;
            memory.Data[4] = BinaryToDecimal(programCounter, 12);
        }

        memory.Data[1] = BinaryToDecimal(memoryFaultRegister, 4);
    }

    /**
     * MemoryFaultHandling - Handling memory fault
     */
    public void MemoryFaultHandling(Memory memory) {
        if (memoryFaultRegister[0] == 1) {
            DecimalToBinary((short) 10, programCounter, 12);
        }
        memoryFaultRegister[0] = 0;
        memoryFaultRegister[1] = 0;
        memoryFaultRegister[2] = 0;
        memoryFaultRegister[3] = 0;
        memory.Data[4] = BinaryToDecimal(programCounter, 12);
    }
}
