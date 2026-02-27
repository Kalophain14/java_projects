import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculator {

    int boardWidth = 360;
    int boardHeight = 540;

    // Apple iOS calculator colors
    Color customLightGray = new Color(165, 165, 165);
    Color customDarkGrey = new Color(51, 51, 51);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255, 149, 0);

    String[] buttonValues = {
        "AC", "+/-", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "√", "="
    };
    String[] rightSymbols = {"÷", "×", "-", "+", "="};
    String[] topSymbols = {"AC", "+/-", "%"};

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    Calculator() {

        // macOS overrides button colors by default — this forces Java to use its own styling
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayLabel.setBackground(customBlack);
        displayLabel.setForeground(Color.white);
        displayLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 80));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        displayLabel.setText("0");
        displayLabel.setOpaque(true); // required otherwise the background color won't show

        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(customBlack);
        displayPanel.add(displayLabel);
        frame.add(displayPanel, BorderLayout.NORTH);

        buttonPanel.setLayout(new GridLayout(5, 4, 1, 1)); // 1px gaps between buttons gives the Apple separated look
        buttonPanel.setBackground(customBlack);

        for (int i = 0; i < buttonValues.length; i++) {
            JButton button = new JButton();
            String buttonValue = buttonValues[i];
            button.setFont(new Font("Helvetica Neue", Font.PLAIN, 24));
            button.setText(buttonValue);
            button.setFocusPainted(false); // removes the default blue focus ring when a button is clicked
            button.setOpaque(true);        // required on macOS otherwise button background color is ignored
            button.setContentAreaFilled(true);
            button.setBorder(new LineBorder(customBlack, 2));

            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                button.setBackground(customLightGray);
                button.setForeground(Color.black);
            } else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                button.setBackground(customOrange);
                button.setForeground(Color.white);
            } else {
                button.setBackground(customDarkGrey);
                button.setForeground(Color.white);
            }
            
            // add action listener to handle button clicks
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String buttonValue = button.getText();

                    if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                        // operator clicked — store the value for later calculation

                    } else if (Arrays.asList(topSymbols).contains(buttonValue)) {
                        // AC, +/-, % clicked

                    } else { // digits or .
                        if (buttonValue.equals(".")) {
                            // only add a decimal if there isn't one already

                        } else if ("0123456789".contains(buttonValue)) {
                            // if display shows 0, replace it — otherwise append the digit
                            if (displayLabel.getText().equals("0")) {
                                displayLabel.setText(buttonValue);
                            } else {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        }
                    }
                }
            });

            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true); // setVisible LAST so all components are added before the window appears
    }
}