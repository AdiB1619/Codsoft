package com.atm.service;

import com.atm.model.BankAccount;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;
import com.atm.util.ConsoleUI;

import java.io.Console;
import java.util.Scanner;

/**
 * Service responsible for user authentication and PIN management.
 */
public class AuthenticationService {

    private BankAccount account;
    private Scanner scanner;

    /**
     * Constructs an AuthenticationService for the session.
     * 
     * @param account the account to authenticate against
     * @param scanner the shared Scanner for console input
     */
    public AuthenticationService(BankAccount account, Scanner scanner) {
        this.account = account;
        this.scanner = scanner;
    }

    /**
     * Handles the login loop. Prompts for PIN up to MAX_FAILED_ATTEMPTS.
     * Locks the account if all attempts fail.
     * 
     * @return true if successfully authenticated, false otherwise
     */
    public boolean authenticate() {
        if (account.isLocked()) {
            showLockedMessage();
            return false;
        }

        int attemptsLeft = BankAccount.MAX_FAILED_ATTEMPTS;

        while (attemptsLeft > 0) {
            String input = readPIN("Enter your 4-digit PIN: ");
            
            if (account.verifyPIN(input)) {
                // Log LOGIN_SUCCESS transaction (amount 0, type LOGIN_SUCCESS)
                Transaction successTxn = new Transaction(
                        TransactionType.LOGIN_SUCCESS, 
                        0.0, 
                        account.getBalance(), 
                        "User logged in successfully"
                );
                account.addTransaction(successTxn);
                
                ConsoleUI.showMessage("\n*********************************");
                ConsoleUI.showMessage("* WELCOME, " + account.getAccountHolderName().toUpperCase() + " *");
                ConsoleUI.showMessage("*********************************");
                return true;
            } else {
                attemptsLeft--;
                if (attemptsLeft > 0) {
                    ConsoleUI.showError("Incorrect PIN. " + attemptsLeft + " attempt(s) remaining.");
                }
            }
        }

        // Exhausted all attempts
        account.setLocked(true);
        Transaction failedTxn = new Transaction(
                TransactionType.LOGIN_FAILED,
                0.0,
                account.getBalance(),
                "Failed login - max attempts exceeded"
        );
        account.addTransaction(failedTxn);
        showLockedMessage();
        
        return false;
    }

    /**
     * Displays a formatted error box for a locked account.
     */
    private void showLockedMessage() {
        ConsoleUI.showMessage("\n+---------------------------------------------------+");
        ConsoleUI.showMessage("|  ERROR: Your account is locked.                   |");
        ConsoleUI.showMessage("|  Please contact bank support to unlock it.        |");
        ConsoleUI.showMessage("+---------------------------------------------------+\n");
    }

    /**
     * Bonus feature to change the PIN.
     * 
     * @param sc scanner for input
     * @return true if successfully changed
     */
    public boolean changePIN() {
        String currentPIN = readPIN("Enter current PIN: ");
        if (!account.verifyPIN(currentPIN)) {
            ConsoleUI.showError("Authentication failed. Cannot change PIN.");
            return false;
        }

        String newPIN1;
        try {
            newPIN1 = com.atm.util.InputValidator.validatePIN(readPIN("Enter new 4-digit PIN: "));
        } catch (com.atm.exception.InvalidPINException e) {
            ConsoleUI.showError(e.getMessage());
            return false;
        }

        String newPIN2 = readPIN("Confirm new PIN: ");
        if (!newPIN1.equals(newPIN2)) {
            ConsoleUI.showError("PINs do not match. Operation cancelled.");
            return false;
        }

        account.setHashedPIN(com.atm.util.InputValidator.hashPIN(newPIN1));
        
        // Log transaction
        Transaction pinChangeTxn = new Transaction(
                TransactionType.DEPOSIT,
                0.0,
                account.getBalance(),
                "PIN changed successfully"
        );
        account.addTransaction(pinChangeTxn);
        
        ConsoleUI.showMessage("PIN changed successfully!");
        return true;
    }

    /**
     * Reads a PIN from the console, masking input if possible.
     * We check if System.console() is available because some IDEs or environments 
     * don't provide a secure console, in which case we fall back to standard Scanner.
     * 
     * @param prompt the message to show the user
     * @return the trimmed PIN string
     */
    private String readPIN(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] passwordArray = console.readPassword(prompt);
            return new String(passwordArray).trim();
        } else {
            // Fallback for environments where console is unavailable (e.g., standard IDE output)
            System.out.print(prompt);
            return scanner.nextLine().trim();
        }
    }
}
