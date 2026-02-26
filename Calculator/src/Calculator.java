import java.awt.*;
import java.awt.event.*; //graphics library
import java.util.Arrays; //arrays for listing out buttons
import javax.swing.*;
import javax.swing.border.LineBorder; //modify boarders on the buttons

public class Calculator {
    
    // Window Frame
    int boardWidth =  360;
    int boardHeight = 540;

    Color customLightGray = new Color(212,212,210);
    Color customDarkGrey = new Color(80, 80, 80);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255,149, 0);

    
    //Java Swing components
    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel(); //display the numbers on the screen, right aligned
    JPanel displayPanel = new JPanel(); //display the buttons


    //Construction
    Calculator (){
        frame.setVisible(true); //see the window
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); //sensor the window
        frame.setResizable(false); //user doesn't drag the sides outside of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x button to close the app
        frame.setLayout(new BorderLayout()); //north, south, west and east within the window
    }
}
