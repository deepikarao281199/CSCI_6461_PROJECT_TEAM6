package com.csa.simulator.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  This file contains instructions for printing and connecting to input and output modules while the simulator is operating.
 */

public class Modules extends JFrame{
    private final JTextArea consoleOutputArea;
    private final JTextArea consoleInputArea;
    private final JTextArea cacheOut;
    char[] cacheAdd, cacheVal;
    public int printStat=0; // check for console printer : 0 if unused, 1 if used
    public Modules(){
        super("Console");
        this.setSize(960,480);
        this.setLayout(null);

        cacheAdd =new char[16];
        cacheVal =new char[16];
        ArrayList<JPanel> panel = new ArrayList<JPanel>();
        panel.add(new JPanel());
        panel.add(new JPanel());
        panel.add(new JPanel());

        TitledBorder ConsoleOutBorder = new TitledBorder("Console Printer");
        ConsoleOutBorder.setTitleJustification(TitledBorder.CENTER);
        ConsoleOutBorder.setTitlePosition(TitledBorder.TOP);
        TitledBorder ConsoleInBorder = new TitledBorder("Console Keyboard");
        ConsoleInBorder.setTitleJustification(TitledBorder.CENTER);
        ConsoleInBorder.setTitlePosition(TitledBorder.TOP);
        TitledBorder CacheBorder = new TitledBorder("Cache Out");
        CacheBorder.setTitleJustification(TitledBorder.CENTER);
        CacheBorder.setTitlePosition(TitledBorder.TOP);

        consoleInputArea = new JTextArea("", 1, 20);
        consoleOutputArea = new JTextArea(16,38);
        consoleOutputArea.setFont(new Font("Courrier New",Font.BOLD,14));
        //ConsoleOut.setBounds(0,0,350,300);
        consoleOutputArea.setEditable(false);
        consoleOutputArea.setBackground(Color.BLACK);
        consoleOutputArea.setForeground(Color.green);

        cacheOut = new JTextArea(18,32);
        cacheOut.setFont(new Font("Courrier New",Font.PLAIN,16));
        cacheOut.setEditable(false);
        JScrollPane scroll = new JScrollPane(cacheOut,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroll);
        panel.get(0).setBorder(ConsoleOutBorder);
        panel.get(0).add(consoleOutputArea);

        panel.get(1).setBorder(ConsoleInBorder);
        panel.get(1).add(consoleInputArea);

        panel.get(2).setBorder(CacheBorder);
        panel.get(2).add(cacheOut);

        panel.get(0).setBounds(10, 10, 480, 340);
        panel.get(1).setBounds((480-250)/2+10, 360, 250, 60);
        panel.get(2).setBounds(500,10,440,420);
        for(int i=0;i<3;i++)
            this.add(panel.get(i));
    }
    public void emptyConsole(){
        consoleOutputArea.setText(null);
        consoleInputArea.setText(null);
    }

    public int boardStatus =0;
    public int keyboardStatus(){
        if(!consoleInputArea.getText().isEmpty()) boardStatus =1; //Text in Keyboard
        else boardStatus =0; // Empty to Use.
        return boardStatus;
    }
    public void keyboard(char []Reg){
        Transformer conv = new Transformer();
        try{
            short c = (short)consoleInputArea.getText().charAt(0);
            conv.DecimalToBinary(c, Reg, 16);
            if(consoleInputArea.getText().length()>1){
                consoleInputArea.setText(consoleInputArea.getText().substring(1));
            }else if(consoleInputArea.getText().length()==1)
                consoleInputArea.setText(null);
        }catch(Exception e){
            conv.DecimalToBinary((short)0, Reg, 16);
            System.out.println("No Input Found");
        }
    }

    public void printer(char[] Reg){
        printStat=1;
        Transformer conv = new Transformer();
        Character dec = (char)conv.BinaryToDecimal(Reg, 16);
        String s = String.valueOf(dec);
        try{
            consoleOutputArea.append(s);
        }catch(Exception e){
            return;
        }
    }
    public void printCache(Cache cache)throws IOException{
        for(int i=cache.last-1;i>=cache.first;i--){
            //System.out.println(cache.lines[i].key + " "+cache.lines[i].val);
            cache.DecimalToBinary(cache.lines[i].key, cacheAdd, 16);
            cache.DecimalToBinary(cache.lines[i].val, cacheVal, 16);
            String key = new String(cacheAdd);
            String val = new String(cacheVal);
            ArrayList<Character> keyarr = new ArrayList<Character>();
            ArrayList<Character> valarr = new ArrayList<Character>();
            for(int k=0;k<16;k++){
                if(key.charAt(k)==(char)0) keyarr.add('0');
                else keyarr.add('1');
                if(val.charAt(k)==(char)0) valarr.add('0');
                else valarr.add('1');

            }
            StringBuilder b = new StringBuilder();
            StringBuilder c = new StringBuilder();
            for(Character ch: keyarr) b.append(ch);
            for(Character ch: valarr) c.append(ch);
            cacheOut.append(b.toString() + " " + c.toString()+"\n");
        }
        cacheOut.append("\n");
        File trace = new File("Cache.txt");
        FileWriter fw = new FileWriter(trace,true);
        fw.write(cacheOut.getText());
        fw.close();
    }
    public void Run(){
        //this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
