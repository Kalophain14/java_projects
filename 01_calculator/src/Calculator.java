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

    double firstNumber = 0;
    String operator = "";
    boolean newNumber = true;

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
        displayLabel.setOpaque(true);

        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(customBlack);
        displayPanel.add(displayLabel);
        frame.add(displayPanel, BorderLayout.NORTH);

        buttonPanel.setLayout(new GridLayout(5, 4, 1, 1));
        buttonPanel.setBackground(customBlack);

        // create buttons and add to panel
        for (int i = 0; i < buttonValues.length; i++) {
            JButton button = new JButton();
            String buttonValue = buttonValues[i];
            button.setFont(new Font("Helvetica Neue", Font.PLAIN, 24));
            button.setText(buttonValue);
            button.setFocusPainted(false);
            button.setOpaque(true);
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

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleInput(button.getText());
                }
            });

            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        // keyboard listener — attached to the frame so it works anywhere in the window
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                int keyCode = e.getKeyCode();

                if (Character.isDigit(key)) {
                    handleInput(String.valueOf(key));
                } else if (key == '.') {
                    handleInput(".");
                } else if (key == '+') {
                    handleInput("+");
                } else if (key == '-') {
                    handleInput("-");
                } else if (key == '*') {
                    handleInput("×");
                } else if (key == '/') {
                    handleInput("÷");
                } else if (key == '%') {
                    handleInput("%");
                } else if (key == '\n' || key == '=') { // Enter or = key
                    handleInput("=");
                } else if (keyCode == KeyEvent.VK_BACK_SPACE) { // delete last digit
                    String current = displayLabel.getText();
                    if (current.length() > 1) {
                        displayLabel.setText(current.substring(0, current.length() - 1));
                    } else {
                        displayLabel.setText("0");
                        newNumber = true;
                    }
                } else if (keyCode == KeyEvent.VK_ESCAPE) { // Escape = AC
                    handleInput("AC");
                }
            }
        });

        // frame must be focusable to receive keyboard events
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        frame.setVisible(true);
    }

    // all button and keyboard input goes through here so logic isn't duplicated
    void handleInput(String value) {
        if (value.equals("AC")) {
            displayLabel.setText("0");
            firstNumber = 0;
            operator = "";
            newNumber = true;

        } else if (value.equals("+/-")) {
            double current = Double.parseDouble(displayLabel.getText());
            displayLabel.setText(formatResult(current * -1));

        } else if (value.equals("%")) {
            double current = Double.parseDouble(displayLabel.getText());
            displayLabel.setText(formatResult(current / 100));

        } else if (Arrays.asList(rightSymbols).contains(value)) {
            if (value.equals("=")) {
                double secondNumber = Double.parseDouble(displayLabel.getText());
                double result = calculate(firstNumber, secondNumber, operator);
                displayLabel.setText(formatResult(result));
                operator = "";
                newNumber = true;
            } else {
                firstNumber = Double.parseDouble(displayLabel.getText());
                operator = value;
                newNumber = true;
            }

        } else {
            if (newNumber) {
                displayLabel.setText(value.equals(".") ? "0." : value);
                newNumber = false;
            } else {
                if (value.equals(".")) {
                    if (!displayLabel.getText().contains(".")) {
                        displayLabel.setText(displayLabel.getText() + ".");
                    }
                } else if (value.equals("√")) {
                    double current = Double.parseDouble(displayLabel.getText());
                    displayLabel.setText(formatResult(Math.sqrt(current)));
                    newNumber = true;
                } else {
                    displayLabel.setText(displayLabel.getText() + value);
                }
            }
        }
    }

    double calculate(double a, double b, String operator) {
        switch (operator) {
            case "÷": return b != 0 ? a / b : 0; // avoid dividing by zero
            case "×": return a * b;
            case "-": return a - b;
            case "+": return a + b;
            default:  return b;
        }
    }

    // removes unnecessary decimal places e.g. 8.0 becomes 8, but 8.5 stays 8.5
    String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }
}