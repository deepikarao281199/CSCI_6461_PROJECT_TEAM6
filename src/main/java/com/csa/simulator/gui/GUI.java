package com.csa.simulator.gui;

import com.csa.simulator.components.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;


/**
 * GUI class to create the GUI for the simulator.
 */
public class GUI extends JFrame {
    private JTextArea textField, textField2;
    private JLabel[] gpr0Arr, gpr1Arr, gpr2Arr, gpr3Arr, pcLabels, marLabels, mbrLabels, mfrLabels, irLabels;
    private JLabel[][] ixrLabels;
    private JLabel haltLabel;
    private JLabel runLabel;
    private JButton[] loadButtons;
    private CPU cpu;
    private Memory memory;
    private File file;
    private Modules devices;
    char[] switchArray;
    boolean isProgram1Loaded = false;
    boolean isProgram2Loaded = false;
    String program2Paragraph = "";

    /**
     * Start position for vertical alignment
     */
    private final int start = 280;
    private List<Integer> NumbersList = new ArrayList<Integer>();


    /**
     * Initializes components like Labels, Buttons, Panels and Arrays
     */
    private void initializeComponents() {
        haltLabel = new JLabel();
        runLabel = new JLabel();
        devices = new Modules();
        cpu = new CPU(devices);
        memory = new Memory();

        switchArray = new char[16];
        Arrays.fill(switchArray, (char) 0);
        loadButtons = new JButton[10];
        gpr0Arr = new JLabel[16];
        gpr1Arr = new JLabel[16];
        gpr2Arr = new JLabel[16];
        gpr3Arr = new JLabel[16];
        mbrLabels = new JLabel[16];
        irLabels = new JLabel[16];
        ixrLabels = new JLabel[3][16]; // 3 rows for IXRs, each with 16 labels
        pcLabels = new JLabel[12];
        marLabels = new JLabel[12];
        mfrLabels = new JLabel[4];
    }

    /**
     * Arranges and positions components on the GUI.
     */
    private void layoutComponents() {
        // Setting up Halt and Run Labels
        haltLabel.setBounds(510, start + 490, 20, 20);
        haltLabel.setOpaque(true);
        haltLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        haltLabel.setBackground(Color.WHITE);
        runLabel.setBounds(590, start + 490, 20, 20);
        runLabel.setOpaque(true);
        runLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        runLabel.setBackground(Color.WHITE);

        this.add(haltLabel);
        this.add(runLabel);

        // Creating and setting up Panels for OpCode, GPR, IXR, etc.
        JPanel[] panels = createPanels();
        createAndAddRegisters();
        createAndAddSwitches(panels);
        createLoadButtons();
        createGeneralLabels();
    }


    /**
     * Creates panels and returns them as an array.
     */
    private JPanel[] createPanels() {
        JPanel[] panels = new JPanel[5];
        JLabel[] panelLabel = new JLabel[5];
        String[] panelLabels = {"OpCode", "GPR", "IXR", "I", "Address"};
        int[] xPositions = {215, 440, 560, 670, 810};
        for (int i = 0; i < 5; i++) {
            panels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 3));
            panels[i].setBackground(Color.getHSBColor(0.6f, 0.25f, 0.9f));
            panels[i].setBorder(BorderFactory.createBevelBorder(0));
            panels[i].setOpaque(true);
            panelLabel[i] = new JLabel(panelLabels[i]);
            panelLabel[i].setFont(new Font("Arial", Font.BOLD, 17));
            panelLabel[i].setBounds(xPositions[i], start + 335, 100, 20);
            this.add(panelLabel[i]);
            this.add(panels[i]);
        }

        panels[0].setBounds(80, start + 265, 310, 70);
        panels[1].setBounds(400, start + 265, 110, 70);
        panels[2].setBounds(520, start + 265, 110, 70);
        panels[3].setBounds(640, start + 265, 60, 70);
        panels[4].setBounds(710, start + 265, 280, 70);
        return panels;
    }

    /**
     * Creates and adds register labels such as GPR, IXR, MBR, etc.
     */
    private void createAndAddRegisters() {
        JLabel PC = new JLabel("PC"), MAR = new JLabel("MAR"), MBR = new JLabel("MBR"), IR = new JLabel("IR"), MFR = new JLabel("MFR");
        JLabel[] registerLabels = {PC, MAR, MBR, IR, MFR};

        // Setting bounds and fonts for all register labels
        int[] yPositions = {0, 30, 60, 90, 120};
        int[] xPositions = {650, 650, 550, 550, 850};
        for (int i = 0; i < registerLabels.length; i++) {
            registerLabels[i].setBounds(xPositions[i], start + yPositions[i], 40, 20);
            registerLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            this.add(registerLabels[i]);
        }

        // Privilege label
        JLabel priv = new JLabel("Privilege");
        priv.setBounds(900, start + 150, 100, 20);
        priv.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(priv);


        JLabel privLabel = createLabel(975, start + 150);
        this.add(privLabel);

        // Adding labels for GPRs
        for (int i = 0; i < 4; i++) {
            JLabel gprLabel = new JLabel("GPR " + i);
            gprLabel.setBounds(30, start + (i * 30), 45, 20);
            gprLabel.setFont(new Font("Arial", Font.BOLD, 15));
            this.add(gprLabel);

            mfrLabels[i] = createLabel(900 + (i * 25), start + 120);
            this.add(mfrLabels[i]);
        }

        // Adding IXR labels
        for (int i = 0; i < 3; i++) {
            JLabel ixrLabel = new JLabel("IXR " + (i + 1));
            ixrLabel.setBounds(35, start + 140 + (i * 30), 40, 20);
            ixrLabel.setFont(new Font("Arial", Font.BOLD, 15));
            this.add(ixrLabel);
        }

        // Adding GPR and IXR arrays for display
        addLabelArrays();
    }


    /**
     * Adds arrays of labels (GPR, IXR, MBR, etc.) to the GUI.
     */
    private void addLabelArrays() {
        for (int i = 0; i < 16; i++) {
            gpr0Arr[i] = createLabel(80 + (i * 25), start);
            gpr1Arr[i] = createLabel(80 + (i * 25), start + 30);
            gpr2Arr[i] = createLabel(80 + (i * 25), start + 60);
            gpr3Arr[i] = createLabel(80 + (i * 25), start + 90);

            ixrLabels[0][i] = createLabel(80 + (i * 25), start + 140);
            ixrLabels[1][i] = createLabel(80 + (i * 25), start + 170);
            ixrLabels[2][i] = createLabel(80 + (i * 25), start + 200);

            mbrLabels[i] = createLabel(600 + (i * 25), start + 60);
            irLabels[i] = createLabel(600 + (i * 25), start + 90);

            this.add(gpr0Arr[i]);
            this.add(gpr1Arr[i]);
            this.add(gpr2Arr[i]);
            this.add(gpr3Arr[i]);
            this.add(ixrLabels[0][i]);
            this.add(ixrLabels[1][i]);
            this.add(ixrLabels[2][i]);
            this.add(mbrLabels[i]);
            this.add(irLabels[i]);
        }

        // Adding PC and MAR labels
        for (int i = 0; i < 12; i++) {
            pcLabels[i] = createLabel(700 + (i * 25), start);
            marLabels[i] = createLabel(700 + (i * 25), start + 30);
            this.add(pcLabels[i]);
            this.add(marLabels[i]);
        }
    }


    /**
     * Creates a label with specified bounds and background color.
     */
    private JLabel createLabel(int x, int y) {
        JLabel label = new JLabel(" 0");
        label.setBounds(x, y, 20, 20);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setBackground(Color.WHITE);
        return label;
    }


    /**
     * Creates and adds switches to the panels.
     */
    private void createAndAddSwitches(JPanel[] panels) {

        for (int i = 0; i < 16; i++) {
            JButton switchButton = new JButton(String.valueOf(15 - i));
            switchButton.setPreferredSize(new Dimension(48, 60));
            switchButton.setFont(new Font("Arial", Font.BOLD, 17));
            switchButton.addActionListener(this::switchAction);

            if (i < 6) {
                panels[0].add(switchButton);
            } else if (i < 8) {
                panels[1].add(switchButton);
            } else if (i < 10) {
                panels[2].add(switchButton);
            } else if (i == 10) {
                panels[3].add(switchButton);
            } else {
                panels[4].add(switchButton);
            }
        }

        for (int i = 11; i < 16; i++) {
            JButton switchButton = new JButton(String.valueOf(15 - i));
            switchButton.setPreferredSize(new Dimension(48, 60));
            switchButton.setFont(new Font("Arial", Font.BOLD, 17));
            switchButton.addActionListener(this::switchAction);
            panels[4].add(switchButton);
        }
    }

    /**
     * Creates the Load buttons and adds them to the panel.
     */
    private void createLoadButtons() {
        for (int i = 0; i < loadButtons.length; i++) {
            loadButtons[i] = new JButton("Load");
            if (i < 4)
                loadButtons[i].setBounds(480, start + (i * 30), 50, 20);
            else if (i < 7)
                loadButtons[i].setBounds(480, start + 140 + ((i - 4) * 30), 50, 20);
            else
                loadButtons[i].setBounds(1000, start + ((i - 7) * 30), 50, 20);

            loadButtons[i].addActionListener(this::loadButton);
            this.add(loadButtons[i]);
        }
    }


    /**
     * Creates general labels like HALT, RUN, etc.
     */
    private void createGeneralLabels() {
        JLabel halt = new JLabel("HALT");
        halt.setBounds(460, start + 490, 40, 20);
        halt.setFont(new Font("Arial", Font.BOLD, 15));
        this.add(halt);

        JLabel run = new JLabel("RUN");
        run.setBounds(550, start + 490, 40, 20);
        run.setFont(new Font("Arial", Font.BOLD, 15));
        this.add(run);
    }


    /**
     * Constructor for GUI class.
     *
     * @throws NullPointerException if any of the components are null.
     */
    public GUI() throws NullPointerException {
        super();
        initializeComponents();
        layoutComponents();
    }

    /**
     * Method to reset the halt label's background color to black.
     */
    private void resetHalt(ActionEvent e) {
        haltLabel.setBackground(Color.white);
    }

    /**
     * Method to reset the CPU, clear devices, refresh LEDs, and reset halt state.
     */
    private void resetAll(ActionEvent e) {
        // Reset the CPU and memory state.
        cpu.Reset(memory);

        // Clear the console output for devices.
        devices.emptyConsole();

        // Refresh the LEDs from 0 to 10 (assuming there are 11 LEDs).
        for (int ledIndex = 0; ledIndex < 11; ledIndex++) {
            refreshLEDs(ledIndex);
        }

        // Reset the halt label's state by invoking resetHalt method.
        resetHalt(e);
    }

    /**
     * Method to refresh LEDs.
     */
    private void refreshLEDs(int onKeyStroke) {
        Color isTurnedOn = Color.green;
        Color isTurnedOff = Color.white;

        // Map of keystrokes to CPU register arrays and ranges
        Map<Integer, LEDUpdater> updaterMap = Map.of(
                0, new LEDUpdater(cpu.gpr0Register, gpr0Arr, 16),
                1, new LEDUpdater(cpu.gpr1Register, gpr1Arr, 16),
                2, new LEDUpdater(cpu.gpr2Register, gpr2Arr, 16),
                3, new LEDUpdater(cpu.gpr3Register, gpr3Arr, 16),
                4, new LEDUpdater(cpu.indexRegister1, ixrLabels[0], 16),
                5, new LEDUpdater(cpu.indexRegister2, ixrLabels[1], 16),
                6, new LEDUpdater(cpu.indexRegister3, ixrLabels[2], 16),
                9, new LEDUpdater(cpu.memoryBufferRegister, mbrLabels, 16),
                10, new LEDUpdater(cpu.instructionRegister, irLabels, 16)
        );

        if (updaterMap.containsKey(onKeyStroke)) {
            updaterMap.get(onKeyStroke).updateLEDs(isTurnedOn, isTurnedOff);
        } else {
            switch (onKeyStroke) {
                case 7: // Special case for PC
                    updateSpecialLEDs(cpu.programCounter, pcLabels, 12, Color.yellow, isTurnedOff);
                    break;
                case 8: // Special case for MAR
                    updateSpecialLEDs(cpu.memoryAddressRegister, marLabels, 12, Color.orange, isTurnedOff);
                    break;
                case 11: // Special case for MFR
                    updateSpecialLEDs(cpu.memoryFaultRegister, mfrLabels, 4, Color.red, isTurnedOff);
                    break;
                default:
                    // Do nothing for unsupported keystrokes
                    break;
            }
        }
    }

    private void updateSpecialLEDs(char[] register, JLabel[] labels, int range, Color isTurnedOn, Color isTurnedOff) {
        IntStream.range(0, range).forEach(i -> {
            labels[i].setBackground(register[i] == 1 ? isTurnedOn : isTurnedOff);
            labels[i].setText(register[i] == 1 ? " 1" : " 0");
        });
    }

    static class LEDUpdater {
        private final char[] register;
        private final JLabel[] labels;
        private final int range;

        public LEDUpdater(char[] register, JLabel[] labels, int range) {
            this.register = register;
            this.labels = labels;
            this.range = range;
        }

        public void updateLEDs(Color isTurnedOn, Color isTurnedOff) {
            IntStream.range(0, range).forEach(i -> {
                labels[i].setBackground(register[i] == 1 ? isTurnedOn : isTurnedOff);
                labels[i].setText(register[i] == 1 ? " 1" : " 0");
            });
        }
    }

    /**
     * Method to load the button and perform the corresponding action.
     */
    private void loadButton(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        int buttonPress = IntStream.range(0, 10)
                .filter(i -> j == loadButtons[i])
                .findFirst().orElse(-1);

        if (buttonPress != -1) {
            // Map of button presses to actions
            Map<Integer, Consumer<char[]>> buttonActionMap = Map.of(
                    0, (arr) -> cpu.setgpr0Register(cpu.BinaryToDecimal(arr, 16)),
                    1, (arr) -> cpu.setgpr1Register(arr, 16),
                    2, (arr) -> cpu.setgpr2Register(arr, 16),
                    3, (arr) -> cpu.setgpr3Register(arr, 16),
                    4, (arr) -> cpu.setindexRegister1(arr, 16),
                    5, (arr) -> cpu.setindexRegister2(arr, 16),
                    6, (arr) -> cpu.setindexRegister3(arr, 16),
                    7, (arr) -> cpu.setprogramCounter(cpu.BinaryToDecimal(arr, 16)),
                    8, (arr) -> cpu.setmemoryAddressRegister(cpu.BinaryToDecimal(arr, 16)),
                    9, (arr) -> cpu.setmemoryBufferRegister(arr, 16)
            );

            // Perform the corresponding action
            buttonActionMap.getOrDefault(buttonPress, arr -> {
            }).accept(switchArray);

            // Refresh the LEDs
            refreshLEDs(buttonPress);
        }
    }

    /**
     * Method to switch the action of the button.
     *
     * @param e ActionEvent object
     */
    private void switchAction(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        int click = 15 - Integer.parseInt(j.getText());
        if (switchArray[click] == 0) {
            switchArray[click] = 1;
            j.setBackground(Color.getHSBColor((float) 0.0, (float) 1.0, (float) 1.0));
            j.setForeground(Color.red);
        } else {
            switchArray[click] = 0;
            j.setBackground(new JButton().getBackground());
            j.setForeground(Color.black);
        }
    }

    /**
     * Method to store the memory.
     *
     * @param e ActionEvent object
     */
    private void Store(ActionEvent e) {
        try {
            System.out.println("Store Invoked");
            short EA = cpu.BinaryToDecimal(cpu.memoryAddressRegister, 12);
            if ((EA >= 0 && EA <= 5)) {
                cpu.memoryFaultRegister[3] = 1;
                refreshLEDs(11);
                cpu.MFHandle(memory);
                return;
            }
            short value = cpu.BinaryToDecimal(cpu.memoryBufferRegister, 16);
            memory.data[EA] = value;
        } catch (Exception ee) {
            cpu.memoryFaultRegister[0] = 1;
            refreshLEDs(11);
        }
    }

    /**
     * Method to store the memory and print to the screen that the store was successful.
     *
     * @param e ActionEvent object
     */
    private void StorePlus(ActionEvent e) {
        /*
         * This will store the memory and print to the screen that the store was
         * successful
         */
        // MAR is incremented here after storing
        System.out.println("Store+ Invoked");
        short EA = cpu.BinaryToDecimal(cpu.memoryAddressRegister, 12);
        if ((EA >= 0 && EA <= 9)) {
            cpu.memoryFaultRegister[3] = 1;
            refreshLEDs(11);
            cpu.MFHandle(memory);
            return;
        }
        short value = cpu.BinaryToDecimal(cpu.memoryBufferRegister, 16);
        try {
            memory.data[EA] = value;
            EA++;
            cpu.DecimalToBinary(EA, cpu.memoryAddressRegister, 12);
            refreshLEDs(8);
        } catch (IndexOutOfBoundsException ioobe) {
            cpu.memoryFaultRegister[0] = 1;
            refreshLEDs(11);
        }
    }

    /**
     * Method to load the value from the memory.
     *
     * @param e ActionEvent object
     */
    private void LoadValue(ActionEvent e) {
        System.out.println("Load Invoked");
        try {
            short EA = cpu.BinaryToDecimal(cpu.memoryAddressRegister, 12);
            cpu.DecimalToBinary((short) memory.data[EA], cpu.memoryBufferRegister, 16);
            refreshLEDs(9);
        } catch (IndexOutOfBoundsException i) {
            JOptionPane.showMessageDialog(this, "Illegal Operation with memory Access", "Error",
                    JOptionPane.ERROR_MESSAGE);
            cpu.memoryFaultRegister[0] = 1;
            refreshLEDs(11);
        }
    }

    /**
     * Method to load the file.
     *
     * @param e ActionEvent object
     */
    private void loadFile(ActionEvent e) {
        JFileChooser fCh = new JFileChooser();
        fCh.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int res = fCh.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            file = new File(fCh.getSelectedFile().getAbsolutePath());
            String filename = file.getAbsolutePath();
            String[] fullLocation = filename.split("/");
            filename = file.getName();
            if (Objects.equals(filename, "Program1.txt")) {
                if (isProgram2Loaded) {
                    JOptionPane.showMessageDialog(this, "Program2.txt is already loaded. Please run the program first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                isProgram1Loaded = true;
                textField2.setText("Enter 20 numbers to find the nearest number:");
                JOptionPane.showMessageDialog(this, filename, "File Load Successful", JOptionPane.PLAIN_MESSAGE);
                try {
                    cpu.setprogramCounter((short) 48);
                    refreshLEDs(7);
                    ProcessFile();
                } catch (FileNotFoundException fileNotFoundException) {
                    System.out.println(fileNotFoundException.getMessage());
                }
            }
            if (Objects.equals(filename, "Program2.txt")) {
                if (isProgram1Loaded) {
                    JOptionPane.showMessageDialog(this, "Program1.txt is already loaded. Please run the program first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                isProgram2Loaded = true;
                JOptionPane.showMessageDialog(this, filename, "File Load Successful", JOptionPane.PLAIN_MESSAGE);
                try {
                    ProcessFile();
                    ProcessFile2();
                } catch (FileNotFoundException fileNotFoundException) {
                    System.out.println(fileNotFoundException.getMessage());
                }
                String[] sentences = program2Paragraph.split("\\.\\s*");
                textField2.setText("The sentences in the paragraph are:");
                for (int i = 0; i < sentences.length; i++) {
                    textField2.setText(textField2.getText() + "\n" + (i + 1) + ": " + sentences[i]);
                }
                textField2.setText(textField2.getText() + "\n" + "Enter the word :");
            }
        }
    }

    /**
     * Method to process the file.
     * @throws FileNotFoundException if the file is not found
     */
    private void ProcessFile() throws FileNotFoundException {
        Scanner s = new Scanner(file);
        while (s.hasNext()) {
            String loc = s.next();
            String val = s.next();
            short hexloc = cpu.OctToDecimal(loc);
            short hexval = cpu.OctToDecimal(val);
            memory.data[hexloc] = hexval;
            System.out.println(hexloc + " " + hexval);
        }
        s.close();
    }
    /**
     * Method to process the file.
     * @throws FileNotFoundException if the file is not found
     */
    private void ProcessFile2() throws FileNotFoundException {
        File para = new File("Paragraph.txt");
        Scanner fileScanner = new Scanner(para);
        StringBuilder paragraphBuilder = new StringBuilder();
        while (fileScanner.hasNextLine()) {
            paragraphBuilder.append(fileScanner.nextLine()).append(" ");
        }
        fileScanner.close();
        program2Paragraph = paragraphBuilder.toString().trim();
    }
    private void program1() {
        NumbersList.add(Integer.parseInt(textField.getText()));
        textField2.setText(textField2.getText()+"\n"+textField.getText());
        textField.setText("");

        if(NumbersList.size() == 20){
            String userInput = JOptionPane.showInputDialog(null, "Enter any number to find closest", "", JOptionPane.QUESTION_MESSAGE);
            textField.setText(userInput);

            int nearestNumber = NumbersList.getFirst();
            int minDifference = Math.abs(nearestNumber - Integer.parseInt(userInput));

            for (int number : NumbersList) {
                int difference = Math.abs((number - Integer.parseInt(userInput)));
                if (difference < minDifference) {
                    minDifference = difference;
                    nearestNumber = number;
                }
            }
            textField2.setText(textField2.getText()+"\nNearest number:"+ nearestNumber +"\n***********************");
            NumbersList = new ArrayList<>();
            isProgram1Loaded=false;
        }
    }


    private void program2() {
        String[] sentences = program2Paragraph.split("\\.\\s*");
        String word = textField.getText();
        textField2.setText(textField2.getText()+"\n"+word);
        boolean found = false;
        for (int i = 0; i < sentences.length; i++) {
            String[] words = sentences[i].split("\\s+");
            for (int j = 0; j < words.length; j++) {
                if (words[j].replaceAll("[^a-zA-Z]", "").equalsIgnoreCase(word)) {
                    textField2.setText(textField2.getText()+"\n"+"Found the word \"" + word + "\" in sentence " + (i + 1) +
                            ", word number " + (j + 1));
                    found = true;
                }
            }
        }

        if (!found) {
            textField2.setText(textField2.getText()+"\n"+"The word \"" + word + "\" was not found in the paragraph.");
        }
        textField2.setText(textField2.getText()+"\n*********************************************************************");
        isProgram2Loaded=false;
    }

    private void takeInput(ActionEvent e) throws IOException {
        if(isProgram1Loaded) {
            program1();
        } else if(isProgram2Loaded) {
            program2();
        } else {
            JOptionPane.showMessageDialog(null,"Please load Program1.txt or Program2.txt first","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * Method to execute the code.
     * @param e ActionEvent object
     */
    private void execCode(ActionEvent e) {
        for (int i = 0; i < 12; i++)
            refreshLEDs(i);
        short EA = cpu.BinaryToDecimal(cpu.programCounter, 12);
        if (EA > 2047) {
            cpu.memoryFaultRegister[0] = 1;
            refreshLEDs(11);
            cpu.MFHandle(memory);
            memory.data[4]++;
            cpu.DecimalToBinary(memory.data[4], cpu.programCounter, 12);
            refreshLEDs(7);
            return;
        }
        cpu.DecimalToBinary(memory.data[EA], cpu.instructionRegister, 16);
        cpu.Execute(memory,devices);

        for (int i = 0; i < 12; i++)
            refreshLEDs(i);
        short val = cpu.BinaryToDecimal(cpu.instructionRegister, 6); // Get The IR Values to check for Conditions for Jumping
        if (val >= 0x08 && val <= 0x0F) {
            EA = cpu.BinaryToDecimal(cpu.programCounter, 12);
        } else if (cpu.BinaryToDecimal(cpu.memoryFaultRegister, 4) > 0) {
            cpu.MFHandle(memory);
            EA = memory.data[4];
            EA++;
        } else
            EA++;
        cpu.DecimalToBinary(EA, cpu.programCounter, 12);
        refreshLEDs(7);
    }


    /**
     * Method to run the program.
     * @param e ActionEvent object
     * @throws InterruptedException if the thread is interrupted
     */
    private void RunProg(ActionEvent e) throws InterruptedException {
        if (haltLabel.getBackground() == Color.red && runLabel.getBackground() == Color.black) {
            JOptionPane.showMessageDialog(this, "System halted. Click on Reset Halt to reset the halt status",
                    "Error: System Halt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        short OpCode;
        do {
            Thread.sleep(300);
            execCode(e);
            haltLabel.setBackground(Color.white);
            runLabel.setBackground(Color.getHSBColor(0.3f,0.5f,0.9f));
            OpCode = cpu.BinaryToDecimal(cpu.instructionRegister, 16);
        } while (OpCode != CPU.HALT_OPCODE);
        runLabel.setBackground(Color.white);
        haltLabel.setBackground(Color.getHSBColor(1f,0.5f,0.9f));

    }

    /**
     * Method to run the main loop.
     */
    private void runMainLoop() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.getHSBColor(0.54f, 0.25f, 0.9f));
        this.setLayout(null);
        this.setVisible(true);
    }


    /**
     * Method to load the GUI.
     */
    public void loadGui() {
        JLabel title = new JLabel("CSA Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setBounds(580, 50, 600, 50);
        this.add(title);
        JLabel team = new JLabel("Team 6");
        team.setFont(new Font("Arial", Font.BOLD, 40));
        team.setBounds(720, 150, 600, 50);
        this.add(team);
        JButton store = new JButton("Store");
        store.addActionListener(this::Store);
        store.setBounds(155, start + 400, 70, 35);
        this.add(store);
        JButton st_plus = new JButton("St+");
        st_plus.addActionListener(this::StorePlus);
        st_plus.setBounds(230, start + 400, 70, 35);
        this.add(st_plus);
        JButton resetAll = new JButton("Reset All");
        resetAll.addActionListener(this::resetAll);
        resetAll.setBounds(530, start + 400, 70, 35);
        this.add(resetAll);
        JButton resetHalt = new JButton("Reset Halt");
        resetHalt.addActionListener(this::resetHalt);
        resetHalt.setBounds(605, start + 400, 170, 35);
        this.add(resetHalt);
        JButton load = new JButton();
        load.setText("Load");
        load.setBounds(305, start + 400, 70, 35);
        load.addActionListener(this::LoadValue);
        load.setEnabled(true);
        this.add(load);
        JButton init = new JButton("IPL");
        init.setBounds(780, start + 403, 65, 27);
        init.setBackground(Color.getHSBColor(1f,0.7f,1f));
        init.setForeground(Color.white);
        init.setOpaque(true);
        init.setBorderPainted(false);

        init.addActionListener(this::loadFile);
        this.add(init);

        JButton ss = new JButton("SS");
        ss.setBounds(380, start + 400, 65, 35);
        ss.addActionListener(this::execCode);
        JButton run = new JButton("Run");
        run.setBounds(455, start + 400, 65, 35);
        run.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    RunProg(e);
                    return null;
                }
            };
            worker.execute();
        });
        run.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        //Input Field

        JLabel keyBoard = new JLabel("Keyboard");
        textField = new JTextArea();
        keyBoard.setBounds(1250, 250, 300, 50);
        keyBoard.setFont(new Font("Arial", Font.BOLD, 20));
        textField.setBounds(1150, 300, 300, 100);
        textField.setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane2 = new JScrollPane(textField);
        scrollPane2.setBounds(1150, 300, 300, 100);
        this.add(keyBoard);
        this.add(scrollPane2);


        JLabel printer = new JLabel("Printer");
        textField2 = new JTextArea();
        printer.setBounds(1270, start+150, 300, 50);
        printer.setFont(new Font("Arial", Font.BOLD, 20));
        textField2.setLineWrap(true);
        textField2.setBounds(1150, start+200, 300, 100);
        textField2.setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(textField2);
        scrollPane.setBounds(1150, start+200, 300, 100);
        this.add(printer);
        this.add(scrollPane);
        //Load Button
        JButton inputNumber = new JButton("Insert");
        inputNumber.setBounds(1250, start+130, 100, 30);
        inputNumber.setBackground(Color.getHSBColor(1f,0.7f,1f));
        inputNumber.setForeground(Color.white);
        inputNumber.setOpaque(true);
        inputNumber.setBorderPainted(false);
        inputNumber.setFont(new Font("Arial", Font.BOLD, 14));
        inputNumber.addActionListener(e -> {
            try {
                takeInput(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.add(inputNumber);
        this.add(ss);
        this.add(run);
        runMainLoop();
    }
}