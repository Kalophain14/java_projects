/*
This is the logical views for the app
 */

import java.util.Scanner;

public class bankingApp {

    // ── Fields ───────────────────────────────────────────────
    private double balance = 0;
    private final Scanner scanner = new Scanner(System.in);

    // ── Entry point called from BankingApp ───────────────────
    public void run() {
        printWelcome();

        boolean isRunning = true;

        while (isRunning) {
            printMenu();
            int choice = readInt("Enter your choice (1-4): ");

            switch (choice) {
                case 1 -> showBalance();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> {
                    printGoodbye();
                    isRunning = false;
                }
                default -> System.out.println("⚠️  Please enter a number between 1 and 4.\n");
            }
        }

        scanner.close();
    }
}