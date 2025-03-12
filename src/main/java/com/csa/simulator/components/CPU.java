package com.csa.simulator.components;

import java.io.IOException;
import java.util.Arrays;

/**
 CPU Class - Definition of the CPU functionality
 * This class encompasses the CPU along with its operations
 * It is responsible for executing instructions and managing memory errors
 * Additionally, the CPU class handles register operations and memory management
 */
public class CPU extends Transformer {
    public char[] gpr0Register;
    public char[] gpr1Register;
    public char[] gpr2Register;
    public char[] gpr3Register;
    public char[] programCounter;
    /** Condition Code_Instruction Register_Memory Address Register_Memory Buffer Register **/
    public char[] conditionCodeRegister;
    public char[] instructionRegister;
    public char[] memoryAddressRegister;
    public char[] memoryBufferRegister;
    /** Memory Fault Register_General Purpose Register_Index Registers **/
    public char[] memoryFaultRegister;
    public char[] R0,R1,R2,R3;
    public char[] indexRegister1,indexRegister2,indexRegister3;
    private final Modules dev;
    /*Floating Point Registers */
    public FloatingPointRegisters FR;
    public Cache reserveCache;
    /**OpCode Inst*/
    public static final short HALT_OPCODE = 0x00;
    static final short trapCode = 0x18;
    static final short LOAD_REGISTER_OPCODE = 0x01;
    /**
     * Save Register Data to Memory Location
     * The Effective Address value is assigned
     * as the stored value in the register
     */
    static final short STORE_REGISTER_OPCODE = 0x02;
    static final short LOAD_ADDRESS_OPCODE = 0x03;
    static final short LOAD_INDEX_OPCODE = 0x21;
    static final short STORE_INDEX_OPCODE = 0x22;
    static final short AddMR = 0x04; // Add Memory To Register
    static final short SubMR = 0x05; // Subtract Memory From Register
    static final short AddIR = 0x06; // Add  Immediate to Register
    static final short SubIR = 0x07; // Subtract Immediate to Register
    static final short VecADD = 0x1D; // Vector Addition
    static final short VecSUB = 0x1E; // Vector Subtraction

    static final short jumpIfZero = 0x08;
    static final short jumpIfNotEq = 0x09;
    static final short jumpIfConditionCode = 0x0A;
    static final short unCondJumpToAdd = 0x0B;
    /**Jump and Save return address*/
    static final short jumpS = 0x0C;
    static final short RFS = 0x0D;
    static final short subOneAndBrReg = 0x0E;
    static final short jumpGreaterThanEq = 0x0F;
    /**Multiplication*/
    static final short MLT = 0x10;
    /**Division*/
    static final short DIV = 0x11;
    static final short regEqInst = 0x12;
    /**AND operator*/
    static final short AND = 0x13;
    /**OR Operator*/
    static final short ORR = 0x14;
    /**Not Operator*/
    static final short NOT = 0x15; // BitWise NOT Operator
    static final short shiftRegCount = 0x19; // Shift Register by Count
    /**Rotate Register by Count*/
    static final short rotRegCount = 0x1A; // Rotate Register by Count
    /**Read*/
    static final short IN = 0x31;
    /**Write*/
    static final short OUT = 0x32;
    /**Check Status*/
    static final short checkDevSt = 0x33;
    static final short floatAdd = 0x1B;
    static final short floatSub = 0x1C;
    static final short convertFixed = 0x1F;
    static final short loadFloatReg = 0x28;
    static final short storeFloatReg = 0x29;
    /**CPU Initialization*/
    public CPU(Modules dev)
    {
        this.dev = dev;
        gpr0Register = new char[16];
        gpr1Register = new char[16];
        gpr2Register = new char[16];
        gpr3Register = new char[16];
        FR = new FloatingPointRegisters();
        programCounter = new char[12];
        conditionCodeRegister = new char[4];
        instructionRegister = new char[16];
        memoryAddressRegister = new char[12];
        memoryBufferRegister = new char[16];
        memoryFaultRegister = new char[4];
        indexRegister1 = new char[16];
        indexRegister2 = new char[16];
        indexRegister3 = new char[16];
        R0 = new char[16];
        R1 = new char[16];
        R2 = new char[16];
        R3 = new char[16];
        for (int i = 0; i < 16; i++) {
            gpr0Register[i] = 0;
            gpr1Register[i] = 0;
            gpr2Register[i] = 0;
            gpr3Register[i] = 0;
            indexRegister1[i] = 0;
            indexRegister2[i] = 0;
            indexRegister3[i] = 0;
            memoryBufferRegister[i] = 0;
            instructionRegister[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            programCounter[i] = 0;
            memoryAddressRegister[i] = 0;
        }
        for(int i=0;i<4;i++){
            conditionCodeRegister[i] = 0;
            memoryFaultRegister[i] = 0;
        }
        for(int i=0;i<16;i++){
            instructionRegister[i] = 0;
            memoryBufferRegister[i] = 0;
            indexRegister1[i] = indexRegister2[i] = indexRegister3[i] = 0;
            R0[i] = R1[i] = R2[i] = R3[i] = 0;
        }
        reserveCache = new Cache();
    }


    /**
     * CopyArray_src_dest - Copies the source array to the destination array
     * src - Source array
     * des - Destination array
     * len - Length of the array
     */
    public void CopyArr(char[] src, char[] des, int len) {
        if (len >= 0) System.arraycopy(src, 0, des, 0, len);
    }
    private short FetchEA(char[] ix, char[] addr, Memory m, char I){
        byte IXVal = (byte)BinaryToDecimal(ix,2);
        short EA = BinaryToDecimal(addr,5);
        switch(IXVal){
            case 0:
                break;
            case 1:
                EA += BinaryToDecimal(indexRegister1,16);
                break;
            case 2:
                EA += BinaryToDecimal(indexRegister2,16);
                break;
            case 3:
                EA += BinaryToDecimal(indexRegister3,16);
                break;
            default:
                break;
        }
        if(I==1) {
            m.data[6] = EA;
            return m.data[EA];
        }
        return EA;
    }
    private void StoreRegister(char[] rx, short EA, Memory m){
        byte RVal= (byte)BinaryToDecimal(rx,2);
        DecimalToBinary(EA,memoryAddressRegister,12);
        DecimalToBinary(m.data[EA],memoryBufferRegister,16);
        switch(RVal){
            case 0:
                CopyArr(memoryBufferRegister, R0, 16);
                break;
            case 1:
                CopyArr(memoryBufferRegister, R1, 16);
                break;
            case 2:
                CopyArr(memoryBufferRegister, R2, 16);
                break;
            case 3:
                CopyArr(memoryBufferRegister, R3, 16);
                break;
        }
    }

    private void StoreIndexRegister(char[] ix, short EA, Memory m){
        byte RVal= (byte)BinaryToDecimal(ix,2);
        DecimalToBinary(EA,memoryAddressRegister,12);
        DecimalToBinary(m.data[EA],memoryBufferRegister,16);
        switch(RVal){
            case 1:
                CopyArr(memoryBufferRegister, indexRegister1, 16);
                break;
            case 2:
                CopyArr(memoryBufferRegister, indexRegister2, 16);
                break;
            case 3:
                CopyArr(memoryBufferRegister, indexRegister3, 16);
                break;
            default: break;
        }
    }

    private void MemStore(char[] rx, short EA, Memory m){
        byte RVal = (byte)BinaryToDecimal(rx,2);
        char[] R_x = getRegister(RVal);
        DecimalToBinary(EA,memoryAddressRegister,12);
        CopyArr(R_x, memoryBufferRegister, 16);
        if(EA>=0 && EA<=9) {
            memoryFaultRegister[3]=1;
            m.data[4] = BinaryToDecimal(programCounter, 12);
            return;
        }
        m.data[EA] = BinaryToDecimal(memoryBufferRegister,16);
    }

    /**Store memory from index register*/
    private void MemStoreFromIndex(char[] ix, short EA, Memory m){
        byte XVal = (byte)BinaryToDecimal(ix,2);
        DecimalToBinary(EA,memoryAddressRegister,12);
        switch(XVal){
            case 0:
                break;
            case 1:
                CopyArr(indexRegister1, memoryBufferRegister, 16);
                break;
            case 2:
                CopyArr(indexRegister2, memoryBufferRegister, 16);
                break;
            case 3:
                CopyArr(indexRegister3, memoryBufferRegister, 16);
                break;
        }
        if(EA>=0 && EA<=9) {
            memoryFaultRegister[3]=1;
            m.data[4] = BinaryToDecimal(programCounter, 12);
            return;
        }
        m.data[EA] = BinaryToDecimal(memoryBufferRegister,16);
    }
    private void StoreRegisterEA(char[] rx, short EA){
        byte RVal= (byte)BinaryToDecimal(rx,2);
        char[] R_x = getRegister(RVal);
        DecimalToBinary(EA,memoryAddressRegister,12);
        ReverseCopyArr(memoryAddressRegister, R_x, 16, 12);
    }
    public void ReverseCopyArr(char[] src, char[] des, int length, int srclen){
        for(int i=0;i<srclen;i++)
            des[length-i-1] = src[srclen-i-1];
    }
    public void Reset(Memory m){
        for(int i=0;i<12;i++){
            programCounter[i] = memoryAddressRegister[i] = 0;
        }
        for(int i=0;i<4;i++){
            conditionCodeRegister[i] = 0;
            memoryFaultRegister[i] = 0;
        }
        for(int i=0;i<16;i++){
            instructionRegister[i] = 0;
            memoryBufferRegister[i] = 0;
            indexRegister1[i] = indexRegister2[i] = indexRegister3[i] = 0;
            R0[i] = R1[i] = R2[i] = R3[i] = 0;
        }
        Arrays.fill(m.data, (short) 0);
    }

    public void Execute(Memory m, Modules d){
        char[] InstOp = new char[6];
        System.arraycopy(instructionRegister, 0, InstOp, 0, 6);
        char[] RX = new char[2];
        System.arraycopy(instructionRegister, 6, RX, 0, 2);
        char[] IX = new char[2];
        System.arraycopy(instructionRegister, 8, IX, 0, 2);
        char I = instructionRegister[10];
        char[] Address = new char[5];
        System.arraycopy(instructionRegister, 11, Address, 0, 5);
        char[] Count = new char[4];
        System.arraycopy(instructionRegister, 12, Count, 0, 4);
        short OpCode = BinaryToDecimal(InstOp,6); // Fetch OpCode Value
        System.out.printf("OpCode: 0x%-2x\n",OpCode);
        try {
            dev.printCache(reserveCache);
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        short EA=FetchEA(IX,Address,m,I);
        try{
            switch(OpCode){
                case HALT_OPCODE: break;
                case LOAD_REGISTER_OPCODE: StoreRegister(RX,EA,m); break;
                case STORE_REGISTER_OPCODE: MemStore(RX,EA,m); break;
                case LOAD_ADDRESS_OPCODE: StoreRegisterEA(RX,EA); break;
                case LOAD_INDEX_OPCODE: StoreIndexRegister(IX, EA, m); break;
                case STORE_INDEX_OPCODE: MemStoreFromIndex(IX, EA, m); break;
                case AddMR: fAddMR(BinaryToDecimal(RX, 2), EA,m); break;
                case SubMR: fSubMR(BinaryToDecimal(RX, 2), EA,m); break;
                case AddIR: fAddIR(BinaryToDecimal(RX, 2), BinaryToDecimal(Address, 5)); break;
                case SubIR: fSubIR(BinaryToDecimal(RX, 2), BinaryToDecimal(Address, 5)); break;
                case jumpIfZero: JumpZero((BinaryToDecimal(RX, 2)),EA); break;
                case jumpIfNotEq: JumpIfNotEqual((BinaryToDecimal(RX, 2)),EA); break;
                case jumpIfConditionCode: JumpIfCond(BinaryToDecimal(RX, 2),EA); break;
                case unCondJumpToAdd: UncondJump(EA); break;
                case jumpS: JumpSubRoutine(EA); break;
                case RFS: RFSImmed(Address); break;
                case subOneAndBrReg: SubandBranch(BinaryToDecimal(RX, 2), EA); break;
                case jumpGreaterThanEq: JumpGE(BinaryToDecimal(RX, 2), EA); break;
                case MLT: fMLT(BinaryToDecimal(RX, 2), BinaryToDecimal(IX, 2)); break;
                case DIV: fDVD(BinaryToDecimal(RX, 2), BinaryToDecimal(IX, 2)); break;
                case regEqInst: fTRR(BinaryToDecimal(RX, 2), BinaryToDecimal(IX, 2)); break;
                case AND: fAND(BinaryToDecimal(RX, 2), BinaryToDecimal(IX, 2)); break;
                case ORR: fORR(BinaryToDecimal(RX, 2), BinaryToDecimal(IX, 2)); break;
                case NOT: fNOT(BinaryToDecimal(RX, 2)); break;
                case trapCode: fTrap(BinaryToDecimal(Count,4),m); break;
                case shiftRegCount: fSRC(BinaryToDecimal(RX, 2),BinaryToDecimal(Count, 4),(byte)instructionRegister[9],(byte)instructionRegister[8]); break;
                case rotRegCount: fRRC(BinaryToDecimal(RX, 2),BinaryToDecimal(Count, 4),(byte)instructionRegister[9],(byte)instructionRegister[8]); break;
                case IN: fIN(BinaryToDecimal(RX, 2), (byte)BinaryToDecimal(Address,5), dev); break;
                case OUT:
                    fOUT(BinaryToDecimal(RX, 2), (byte)BinaryToDecimal(Address,5), dev); break;
                case checkDevSt:
                    fCHK(BinaryToDecimal(RX, 2), (byte)BinaryToDecimal(Address, 5), dev); break;
                case floatAdd: FloatAdd(BinaryToDecimal(RX, 2), EA, m); break;
                case floatSub: FloatSub(BinaryToDecimal(RX, 2), EA, m); break;
                case VecADD: VectorAdd(BinaryToDecimal(RX, 2), EA, m); break;
                case VecSUB: VectorSub(BinaryToDecimal(RX, 2), EA, m); break;
                case convertFixed: ConvertFloatFixed(BinaryToDecimal(RX, 2), EA, m); break;
                case loadFloatReg: LoadFloatRegister(BinaryToDecimal(RX, 2), EA, m); break;
                case storeFloatReg: StoreFloatRegister(BinaryToDecimal(RX, 2), EA, m); break;
                default:
                    memoryFaultRegister[1]=1;
                    m.data[4] = BinaryToDecimal(programCounter, 12);
                    break;
            }
        }catch(IndexOutOfBoundsException ioobe){
            memoryFaultRegister[0]=1;
            m.data[4] = BinaryToDecimal(programCounter, 12);
        }
        m.data[1] = BinaryToDecimal(memoryFaultRegister, 4);
    }
    /**Handle Machine Fault*/
    public void MFHandle(Memory m){
        if(memoryFaultRegister[0]==1){
            DecimalToBinary((short)10, programCounter, 12);
        }
        memoryFaultRegister[0] = memoryFaultRegister[1] = memoryFaultRegister[2] = memoryFaultRegister[3] = 0;
        m.data[4] = BinaryToDecimal(programCounter, 12);
    }

    /**
     * Jump if zero rx Register to check for JMP condition*/
    public void JumpZero(short rx,short EA){
        short val;char[] R_x=getRegister(rx);
        val = BinaryToDecimal(R_x, 16);
        if(val==0){
            DecimalToBinary(EA, programCounter, 12);
        }else {
            DecimalToBinary((short)
                    (BinaryToDecimal(programCounter, 12)+1), programCounter, 12);
        }

    }
    /**
     * Jump if Not Equal to if True
     * rx Register to check for JMP condition
     * EA Effective Address to programCounter
     */
    public void JumpIfNotEqual(short rx,short EA){
        short val;
        char[] R_x = getRegister(rx);
        val=BinaryToDecimal(R_x, 16);
        if (val != 0){
            DecimalToBinary(EA, programCounter, 12);
        } else {
            DecimalToBinary((short)
                    (BinaryToDecimal(programCounter, 12)+1), programCounter, 12);
        }
    }
    /**Jump if condition code*/
    public void JumpIfCond(short cc,short EA){
        if ( conditionCodeRegister[cc] == 1){
            DecimalToBinary(EA, programCounter, 12);
            conditionCodeRegister[cc]=0;
        } else {
            DecimalToBinary((short)
                    (BinaryToDecimal(programCounter, 12)+1), programCounter, 12);
        }
    }

    public void UncondJump(short EA){
        DecimalToBinary(EA, programCounter, 12);
    }
    /* End Implementation of OpCode Method - Natalie Jordan */
    /* Implementation of OpCode Method - AlHassan Halawani */
    public void JumpSubRoutine(short EA){
        DecimalToBinary(
                (short)(BinaryToDecimal(programCounter, 12)+1),
                R3, 16);
        DecimalToBinary(EA, programCounter, 12);
        DecimalToBinary((short)(EA+1), R0, 16);

    }
    public void RFSImmed(char[] Addr){
        ReverseCopyArr(Addr, R0, 16, 5);
        ReverseCopyArr(R3, programCounter, 12, 16);
    }
    public void SubandBranch(short rx,short EA){
        short val,flag=0;
        char[] R_x = getRegister(rx);
        val=BinaryToDecimal(R_x, 16);
        if(val>0)flag=1;
        if(flag==1) DecimalToBinary(EA, programCounter, 12);
        else DecimalToBinary((short)(BinaryToDecimal(programCounter, 12)+1),
                programCounter, 12);
    }
    /**Jump Greater than equal to if True*/
    public void JumpGE(short rx,short EA){
        char[] R_x = getRegister(rx);
        short val=BinaryToDecimal(R_x, 16);
        if(val>=0){
            DecimalToBinary(EA, programCounter, 12);
        }else
            DecimalToBinary((short)(BinaryToDecimal(programCounter, 12)+1),
                    programCounter, 12);
    }

    public void fAddMR(short RVal,short EA, Memory m){
        char[] R_x = getRegister(RVal);
        short result = (short)(BinaryToDecimal(R_x, 16) + m.data[EA]);
        DecimalToBinary(result, R_x, 16);
    }
    public void fSubMR(short RVal,short EA, Memory m){
        char[] R_x = getRegister(RVal);
        short result = (short)(BinaryToDecimal(R_x, 16) - m.data[EA]);
        DecimalToBinary(result, R_x, 16);
    }
    public void fAddIR(short RVal,short Addr){
        char[] R_x = getRegister(RVal);
        short result = (short)(BinaryToDecimal(R_x, 16) + Addr);
        DecimalToBinary(result, R_x, 16);
    }
    public void fSubIR(short RVal,short Addr){
        char[] R_x = getRegister(RVal);
        short result = (short)(BinaryToDecimal(R_x, 16) - Addr);
        DecimalToBinary(result, R_x, 16);
    }

    public void VectorAdd(short fx, short EA, Memory m){
        short v1addr = m.data[EA];
        short v2addr = m.data[EA+1];
        System.out.println(v1addr+" "+v2addr);
        float frVal = FloatingPointRegisters.shortToFloat(GetFloatingRegister(fx));
        for(int i=0; i<(int)frVal; i++){
            m.data[v1addr+i] += m.data[v2addr+i];
            //System.out.println(m.data[v1addr+i]);
        }
    }
    public void VectorSub(short fx, short EA, Memory m){
        short v1addr = m.data[EA];
        short v2addr = m.data[EA+1];
        float frVal = FloatingPointRegisters.shortToFloat(GetFloatingRegister(fx));
        for(int i=0; i<frVal; i++)
            m.data[v1addr+i] -= m.data[v2addr+i];
    }

    /**Multiplication OpCode Method*/
    public void fMLT(short rx,short ry){
        if( rx%2==1 || ry%2==1) return ;
        if(rx==0){
            short result=BinaryToDecimal(R0, 16);
            if(ry==0) result *= result;
            else result *= BinaryToDecimal(R2, 16);
            DecimalToBinary(result, R1, 16);
        }else if(rx==2){
            short result = BinaryToDecimal(R2, 16);
            if(ry==0) result *= BinaryToDecimal(R0, 16);
            else result *= result;
            DecimalToBinary(result, R3, 16);
        }
    }
    /**Method for the Division OpCode Method*/
    public void fDVD(short rx,short ry){
        if( rx%2==1 || ry%2==1) return ;
        if(rx==0){
            short result=BinaryToDecimal(R0, 16);
            try{
                short rem=0;
                if(ry==0) {
                    rem+= (short) (result % BinaryToDecimal(R0,16));
                    result /= BinaryToDecimal(R0, 16);
                }
                else {
                    rem+= (short) (result % BinaryToDecimal(R2,16));
                    result /= BinaryToDecimal(R2, 16);
                }
                DecimalToBinary(rem, R1, 16);
                DecimalToBinary(result, R0, 16);
            }catch(ArithmeticException E){
                conditionCodeRegister[2]=1;
            }
        }else if(rx==2){
            short result=BinaryToDecimal(R2, 16);
            try{
                short rem=0;
                if(ry==0) {
                    rem+= (short) (result % BinaryToDecimal(R0,16));
                    result /= BinaryToDecimal(R0, 16);
                }
                else {
                    rem+= (short) (result % BinaryToDecimal(R2,16));
                    result /= BinaryToDecimal(R2, 16);
                }
                DecimalToBinary(rem, R2, 16);
                DecimalToBinary(result, R3, 16);
            }catch(ArithmeticException E){
                conditionCodeRegister[2]=1;
            }
        }
    }
    /**Equality OpCode Method*/
    public void fTRR(short rx,short ry){
        char[] R_x=getRegister(rx),
                R_y=getRegister(ry);
        short rxValue = BinaryToDecimal(R_x,16);
        short ryValue = BinaryToDecimal(R_y,16);
        if(rxValue==ryValue) conditionCodeRegister[3]=1;
    }
    /**AND Operator OpCode Method*/
    public void fAND(short rx,short ry){
        char[] R_x=getRegister(rx),
                R_y=getRegister(ry);
        for(int i=0;i<16;i++){
            R_x[i] = (char)(R_x[i] & R_y[i]);
        }
    }
    /**Method for the OR Operator OpCode Method*/
    public void fORR(short rx,short ry){
        char[] R_x=getRegister(rx),
                R_y=getRegister(ry);
        for(int i=0;i<16;i++){
            R_x[i] = (char)(R_x[i] | R_y[i]);
        }
    }
    public void fNOT(short rx){
        char[] R_x=getRegister(rx);
        for(int i=0;i<16;i++){
            if(R_x[i] == 0) R_x[i] = 1;
            else if(R_x[i] == 1) R_x[i] = 0;
        }
    }
    /**Trap Code Ist*/
    public void fTrap(short trapCode,Memory m){
        m.data[0]=trapCode;
        short Value = (short)(BinaryToDecimal(programCounter, 12)+1);
        m.data[2] = Value;
        //DecimalToBinary(m.data[2], programCounter, 12);
        // Handle Trap Code here
    }
    public void fSRC(short rx,short count,byte LR,byte AL){
        char[] R_x=getRegister(rx);
        short val = BinaryToDecimal(R_x, 16);
        if(AL==1){
            switch(LR){
                case 0:
                    val = (short)(val>>count); // Right Shift
                    DecimalToBinary(val, R_x, 16);
                    break;
                case 1:
                    val = (short)(val<<count); // Left Shift
                    DecimalToBinary(val, R_x, 16);
                    break;
            }
        }else{
            // Arithmetic Shift
            switch(LR){
                case 0:
                    val = (short)(val>>count); // Right Shift
                    DecimalToBinary(val, R_x, 16);
                    R_x[0] = R_x[1];
                    break;
                case 1:
                    val = (short)(val<<count); // Left Shift
                    DecimalToBinary(val, R_x, 16);
                    break;
            }
        }
    }
    /**
     * Rotate Register by Count
     */
    public void fRRC(short rx,short count,byte LR,byte AL){
        char[] R_x=getRegister(rx);
        //short val = BinaryToDecimal(R_x, 16);
        if(AL==1){
            switch(LR){
                case 0:
                    for(int i=0;i<16;i++){
                        char temp = R_x[(i+count)%16];
                        R_x[(i+count)%16] = R_x[i];
                        R_x[i] = temp;
                    }
                    break;
                case 1:
                    for(int i=15;i>=0;i++){
                        char temp = R_x[(i+count)%16];
                        R_x[(i+count)%16] = R_x[i];
                        R_x[i] = temp;
                    }
            }
        }
    }
    /**Read from Device to Register*/
    public void fIN(short rx, byte devId, Modules dev){
        char[] Rx = getRegister(rx);
        if (devId == 0) {
            dev.keyboard(Rx);
        }
    }
    /**Write from Register to Device*/
    public void fOUT(short rx, byte deviceId, Modules dev){
        char[] Rx = getRegister(rx);
        if (deviceId == 1) {
            dev.printer(Rx);
        }
    }
    /**Device ID Status check*/
    public void fCHK(short rx, byte deviceId, Modules dev){
        char[] Rx = getRegister(rx);
        switch(deviceId){
            case 0:
                DecimalToBinary((short)dev.printStat, Rx, 16);
                break;
            case 1:
                DecimalToBinary((short)dev.keyboardStatus(), Rx, deviceId);
                break;
        }
    }
    /**Floating Point Addition*/
    public void FloatAdd(short fx,short EA,Memory m){
        float value = FloatingPointRegisters.shortToFloat(m.data[EA]);
        switch(fx){
            case 0:
                float fr0 = FR.getFR0() + value;
                FR.setFR0(fr0);
                break;
            case 1:
                float fr1 = FR.getFR1() + value;
                FR.setFR0(fr1);
                break;
        }
    }
    /**Floating Point Subtraction*/
    public void FloatSub(short fx,short EA,Memory m){
        float value = FloatingPointRegisters.shortToFloat(m.data[EA]);
        switch(fx){
            case 0:
                float fr0 = FR.getFR0() - value;
                FR.setFR0(fr0);
                break;
            case 1:
                float fr1 = FR.getFR1() - value;
                FR.setFR0(fr1);
                break;
        }
    }
    /**Convert to Fixed/FloatingPoint*/
    public void ConvertFloatFixed(short rx,short EA,Memory m){
        char[] R_x = getRegister(rx);
        int F = BinaryToDecimal(R_x, 16);
        switch(F){
            case 0:
                DecimalToBinary(m.data[EA], R_x, 16);
                break;
            case 1:
                FR.setFR0(FloatingPointRegisters.shortToFloat(m.data[EA]));
        }
    }
    /**Load Floating Register From Memory*/
    public void LoadFloatRegister(short fx,short EA,Memory m){
        switch(fx){
            case 0:
                FR.setFR0(FloatingPointRegisters.shortToFloat(m.data[EA]));
                FR.setFR1(FloatingPointRegisters.shortToFloat(m.data[EA+1]));
                break;
            case 1:
                FR.setFR0(FloatingPointRegisters.shortToFloat(m.data[EA+1]));
                FR.setFR1(FloatingPointRegisters.shortToFloat(m.data[EA]));
                break;
        }
    }
    /**Store Floating Register To Memory*/
    public void StoreFloatRegister(short fx,short EA,Memory m){
        switch(fx){
            case 0:
                m.data[EA]=FR.FR0;
                m.data[EA+1]=FR.FR1;
                break;
            case 1:
                m.data[EA+1]=FR.FR0;
                m.data[EA]=FR.FR1;
                break;
        }
    }


    public char[] getinstructionRegister() {
        return instructionRegister;
    }
    public char[] getmemoryAddressRegister(){
        return memoryAddressRegister;
    }
    public void getR0(){
        for(int i=0;i<16;i++)
            System.out.printf("%d ",(int)R0[i]);
        System.out.println();
    }
    public char[] getR1() {
        return R1;
    }
    public char[] getR2(){
        return R2;
    }
    public char[] getR3() {
        return R3;
    }
    public void setinstructionRegister(char[] instructionRegister){
        System.arraycopy(instructionRegister, 0, this.instructionRegister, 0, 16);
    }
    public void setgpr0Register(short value){
        DecimalToBinary(value, R0, 16);
    }
    public void setgpr1Register(char[] arr, int len){
        CopyArr(arr,R1,16);
    }
    public void setgpr2Register(char[] arr, int len){
        CopyArr(arr,R2,16);
    }
    public void setgpr3Register(char[] arr, int len){
        CopyArr(arr,R3,16);
    }
    public void setindexRegister1(char[] arr, int len){
        CopyArr(arr,indexRegister1,16);
    }
    public void setindexRegister2(char[] arr, int len){
        CopyArr(arr,indexRegister2,16);
    }
    public void setindexRegister3(char[] arr, int len){
        CopyArr(arr,indexRegister3,16);
    }
    public void setprogramCounter(short value){
        DecimalToBinary(value, programCounter, 12);
    }
    public void setmemoryAddressRegister(short value){
        DecimalToBinary(value, memoryAddressRegister, 12);
    }
    public void setmemoryBufferRegister(char[] arr, int len){
        CopyArr(arr,memoryBufferRegister,16);
    }
    public char[] getmemoryBufferRegister(){
        return memoryBufferRegister;
    }
    public char[] getRegister(short rx){
        return switch (rx) {
            case 0 -> R0;
            case 1 -> R1;
            case 2 -> R2;
            case 3 -> R3;
            default -> R0;
        };
    }
    public short GetFloatingRegister(short fx){
        return switch (fx) {
            case 0 -> FR.FR0;
            case 1 -> FR.FR1;
            default -> FR.FR0;
        };
    }
}