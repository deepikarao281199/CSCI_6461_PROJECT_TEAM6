
/**
 * In this  java program, we utilized arrays, hashmaps and other functionalities to create a assembler.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Assembler {

    public static final HashMap<String, Integer> opcodeHashMapForArithmeticAndLogic = new HashMap<>();
    public static final HashMap<String, Integer> opcodeHashMapForShiftRotate = new HashMap<>();
    public static final HashMap<String, Integer> opcodeHashMapForFloatVector = new HashMap<>();
    public static final HashMap<String, Integer> opcodeHashMapForIO = new HashMap<>();
    public static final HashMap<String, Integer> opcodeHashMapForLSAndOther = new HashMap<>();
    public static final HashMap<String, Integer> opcodeHashMapForMisallaneous = new HashMap<>();

    static {
        // Initializing the opcode hashmap with instruction mnemonics and their
        // corresponding binary opcode representations.
        opcodeHashMapForMisallaneous.put("HLT", 000);
        opcodeHashMapForMisallaneous.put("TRAP", 045);
        opcodeHashMapForLSAndOther.put("LDR", 001);
        opcodeHashMapForLSAndOther.put("STR", 002);
        opcodeHashMapForLSAndOther.put("LDA", 003);
        opcodeHashMapForLSAndOther.put("LDX", 004);
        opcodeHashMapForLSAndOther.put("STX", 005);
        opcodeHashMapForLSAndOther.put("SETCCE", 036);
        opcodeHashMapForLSAndOther.put("JZ", 006);
        opcodeHashMapForLSAndOther.put("JNE", 007);
        opcodeHashMapForLSAndOther.put("JCC", 010);
        opcodeHashMapForLSAndOther.put("JMA", 011);
        opcodeHashMapForLSAndOther.put("JSR", 012);
        opcodeHashMapForLSAndOther.put("RFS", 013);
        opcodeHashMapForLSAndOther.put("SOB", 014);
        opcodeHashMapForLSAndOther.put("JGE", 015);
        opcodeHashMapForLSAndOther.put("AMR", 016);
        opcodeHashMapForLSAndOther.put("SMR", 017);
        opcodeHashMapForLSAndOther.put("AIR", 020);
        opcodeHashMapForLSAndOther.put("SIR", 021);
        opcodeHashMapForArithmeticAndLogic.put("MLT", 022);
        opcodeHashMapForArithmeticAndLogic.put("DVD", 023);
        opcodeHashMapForArithmeticAndLogic.put("TRR", 024);
        opcodeHashMapForArithmeticAndLogic.put("AND", 025);
        opcodeHashMapForArithmeticAndLogic.put("ORR", 026);
        opcodeHashMapForArithmeticAndLogic.put("NOT", 027);
        opcodeHashMapForShiftRotate.put("SRC", 030);
        opcodeHashMapForShiftRotate.put("RRC", 031);
        opcodeHashMapForIO.put("IN", 032);
        opcodeHashMapForIO.put("OUT", 033);
        opcodeHashMapForIO.put("CHK", 034);
        opcodeHashMapForFloatVector.put("FADD", 035);
        opcodeHashMapForFloatVector.put("FSUB", 036);
        opcodeHashMapForFloatVector.put("VADD", 037);
        opcodeHashMapForFloatVector.put("VSUB", 040);
        opcodeHashMapForFloatVector.put("CNVRT", 041);
        opcodeHashMapForFloatVector.put("LDFR", 042);
        opcodeHashMapForFloatVector.put("STFR", 043);

    }
    public int currentAddress = 0; // This tracks the current address location.
    public String LISTING_FILE = "listingFile.txt"; // output file name for the listing file5.
    public String LOAD_FILE = "LoadFile.txt"; // output file name for the load file
    public HashMap<String, Integer> symbolsMap = new HashMap<>();

    /**
     * firstPass : This method call is for reading the source program,store the
     * instructions, resolve labels, and populates the symbolsMap
     * Each label is assigned a memory location, and instructions are counted.
     *
     * @param sourceFile The input file containing the assembly source code.
     * @throws IOException if the source file cannot be read.
     */
    public ArrayList<String> firstPass(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        ArrayList<String> inputLines = new ArrayList<>(); // ArrayList to store the instructions for the secondPass
                                                          // call.
        String line;

        while ((line = reader.readLine()) != null) { // Read each line of the input file and remove comment if it is
                                                     // present in the inputfile.
            int commentIndex = line.indexOf(';');
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex).trim();
                if (!line.isEmpty()) {
                    inputLines.add(line);
                }
            } else {
                inputLines.add(line.trim());
            }
        }
        reader.close(); // closing the file after reading it.
        int symbolIndex;
        for (String instruction : inputLines) { // This part is for parsing the symbols and updating the current address
            if (instruction.startsWith("LOC")) {
                String locationString = "LOC";
                currentAddress = Integer.parseInt(instruction.substring(locationString.length()).trim());
                continue;
            }
            symbolIndex = instruction.indexOf(':');
            if (symbolIndex != -1) {
                symbolsMap.put(instruction.substring(0, symbolIndex), currentAddress);
                instruction = instruction.substring(symbolIndex + 1).trim();
            }
            if (!instruction.isEmpty()) {
                currentAddress++;
            }
        }

        return inputLines;
    }

    /**
     * secondPass: Translate Instructions (input file lines) to Machine Code and
     * Generate Output Files
     * 
     * @param inputLines The inputlines containing the instruction list from the
     *                   firstPass method.
     */
    public void secondPass(ArrayList<String> inputLines) throws IOException {

        BufferedWriter listingFileWriter = new BufferedWriter(new FileWriter(LISTING_FILE));
        BufferedWriter loadFileWriter = new BufferedWriter(new FileWriter(LOAD_FILE));
        currentAddress = 0;
        ArrayList<String> machineCodeList = new ArrayList<>();
        int symbolIndex;
        for (String inputInstruction : inputLines) {
            if (inputInstruction.startsWith("LOC")) {
                String locString = "LOC";
                currentAddress = Integer.parseInt(inputInstruction.substring(locString.length()).trim());
                machineCodeList.add(String.format("%06o\t%06o", currentAddress, 0)); // Initial memory location with
                                                                                     // zero value
                continue;
            }

            symbolIndex = inputInstruction.indexOf(':'); // Ignoring the label
            if (symbolIndex != -1) {
                inputInstruction = inputInstruction.substring(symbolIndex + 1).trim();
            }

            if (inputInstruction.isEmpty()) { // paasing the empty instruction
                continue;
            }
            String[] instructionComponents = inputInstruction.split("\\s+", 2);
            if (instructionComponents[0].equals("Data")) {   // parsing using the opcodes form the hashmaps if the component is related to data
                int dataValue;
                try {
                    dataValue = Integer.parseInt(instructionComponents[1]);
                } catch (NumberFormatException e) {
                    dataValue = symbolsMap.get(instructionComponents[1]);
                }
                machineCodeList.add(String.format("%06o\t%06o", currentAddress, dataValue));
            }

            else if ((instructionComponents[0].equals("AND")) || (instructionComponents[0].equals("ORR"))
                    || (instructionComponents[0].equals("NOT")) || (instructionComponents[0].equals("MLT"))
                    || (instructionComponents[0].equals("DVD")) || (instructionComponents[0].equals("TRR"))) { //parsing using the opcodes form the hashmaps if the component is related to arithmetic and logical operations
                int opcode = opcodeHashMapForArithmeticAndLogic.get(instructionComponents[0]);

                // Split and trim the operand list (assuming operands are comma-separated)
                String[] operands = instructionComponents[1].split(",");
                Arrays.setAll(operands, i -> operands[i].trim());

                int a1, a2;

                if (instructionComponents[0].equals("AND") || instructionComponents[0].equals("ORR")
                        || instructionComponents[0].equals("MLT")
                        || instructionComponents[0].equals("DVD") || instructionComponents[0].equals("TRR")) {
                    a1 = Integer.parseInt(operands[0]);
                    a2 = Integer.parseInt(operands[1]);
                    machineCodeList
                            .add(String.format("%06o\t%06o", currentAddress, (opcode << 10) | (a1 << 8) | (a2 << 6)));
                }
                if (instructionComponents[0].equals("NOT")) {
                    a1 = Integer.parseInt(operands[0]);
                    machineCodeList.add(String.format("%06o\t%06o", currentAddress, (opcode << 10) | (a1 << 8)));
                }
            }

            else if ((instructionComponents[0].equals("SRC")) || (instructionComponents[0].equals("RRC"))) { // parsing using the opcodes form the hashmaps if the component is related to shift rotate operation

                int opcode = opcodeHashMapForShiftRotate.get(instructionComponents[0]);// Get the opcode from the
                                                                                       // corresponding hashmap

                String[] operands = instructionComponents[1].split(",");// Spliting and trim the operand list
                Arrays.setAll(operands, i -> operands[i].trim());

                int a, b, c, d;
                if (instructionComponents[0].equals("SRC") || instructionComponents[0].equals("RRC")) {
                    a = Integer.parseInt(operands[0]);
                    b = Integer.parseInt(operands[1]);
                    c = Integer.parseInt(operands[2]);
                    d = Integer.parseInt(operands[3]);
                    machineCodeList.add(String.format("%06o\t%06o", currentAddress,
                            (opcode << 10) | (a << 8) | (d << 7) | (c << 6) | b));
                }
            }

            else if ((instructionComponents[0].equals("FADD")) || (instructionComponents[0].equals("FSUB")) ||
                    (instructionComponents[0].equals("VADD")) || (instructionComponents[0].equals("VSUB")) ||
                    (instructionComponents[0].equals("CNVRT")) || (instructionComponents[0].equals("LDFR")) ||
                    (instructionComponents[0].equals("STFR"))) { //parsing using the opcodes form the hashmaps if the component is related to float vector operations
                // Get the opcode from the map
                int opcode = opcodeHashMapForFloatVector.get(instructionComponents[0]);

                // Split and trim the operand list
                String[] operands = instructionComponents[1].split(",");
                Arrays.setAll(operands, i -> operands[i].trim());

                int fr, i, x, addr;

                fr = Integer.parseInt(operands[0]);
                x = Integer.parseInt(operands[1]);
                addr = Integer.parseInt(operands[2]);
                i = (operands.length > 3) ? Integer.parseInt(operands[3]) : 0; // Optional operand
                machineCodeList.add(String.format("%06o\t%06o", currentAddress,
                        (opcode << 10) | (fr << 8) | (i << 7) | (x << 5) | addr));

            }

            else if ((instructionComponents[0].equals("IN")) || (instructionComponents[0].equals("OUT"))
                    || (instructionComponents[0].equals("CHK"))) { //parsing using the opcodes form the hashmaps if the component is related to Input and output opertions
                // Get the opcode from the map
                int opcode = opcodeHashMapForIO.get(instructionComponents[0]);

                // Split and trim the operand list
                String[] operands = instructionComponents[1].split(",");
                Arrays.setAll(operands, i -> operands[i].trim());

                int r, devId;
                r = Integer.parseInt(operands[0]);
                devId = Integer.parseInt(operands[1]);
                machineCodeList.add(String.format("%06o\t%06o", currentAddress, (opcode << 10) | (r << 8) | devId));

            } else if ((instructionComponents[0].equals("HLT")) || (instructionComponents[0].equals("TRAP"))) { //parsing using the opcodes form the hashmaps if the component is related to Misllaneous operations
                // Get the opcode from the map
                int opcode = opcodeHashMapForMisallaneous.get(instructionComponents[0]);

                // Switch statement to handle specific instructions like HLT and TRAP
                switch (instructionComponents[0]) {
                    case "HLT": // Halt instruction has no operand, return machine code with 0
                        machineCodeList.add(String.format("%06o\t%06o", currentAddress, 0));
                        break;
                    case "TRAP": // Trap instruction has an operand, combining both opcode and operand
                        // Ensuring if there's a second component for the operand
                        if (instructionComponents.length > 1) {
                            int operand = Integer.parseInt(instructionComponents[1]);
                            machineCodeList.add(String.format("%06o\t%06o", currentAddress, (opcode << 10) | operand));
                        } else {
                            machineCodeList.add("ERROR: Missing operand for TRAP instruction!");
                        }
                        break;
                    default: // for handling unknown instructions

                        if (opcode == 0) {// Check if the opcode exists, if not return an error
                            machineCodeList.add("ERROR: Unknown instruction!");
                        }
                        machineCodeList.add(String.format("%06o\t%06o", currentAddress, opcode));
                }
            }

            else if ((instructionComponents[0].equals("LDR")) || (instructionComponents[0].equals("STR")) ||
                    (instructionComponents[0].equals("LDA")) || (instructionComponents[0].equals("LDX")) ||
                    (instructionComponents[0].equals("STX")) || (instructionComponents[0].equals("SETCCE")) ||
                    (instructionComponents[0].equals("JZ")) || (instructionComponents[0].equals("JNE")) ||
                    (instructionComponents[0].equals("JCC")) || (instructionComponents[0].equals("JMA")) ||
                    (instructionComponents[0].equals("JSR")) || (instructionComponents[0].equals("RFS")) ||
                    (instructionComponents[0].equals("SOB")) || (instructionComponents[0].equals("JGE")) ||
                    (instructionComponents[0].equals("AMR")) || (instructionComponents[0].equals("SMR")) ||
                    (instructionComponents[0].equals("SIR")) || (instructionComponents[0].equals("AIR"))) { // parsing using the opcodes form the hashmaps if the component is related to load , store , jump etc operations. 
                machineCodeList.add(parseLsInstruction(instructionComponents));
            } else {
                machineCodeList.add("ERROR: Unknown instruction!");
            }
            currentAddress++;
        }
        System.out.println(machineCodeList);
        writeDataToFile("loadFile.txt", machineCodeList); // loadfile generation - output file
        formListingFileData(inputLines, LISTING_FILE, machineCodeList); //  listing file generation 
        listingFileWriter.close();
        loadFileWriter.close();

    }

    String parseLsInstruction(String[] instructionComponents) {
        // Get the opcode from the map
        int opcode = opcodeHashMapForLSAndOther.get(instructionComponents[0]);

        // Split and trim the operand list
        String[] operands = instructionComponents[1].split(",");
        Arrays.setAll(operands, i -> operands[i].trim());

        int r, a1, address, i;

        // Switch statement for different types of instructions
        switch (instructionComponents[0]) {
            case "LDR":
            case "STR":
            case "LDA":
            case "JCC":
            case "SOB":
            case "JGE":
            case "AMR":
            case "SMR":
                r = Integer.parseInt(operands[0]);
                a1 = Integer.parseInt(operands[1]);
                address = Integer.parseInt(operands[2]);
                i = (operands.length > 3) ? Integer.parseInt(operands[3]) : 0; // Optional operand
                return String.format("%06o\t%06o", currentAddress,
                        (opcode << 10) | (r << 8) | (a1 << 6) | (i << 5) | address);

            case "LDX":
            case "STX":
            case "JZ":
            case "JNE":
            case "JMA":
            case "JSR":
                a1 = Integer.parseInt(operands[0]);
                address = Integer.parseInt(operands[1]);
                i = (operands.length > 2) ? Integer.parseInt(operands[2]) : 0; 
                return String.format("%06o\t%06o", currentAddress, (opcode << 10) | (a1 << 6) | (i << 5) | address);

            case "SETCCE":
                r = Integer.parseInt(operands[0]);
                return String.format("%06o\t%06o", currentAddress, (opcode << 10) | (r << 8));

            case "RFS":
                address = Integer.parseInt(operands[0]);
                return String.format("%06o\t%06o", currentAddress, (opcode << 10) | address);

            case "AIR":
            case "SIR":
                r = Integer.parseInt(operands[0]);
                address = Integer.parseInt(operands[1]);
                return String.format("%06o\t%06o", currentAddress, (opcode << 10) | (r << 8) | address);

            default:
                return "ERROR: Unknown or invalid instruction!";
        }
    }
    // This method gets the data and writes its contents into the file path given 
    public static void writeDataToFile(String filePath, ArrayList<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    // This method is used for forming the data required for the lisiting file and write in a file.
    public void formListingFileData(ArrayList<String> inputLines, String destinationFile, ArrayList<String> output) {

        ArrayList<String> dataToWrite = new ArrayList<>();
        int maxLines = Math.max(inputLines.size(), output.size());

        for (int i = 0; i < maxLines; i++) {
            String sourceLine = (i < inputLines.size()) ? inputLines.get(i) : "";
            String resultLine = (i <=output.size()) ? output.get(i) : "";

            dataToWrite.add(String.format("%-50s %s", resultLine, sourceLine)); // Adjust the format according to your needs
        }
        writeDataToFile(destinationFile, dataToWrite);
    }

    public static void main(String[] args) {
        try {
            Assembler assembler = new Assembler();// Create an instance of the assembler.

            ArrayList<String> inputLines = assembler.firstPass("sourceProgram.txt");// This firstPass method call is for the input lines from the source file ,labels and address.
                                                                                        

            assembler.secondPass(inputLines);// This secondPass method calls to translate the instructions to machine
                                             // code and generate output files.

            System.out.println("Assembler completed successfully. Listing and load files created."); // After completion shows the completion message
                                                                                                    
        } catch (IOException e) {

            e.printStackTrace(); // for debugging the error occured during the execution .
        }
    }

}
