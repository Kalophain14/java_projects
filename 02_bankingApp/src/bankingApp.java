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

        boolean isRunning = true; // To always ask the user option

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
                default -> System.out.println("  Please enter a number between 1 and 4.\n");
            }
        }
        scanner.close();
    }

    // ── Menu options ─────────────────────────────────────────

    private void showBalance() {
        printHeader("BALANCE");
        System.out.printf("  Your current balance is: R%.2f%n%n", balance);
    }

    private void deposit() {
        printHeader("DEPOSIT");
        System.out.printf("  Current balance: R%.2f%n", balance);

        double amount = readDouble("  How much would you like to deposit? R");

        if (amount <= 0) {
            System.out.println("  Deposit amount must be greater than zero.\n");
            return; //stops
        }

        balance += amount;
        System.out.printf("  Deposited R%.2f  |  New balance: R%.2f%n%n", amount, balance);
    }

    private void withdraw() {
        printHeader("WITHDRAW");
        System.out.printf("  Current balance: R%.2f%n", balance);

        double amount = readDouble("  How much would you like to withdraw? R");

        if (amount <= 0) {
            System.out.println("  Withdrawal amount must be greater than zero.\n");
            return; //stops
        }

        if (amount > balance) {
            System.out.printf("  Insufficient funds. You only have R%.2f available.%n%n", balance);
            return; //stops
        }

        balance -= amount;
        System.out.printf("  Withdrew R%.2f  |  New balance: R%.2f%n%n", amount, balance);
    }

    // ── Input helpers ────────────────────────────────────────

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("  Invalid input. Enter a number: ");
            scanner.next(); // discard bad input
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // clear leftover newline
        return value;
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("  Invalid input. Enter a number: ");
            scanner.next(); // discard bad input
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // clear leftover newline
        return value;
    }

    // ── Display helpers ──────────────────────────────────────
    // This shows the user the options they have to chose

    private void printWelcome() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║    Welcome to SA Bank        ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("┌──────────────────────────────┐");
        System.out.println("│         MAIN MENU            │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1.  Show Balance            │");
        System.out.println("│  2.  Deposit                 │");
        System.out.println("│  3.  Withdraw                │");
        System.out.println("│  4.  Exit                    │");
        System.out.println("└──────────────────────────────┘");
    }

    private void printHeader(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 26 - title.length())));
    }

    private void printGoodbye() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║  Thanks for banking with us! ║");
        System.out.println("║         Goodbye!             ║");
        System.out.println("╚══════════════════════════════╝");
    }

}