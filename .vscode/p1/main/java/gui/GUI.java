package gui;

import components.*;

import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;


/**
 * GUI class - create the GUI for the simulator.
 */
public class GUI extends JFrame {
    char[] toggleSwitches;
    private CPU cpuInstance;
    private Memory memoryUnit;
    private File selectedFile;
    private Modules deviceManager;
    private JButton[] registerLoadButtons;
    private JLabel haltIndicator, runIndicator;
    private JLabel[] gpr0Labels,gpr1Labels, gpr2Labels, gpr3Labels;
    private JLabel[] pcRegisterLabels, marRegisterLabels, mbrRegisterLabelsbrLabels, mfrRegisterLabels, irRegisterLabels;
    private JLabel[][] ixrLabels;

    private final int verticalStartPosition = 280;


    /**
     * Initialize components.
     */
    private void initComponents() {
        cpuInstance = new CPU();
        memoryUnit = new Memory();
        haltIndicator = new JLabel();
        runIndicator = new JLabel();
        deviceManager = new Modules();

        gpr0Labels = new JLabel[16];
        gpr1Labels = new JLabel[16];
        gpr2Labels = new JLabel[16];
        gpr3Labels = new JLabel[16];
        toggleSwitches = new char[16];
        irRegisterLabels = new JLabel[16];
        pcRegisterLabels = new JLabel[12];
        mfrRegisterLabels = new JLabel[4];
        marRegisterLabels = new JLabel[12];
        Arrays.fill(toggleSwitches, (char) 0);
        registerLoadButtons = new JButton[10];
        mbrRegisterLabelsbrLabels = new JLabel[16];
        ixrLabels = new JLabel[3][16]; // 3 rows for IXRs, each with 16 labels
    }

    /**
     * Initialize the file and parse the loaded file.
     */
    private JPanel[] initRegisterPanels() {
        JPanel[] panels = new JPanel[5];
        JLabel[] panelLabel = new JLabel[5];
        String[] panelLabels = {"OpCode", "GPR", "IXR", "I", "Address"};
        int[] xPositions = {435, 660, 780, 890, 1030};
        for (int i = 0; i < 5; i++) {
            panels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 3));
            panels[i].setBackground(Color.getHSBColor(0.6f, 0.25f, 0.9f));
            panels[i].setBorder(BorderFactory.createBevelBorder(0));
            panels[i].setOpaque(true);
            panelLabel[i] = new JLabel(panelLabels[i]);
            panelLabel[i].setFont(new Font("Arial", Font.BOLD, 17));
            panelLabel[i].setBounds(xPositions[i], verticalStartPosition + 335, 100, 20);
            this.add(panelLabel[i]);
            this.add(panels[i]);
        }

        panels[0].setBounds(300, verticalStartPosition + 265, 310, 70);
        panels[1].setBounds(620, verticalStartPosition + 265, 110, 70);
        panels[2].setBounds(740, verticalStartPosition + 265, 110, 70);
        panels[3].setBounds(860, verticalStartPosition + 265, 60, 70);
        panels[4].setBounds(930, verticalStartPosition + 265, 280, 70);
        return panels;
    }

    /**
     * Initialize the register labels.
     */
    private void initRegisterLabels() {
        JLabel PC = new JLabel("PC"), MAR = new JLabel("MAR"), MBR = new JLabel("MBR"), IR = new JLabel("IR"), MFR = new JLabel("MFR");
        JLabel[] registerLabels = {PC, MAR, MBR, IR, MFR};

        // Setting up the labels for PC, MAR, MBR, IR, and MFR
        int[] yPositions = {0, 30, 60, 90, 120};
        int[] xPositions = {930, 930, 830, 830, 1130};
        for (int i = 0; i < registerLabels.length; i++) {
            registerLabels[i].setBounds(xPositions[i], verticalStartPosition + yPositions[i], 40, 20);
            registerLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            this.add(registerLabels[i]);
        }

        // Adding labels for OpCode
        JLabel priv = new JLabel("Privilege");
        priv.setBounds(1180, verticalStartPosition + 150, 100, 20);
        priv.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(priv);


        JLabel privilegeIndicator = setLabel(1255, verticalStartPosition + 150);
        this.add(privilegeIndicator);

        // Adding IXR labels
        for (int i = 0; i < 3; i++) {
            JLabel ixrLabel = new JLabel("IXR " + (i + 1));
            ixrLabel.setBounds(35, verticalStartPosition + 140 + (i * 30), 40, 20);
            ixrLabel.setFont(new Font("Arial", Font.BOLD, 15));
            this.add(ixrLabel);
        }

        // Adding MFR labels
        for (int i = 0; i < 4; i++) {
            JLabel gprLabel = new JLabel("GPR " + i);
            gprLabel.setBounds(30, verticalStartPosition + (i * 30), 45, 20);
            gprLabel.setFont(new Font("Arial", Font.BOLD, 15));
            this.add(gprLabel);

            mfrRegisterLabels[i] = setLabel(1180 + (i * 25), verticalStartPosition + 120);
            this.add(mfrRegisterLabels[i]);
        }

        // Adding GPR and IXR arrays for display
        addRegisterLabelArrays();
    }

    /**
     * Add register label arrays.
     */
    private void addRegisterLabelArrays() {
        for (int i = 0; i < 16; i++) {
            gpr0Labels[i] = setLabel(80 + (i * 25), verticalStartPosition);
            gpr1Labels[i] = setLabel(80 + (i * 25), verticalStartPosition + 30);
            gpr2Labels[i] = setLabel(80 + (i * 25), verticalStartPosition + 60);
            gpr3Labels[i] = setLabel(80 + (i * 25), verticalStartPosition + 90);

            ixrLabels[0][i] = setLabel(80 + (i * 25), verticalStartPosition + 140);
            ixrLabels[1][i] = setLabel(80 + (i * 25), verticalStartPosition + 170);
            ixrLabels[2][i] = setLabel(80 + (i * 25), verticalStartPosition + 200);

            mbrRegisterLabelsbrLabels[i] = setLabel(880 + (i * 25), verticalStartPosition + 60);
            irRegisterLabels[i] = setLabel(880 + (i * 25), verticalStartPosition + 90);

            this.add(gpr0Labels[i]);
            this.add(gpr1Labels[i]);
            this.add(gpr2Labels[i]);
            this.add(gpr3Labels[i]);
            this.add(ixrLabels[0][i]);
            this.add(ixrLabels[1][i]);
            this.add(ixrLabels[2][i]);
            this.add(mbrRegisterLabelsbrLabels[i]);
            this.add(irRegisterLabels[i]);
        }

        // Add PC and MAR labels
        for (int i = 0; i < 12; i++) {
            pcRegisterLabels[i] = setLabel(980 + (i * 25), verticalStartPosition);
            marRegisterLabels[i] = setLabel(980 + (i * 25), verticalStartPosition + 30);
            this.add(pcRegisterLabels[i]);
            this.add(marRegisterLabels[i]);
        }
    }

    /**
     * Set up the layout of the components.
     */
    private void setupComponentLayout() {
        // Setting up Halt and Run Labels
        
        runIndicator.setBounds(810, verticalStartPosition + 490, 20, 20);
        runIndicator.setOpaque(true);
        runIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        runIndicator.setBackground(Color.WHITE);

        haltIndicator.setBounds(730, verticalStartPosition + 490, 20, 20);
        haltIndicator.setOpaque(true);
        haltIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        haltIndicator.setBackground(Color.WHITE);
        
        this.add(runIndicator);
        this.add(haltIndicator);

        // Creating and setting up Panels for OpCode, GPR, IXR, etc.
        JPanel[] panels = initRegisterPanels();
        initRegisterLabels();
        initLoadButtons();
        initGeneralLabels();
        initToggleSwitches(panels);
    }

    /**
     * Set label panel.
     */
    private JLabel setLabel(int x, int y) {
        JLabel label = new JLabel(" 0");
        label.setBounds(x, y, 20, 20);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setBackground(Color.WHITE);
        return label;
    }

    /**
     * Initialize general labels.
     */
    private void initGeneralLabels() {
        JLabel halt = new JLabel("HALT");
        halt.setBounds(680, verticalStartPosition + 490, 40, 20);
        halt.setFont(new Font("Arial", Font.BOLD, 15));
        this.add(halt);

        JLabel run = new JLabel("RUN");
        run.setBounds(770, verticalStartPosition + 490, 40, 20);
        run.setFont(new Font("Arial", Font.BOLD, 15));
        this.add(run);
    }

    /**
     * Initialize Load buttons.
     */
    private void initLoadButtons() {
        for (int i = 0; i < registerLoadButtons.length; i++) {
            registerLoadButtons[i] = new JButton("Load");
            if (i < 4)
                registerLoadButtons[i].setBounds(480, verticalStartPosition + (i * 30), 50, 20);
            else if (i < 7)
                registerLoadButtons[i].setBounds(480, verticalStartPosition + 140 + ((i - 4) * 30), 50, 20);
            else
                registerLoadButtons[i].setBounds(1280, verticalStartPosition + ((i - 7) * 30), 50, 20);

            registerLoadButtons[i].addActionListener(this::handleLoadButton);
            this.add(registerLoadButtons[i]);
        }
    }

    /**
     * Initialize switches to the panels.
     */
    private void initToggleSwitches(JPanel[] panels) {

        for (int i = 0; i < 16; i++) {
            JButton switchButton = new JButton(String.valueOf(15 - i));
            switchButton.setPreferredSize(new Dimension(48, 60));
            switchButton.setFont(new Font("Arial", Font.BOLD, 17));
            switchButton.addActionListener(this::toggleSwitchState);

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
            switchButton.addActionListener(this::toggleSwitchState);
            panels[4].add(switchButton);
        }
    }


    /**
     * Constructor for GUI - initialize components.
     * @throws NullPointerException if any of the components are null.
     */
    public GUI() throws NullPointerException {
        super();
        initComponents();
        setupComponentLayout();
    }

    /**
     * Clear the halt label's background color to white.
     */
    private void clearHaltIndicator(ActionEvent e) {
        haltIndicator.setBackground(Color.white);
    }

    /**
     * Refresh LEDs.
     */
    private void updateLEDDisplay(int onKeyStroke) {
        Color isTurnedOn = Color.green;
        Color isTurnedOff = Color.white;

        // Map of keystrokes to CPU register arrays and ranges
        Map<Integer, LEDUpdater> updaterMap = Map.of(
                0, new LEDUpdater(cpuInstance.gpr0Register, gpr0Labels, 16),
                1, new LEDUpdater(cpuInstance.gpr1Register, gpr1Labels, 16),
                2, new LEDUpdater(cpuInstance.gpr2Register, gpr2Labels, 16),
                3, new LEDUpdater(cpuInstance.gpr3Register, gpr3Labels, 16),
                4, new LEDUpdater(cpuInstance.indexRegister1, ixrLabels[0], 16),
                5, new LEDUpdater(cpuInstance.indexRegister2, ixrLabels[1], 16),
                6, new LEDUpdater(cpuInstance.indexRegister3, ixrLabels[2], 16),
                9, new LEDUpdater(cpuInstance.memoryBufferRegister, mbrRegisterLabelsbrLabels, 16),
                10, new LEDUpdater(cpuInstance.instructionRegister, irRegisterLabels, 16)
        );

        if (updaterMap.containsKey(onKeyStroke)) {
            updaterMap.get(onKeyStroke).updateLEDs(isTurnedOn, isTurnedOff);
        } else {
            switch (onKeyStroke) {
                case 7: // Special case for PC
                    highlightSpecialLEDs(cpuInstance.programCounter, pcRegisterLabels, 12, Color.yellow, isTurnedOff);
                    break;
                case 8: // Special case for MAR
                    highlightSpecialLEDs(cpuInstance.memoryAddressRegister, marRegisterLabels, 12, Color.orange, isTurnedOff);
                    break;
                case 11: // Special case for MFR
                    highlightSpecialLEDs(cpuInstance.memoryFaultRegister, mfrRegisterLabels, 4, Color.red, isTurnedOff);
                    break;
                default:
                    // Do nothing for unsupported keystrokes
                    break;
            }
        }
    }

    private void highlightSpecialLEDs(char[] register, JLabel[] labels, int range, Color isTurnedOn, Color isTurnedOff) {
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
     * Reset the CPU, clear devices, refresh LEDs, and reset halt state.
     */
    private void resetSystemState(ActionEvent e) {
        // Reset the CPU and memory state.
        cpuInstance.Reset(memoryUnit);

        // Clear the console output for devices.
        deviceManager.clearConsoleText();

        // Refresh the LEDs from 0 to 10 (assuming there are 11 LEDs).
        for (int ledIndex = 0; ledIndex < 11; ledIndex++) {
            updateLEDDisplay(ledIndex);
        }

        // Reset the halt label's state by invoking resetHalt method.
        clearHaltIndicator(e);
    }

    /**
     * Load the button and perform the corresponding action.
     */
    private void handleLoadButton(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        int buttonPress = IntStream.range(0, 10)
                .filter(i -> j == registerLoadButtons[i])
                .findFirst().orElse(-1);

        if (buttonPress != -1) {
            // Map of button presses to actions
            Map<Integer, Consumer<char[]>> buttonActionMap = Map.of(
                    0, (arr) -> cpuInstance.setGpr0Register(cpuInstance.BinaryToDecimal(arr, 16)),
                    1, (arr) -> cpuInstance.setGpr1Register(arr, 16),
                    2, (arr) -> cpuInstance.setGpr2Register(arr, 16),
                    3, (arr) -> cpuInstance.setGpr3Register(arr, 16),
                    4, (arr) -> cpuInstance.setIndexRegister1(arr, 16),
                    5, (arr) -> cpuInstance.setIndexRegister2(arr, 16),
                    6, (arr) -> cpuInstance.setIndexRegister3(arr, 16),
                    7, (arr) -> cpuInstance.setProgramCounter(cpuInstance.BinaryToDecimal(arr, 16)),
                    8, (arr) -> cpuInstance.setMemoryAddressRegister(cpuInstance.BinaryToDecimal(arr, 16)),
                    9, (arr) -> cpuInstance.setMemoryBufferRegister(arr, 16)
            );

            // Perform the corresponding action
            buttonActionMap.getOrDefault(buttonPress, arr -> {}).accept(toggleSwitches);

            // Refresh the LEDs
            updateLEDDisplay(buttonPress);
        }
    }

    /**
     * Switch the action of the button.
     * @param e ActionEvent object
     */
    private void toggleSwitchState(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        int click = 15 - Integer.parseInt(j.getText());
        if (toggleSwitches[click] == 0) {
            toggleSwitches[click] = 1;
            j.setBackground(Color.getHSBColor((float) 0.0, (float) 1.0, (float) 1.0));
            j.setForeground(Color.red);
        } else {
            toggleSwitches[click] = 0;
            j.setBackground(new JButton().getBackground());
            j.setForeground(Color.black);
        }
    }

    /**
     * Load the file.
     * @param e ActionEvent object
     */
    private void selectFileAndLoad(ActionEvent e) {
        JFileChooser fCh = new JFileChooser();
        fCh.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int res = fCh.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedFile = new File(fCh.getSelectedFile().getAbsolutePath());
            String filename = selectedFile.getAbsolutePath();
            JOptionPane.showMessageDialog(this, filename, "File Load Successful", JOptionPane.PLAIN_MESSAGE);
            try {
                ProcessFile();
            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println(fileNotFoundException.getMessage());
            }
        }
    }

    /**
     * Process the file.
     * @throws FileNotFoundException if the file is not found
     */
    private void ProcessFile() throws FileNotFoundException {
        Scanner s = new Scanner(selectedFile);
        while (s.hasNext()) {
            String loc = s.next();
            String val = s.next();
            short octLoc = cpuInstance.OctToDecimal(loc);
            short octVal = cpuInstance.OctToDecimal(val);
            memoryUnit.Data[octLoc] = octVal;
            System.out.println(octLoc + " " + octVal);
        }
        s.close();
    }

    /**
     * Execute the code.
     * @param e ActionEvent object
     */
    private void executeInstruction(ActionEvent e) {
        for (int i = 0; i < 12; i++)
            updateLEDDisplay(i);
        short EA = cpuInstance.BinaryToDecimal(cpuInstance.programCounter, 12);
        if (EA > 2047) {
            cpuInstance.memoryFaultRegister[0] = 1;
            updateLEDDisplay(11);
            cpuInstance.MemoryFaultHandling(memoryUnit);
            memoryUnit.Data[4]++;
            cpuInstance.DecimalToBinary(memoryUnit.Data[4], cpuInstance.programCounter, 12);
            updateLEDDisplay(7);
            return;
        }
        cpuInstance.DecimalToBinary(memoryUnit.Data[EA], cpuInstance.instructionRegister, 16);
        cpuInstance.Execute(memoryUnit);

        for (int i = 0; i < 12; i++)
            updateLEDDisplay(i);
        short val = cpuInstance.BinaryToDecimal(cpuInstance.instructionRegister, 6); // Get The IR Values to check for Conditions for Jumping
        if (val >= 0x08 && val <= 0x0F) {
            EA = cpuInstance.BinaryToDecimal(cpuInstance.programCounter, 12);
        } else if (cpuInstance.BinaryToDecimal(cpuInstance.memoryFaultRegister, 4) > 0) {
            cpuInstance.MemoryFaultHandling(memoryUnit);
            EA = memoryUnit.Data[4];
            EA++;
        } else
            EA++;
        cpuInstance.DecimalToBinary(EA, cpuInstance.programCounter, 12);
        updateLEDDisplay(7);
    }

    /**
     * Store the memory.
     * @param e ActionEvent object
     */
    private void Store(ActionEvent e) {
        try {
            System.out.println("Store Invoked");
            short EA = cpuInstance.BinaryToDecimal(cpuInstance.memoryAddressRegister, 12);
            if ((EA >= 0 && EA <= 5)) {
                cpuInstance.memoryFaultRegister[3] = 1;
                updateLEDDisplay(11);
                cpuInstance.MemoryFaultHandling(memoryUnit);
                return;
            }
            short value = cpuInstance.BinaryToDecimal(cpuInstance.memoryBufferRegister, 16);
            memoryUnit.Data[EA] = value;
        } catch (Exception ee) {
            cpuInstance.memoryFaultRegister[0] = 1;
            updateLEDDisplay(11);
        }
    }

    /**
     * Store the memory and print to the screen that the store was successful.
     * @param e ActionEvent object
     */
    private void storeMemoryAndIncrement(ActionEvent e) {
        /*
         * This will store the memory and print to the screen that the store was
         * successful
         */
        // MAR is incremented here after storing
        System.out.println("Store+ Invoked");
        short EA = cpuInstance.BinaryToDecimal(cpuInstance.memoryAddressRegister, 12);
        if ((EA >= 0 && EA <= 9)) {
            cpuInstance.memoryFaultRegister[3] = 1;
            updateLEDDisplay(11);
            cpuInstance.MemoryFaultHandling(memoryUnit);
            return;
        }
        short value = cpuInstance.BinaryToDecimal(cpuInstance.memoryBufferRegister, 16);
        try {
            memoryUnit.Data[EA] = value;
            EA++;
            cpuInstance.DecimalToBinary(EA, cpuInstance.memoryAddressRegister, 12);
            updateLEDDisplay(8);
        } catch (IndexOutOfBoundsException ioobe) {
            cpuInstance.memoryFaultRegister[0] = 1;
            updateLEDDisplay(11);
        }
    }

    /**
     * Load the value from the memory.
     * @param e ActionEvent object
     */
    private void loadMemoryValue(ActionEvent e) {
        System.out.println("Load Invoked");
        try {
            short EA = cpuInstance.BinaryToDecimal(cpuInstance.memoryAddressRegister, 12);
            cpuInstance.DecimalToBinary((short) memoryUnit.Data[EA], cpuInstance.memoryBufferRegister, 16);
            updateLEDDisplay(9);
        } catch (IndexOutOfBoundsException i) {
            JOptionPane.showMessageDialog(this, "Illegal Operation with memory Access", "Error",
                    JOptionPane.ERROR_MESSAGE);
            cpuInstance.memoryFaultRegister[0] = 1;
            updateLEDDisplay(11);
        }
    }

    /**
     * Method to run the program.
     * @param e ActionEvent object
     * @throws InterruptedException if the thread is interrupted
     */
    private void runProgramLoop(ActionEvent e) throws InterruptedException {
        if (haltIndicator.getBackground() == Color.red && runIndicator.getBackground() == Color.black) {
            JOptionPane.showMessageDialog(this, "System halted. Click on Reset Halt to reset the halt status",
                    "Error: System Halt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        short OpCode;
        do {
            Thread.sleep(300);
            executeInstruction(e);
            haltIndicator.setBackground(Color.white);
            runIndicator.setBackground(Color.getHSBColor(0.3f,0.5f,0.9f));
            OpCode = cpuInstance.BinaryToDecimal(cpuInstance.instructionRegister, 16);
        } while (OpCode != CPU.HALT_OPCODE);
        runIndicator.setBackground(Color.white);
        haltIndicator.setBackground(Color.getHSBColor(1f,0.5f,0.9f));

    }

    /**
     * Run the main loop.
     */
    private void startMainGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.getHSBColor(0.54f, 0.25f, 0.9f));
        this.setLayout(null);
        this.setVisible(true);
    }

    /**
     * Initialize GUI.
     */
    public void initializeGUI() {

        // Setting up the title
        JLabel title = new JLabel("CSCI 6461 Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setBounds(580, 50, 600, 50);
        this.add(title);

        // Setting up the team name
        JLabel team = new JLabel("Team 6");
        team.setFont(new Font("Arial", Font.BOLD, 40));
        team.setBounds(720, 150, 600, 50);
        this.add(team);

        // Setting up the store button
        JButton storeButton = new JButton("Store");
        storeButton.addActionListener(this::Store);
        storeButton.setBounds(375, verticalStartPosition + 400, 70, 35);
        this.add(storeButton);
        
        // Setting up the store plus button
        JButton storePlusButton = new JButton("St+");
        storePlusButton.addActionListener(this::storeMemoryAndIncrement);
        storePlusButton.setBounds(450, verticalStartPosition + 400, 70, 35);
        this.add(storePlusButton);

        // Load button
        JButton loadButton = new JButton();
        loadButton.setText("Load");
        loadButton.setBounds(525, verticalStartPosition + 400, 70, 35);
        loadButton.addActionListener(this::loadMemoryValue);
        loadButton.setEnabled(true);
        this.add(loadButton);

        // Resetting the halt button
        JButton resetHaltButton = new JButton("Reset Halt");
        resetHaltButton.addActionListener(this::clearHaltIndicator);
        resetHaltButton.setBounds(825, verticalStartPosition + 400, 170, 35);
        this.add(resetHaltButton);

        // Restting all the buttons
        JButton resetAllButton = new JButton("Reset All");
        resetAllButton.addActionListener(this::resetSystemState);
        resetAllButton.setBounds(750, verticalStartPosition + 400, 70, 35);
        this.add(resetAllButton);

        // Run button
        JButton iplButton = new JButton("IPL");
        iplButton.setBounds(1000, verticalStartPosition + 403, 65, 27);
        iplButton.setBackground(Color.getHSBColor(1f,0.7f,1f));
        iplButton.setForeground(Color.white);
        iplButton.setOpaque(true);
        iplButton.setBorderPainted(false);

        iplButton.addActionListener(this::selectFileAndLoad);
        this.add(iplButton);

        JButton singleStepButton = new JButton("SS");
        singleStepButton.setBounds(600, verticalStartPosition + 400, 65, 35);
        singleStepButton.addActionListener(this::executeInstruction);
        JButton runButton = new JButton("Run");
        runButton.setBounds(675, verticalStartPosition + 400, 65, 35);
        runButton.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    runProgramLoop(e);
                    return null;
                }
            };
            worker.execute();
        });
        runButton.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.add(singleStepButton);
        this.add(runButton);
        startMainGUI();
    }
}