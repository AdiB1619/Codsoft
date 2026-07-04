package com.atm.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class for managing console output, styling, and user input prompts.
 * Fully static, no instantiation needed.
 */
public class ConsoleUI {

    /**
     * Clears the console screen (simulated with newlines).
     */
    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Prints the main ATM banner.
     */
    public static void showBanner() {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     🏧  JAVA BANKING SYSTEM  v1.0         ║");
        System.out.println("║         Powered by Aditya Bachute         ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }

    /**
     * Prints the main menu options.
     */
    public static void showMainMenu() {
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│        MAIN MENU            │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│  1. Check Balance           │");
        System.out.println("│  2. Deposit Cash            │");
        System.out.println("│  3. Withdraw Cash           │");
        System.out.println("│  4. Transaction History     │");
        System.out.println("│  5. Mini Statement          │");
        System.out.println("│  6. Change PIN              │");
        System.out.println("│  7. Quit                    │");
        System.out.println("└─────────────────────────────┘");
    }

    /**
     * Prompts the user for a menu choice within the valid range.
     * 
     * @param scanner the scanner
     * @param min minimum valid option
     * @param max maximum valid option
     * @return the valid choice
     */
    public static int promptMenuChoice(Scanner scanner, int min, int max) {
        int choice = -1;
        while (true) {
            System.out.print("Enter your choice (" + min + "-" + max + "): ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    showError("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (InputMismatchException e) {
                showError("Invalid input type. Numbers only.");
                scanner.nextLine(); // consume the bad input
            }
        }
    }

    /**
     * Prompts the user for a monetary amount.
     * 
     * @param scanner the scanner
     * @param prompt the message prompt
     * @return the valid double amount
     */
    public static double promptAmount(Scanner scanner, String prompt) {
        double amount = -1.0;
        while (true) {
            System.out.print(prompt);
            try {
                amount = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                return amount;
            } catch (InputMismatchException e) {
                showError("Invalid input type. Numbers only.");
                scanner.nextLine(); // consume the bad input
            }
        }
    }

    /**
     * Prints a standard message.
     */
    public static void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints a success message (could be green in real ANSI terminals).
     */
    public static void showSuccess(String success) {
        System.out.println("[SUCCESS] " + success);
    }

    /**
     * Prints an error message (could be red in real ANSI terminals).
     */
    public static void showError(String error) {
        System.out.println("[ERROR] " + error);
    }

    /**
     * Prints an info message.
     */
    public static void showInfo(String info) {
        System.out.println("[INFO] " + info);
    }

    /**
     * Pauses execution until the user presses Enter.
     * 
     * @param scanner the scanner
     */
    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
