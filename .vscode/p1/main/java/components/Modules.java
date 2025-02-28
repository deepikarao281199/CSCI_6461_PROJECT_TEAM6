package components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;

/**
 *  This file contains instructions for printing and connecting to input and output modules while the simulator is operating.
 */
public class Modules extends JFrame{
    private final JTextArea consoleOutputArea;
    private final JTextArea consoleInputArea;
    public Modules(){
        super("Console");
        this.setSize(960,480);
        this.setLayout(null);
        ArrayList<JPanel> devicePanels = new ArrayList<JPanel>();
        devicePanels.add(new JPanel());
        devicePanels.add(new JPanel());
        devicePanels.add(new JPanel());

        TitledBorder consoleOutputBorder = new TitledBorder("Console Printer");
        consoleOutputBorder.setTitleJustification(TitledBorder.CENTER);
        consoleOutputBorder.setTitlePosition(TitledBorder.TOP);
        TitledBorder consoleInputBorder = new TitledBorder("Console Keyboard");
        consoleInputBorder.setTitleJustification(TitledBorder.CENTER);
        consoleInputBorder.setTitlePosition(TitledBorder.TOP);

        consoleInputArea = new JTextArea("", 1, 20);
        consoleOutputArea = new JTextArea(16,38);
        consoleOutputArea.setFont(new Font("Courrier New",Font.BOLD,14));
        //ConsoleOut.setBounds(0,0,350,300);
        consoleOutputArea.setEditable(false);
        consoleOutputArea.setBackground(Color.BLACK);
        consoleOutputArea.setForeground(Color.green);

        devicePanels.get(0).setBorder(consoleOutputBorder);
        devicePanels.get(0).add(consoleOutputArea);

        devicePanels.get(1).setBorder(consoleInputBorder);
        devicePanels.get(1).add(consoleInputArea);

        devicePanels.get(0).setBounds(10, 10, 480, 340);
        devicePanels.get(1).setBounds((480-250)/2+10, 360, 250, 60);
        devicePanels.get(2).setBounds(500,10,440,420);
        for(int i=0;i<3;i++)
            this.add(devicePanels.get(i));
    }
    public void clearConsoleText(){
        consoleOutputArea.setText(null);
        consoleInputArea.setText(null);
    }
}
