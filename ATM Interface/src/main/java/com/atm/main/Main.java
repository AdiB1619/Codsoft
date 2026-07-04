package com.atm.main;

import com.atm.exception.ATMException;
import com.atm.machine.ATMMachine;
import com.atm.util.ConsoleUI;

/**
 * Main entry point for the ATM Banking System application.
 *
 * <p>Initializes the top-level machine components, sets up critical error boundaries,
 * and begins the authentication lifecycle. Implements a safe shutdown hook to ensure
 * data consistency.</p>
 *
 * @author Aditya Bachute
 * @version 1.0
 * @since 2024
 */
public class Main {

    public static void main(String[] args) {
        ATMMachine atm = null;

        try {
            // Print the startup sequence
            ConsoleUI.clearScreen();
            ConsoleUI.showBanner();
            
            // Small pause for visual effect
            Thread.sleep(800);

            // Initialize the machine
            atm = new ATMMachine();
            
            // Create effectively final reference for the shutdown hook
            final ATMMachine finalAtm = atm; 

            // Add a JVM shutdown hook to capture Ctrl+C or unexpected kills
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nSystem shutdown detected. Saving data...");
                if (finalAtm != null) {
                    finalAtm.saveState();
                }
            }));

            // Launch the authentication flow
            atm.start();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Startup interrupted.");
            System.exit(2);
        } catch (ATMException e) {
            System.err.println("ATM system error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected system error. Please contact support.");
            e.printStackTrace();
            System.exit(2);
        }
    }
}
