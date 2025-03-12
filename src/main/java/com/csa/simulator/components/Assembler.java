package com.csa.simulator.components;

public class Assembler extends Transformer {
    public String getOpCode(String op){
        return switch (op) {
            case "LOAD_REGISTER_OPCODE" -> "000001";
            case "STORE_REGISTER_OPCODE" -> "000010";
            case "LOAD_ADDRESS_OPCODE" -> "000011";
            case "LOAD_INDEX_OPCODE" -> "100001";
            case "STORE_INDEX_OPCODE" -> "101010";
            case "jumpIfZero" -> "001000";
            case "jumpIfNotEq" -> "001001";
            case "jumpIfConditionCode" -> "001010";
            case "unCondJumpToAdd" -> "001011";
            case "jumpS" -> "001100";
            case "RFS" -> "001101";
            case "jumpGreaterThanEq" -> "001111";
            case "AddMR" -> "000100";
            case "SubMR" -> "000101";
            case "AddIR" -> "000110";
            case "SubIR" -> "000111";
            default -> "none";
        };
    }
}