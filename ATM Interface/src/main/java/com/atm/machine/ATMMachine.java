package com.atm.machine;

import com.atm.exception.DailyLimitExceededException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;
import com.atm.model.BankAccount;
import com.atm.model.Transaction;
import com.atm.service.AuthenticationService;
import com.atm.service.StatementService;
import com.atm.service.TransactionService;
import com.atm.storage.FileStorageManager;
import com.atm.util.ConsoleUI;

import java.util.Scanner;

/**
 * Core controller class representing the ATM machine itself.
 * Manages the main event loop and coordinates interactions between 
 * the UI, authentication, and transaction services.
 */
public class ATMMachine {

    private static boolean DEBUG = false;
    private BankAccount currentAccount;
    private AuthenticationService authService;
    private TransactionService txnService;
    private StatementService stmtService;
    private FileStorageManager storageManager;
    private boolean isAuthenticated;
    private Scanner scanner;

    /**
     * Initializes the ATM Machine, creates service instances, and loads account data.
     */
    public ATMMachine() {
        this.scanner = new Scanner(System.in);
        this.storageManager = new FileStorageManager();
        this.storageManager.ensureDataDirectoryExists();
        this.isAuthenticated = false;

        BankAccount loaded = storageManager.loadAccount();
        if (loaded == null) {
            this.currentAccount = new BankAccount("ACC-0001", "Rahul Sharma", "1234", 10000.0);
            System.out.println("No account data found. Demo account created. PIN: 1234");
            saveState();
        } else {
            this.currentAccount = loaded;
            java.util.List<Transaction> history = storageManager.loadTransactions();
            for (Transaction t : history) {
                this.currentAccount.addTransaction(t);
            }
        }
        
        this.txnService = new TransactionService(this.currentAccount);
        this.authService = new AuthenticationService(this.currentAccount, this.scanner);
        this.stmtService = new StatementService(this.currentAccount, this.scanner);
    }

    /**
     * Starts the ATM lifecycle.
     */
    public void start() {
        authenticate();
        if (isAuthenticated) {
            showMenu();
        }
    }

    /**
     * Handles the user authentication process.
     * Uses the AuthenticationService to verify the PIN. Exits if max attempts reached.
     */
    public void authenticate() {
        ConsoleUI.clearScreen();
        ConsoleUI.showBanner();
        
        this.isAuthenticated = authService.authenticate();
        if (!this.isAuthenticated) {
            handleExit();
        }
    }

    /**
     * Displays the main ATM menu and processes user input in a loop.
     */
    public void showMenu() {
        while (isAuthenticated) {
            ConsoleUI.clearScreen();
            ConsoleUI.showBanner();
            ConsoleUI.showMainMenu();

            int choice = ConsoleUI.promptMenuChoice(scanner, 1, 7);

            switch (choice) {
                case 1:
                    handleBalance();
                    break;
                case 2:
                    handleDeposit();
                    break;
                case 3:
                    handleWithdraw();
                    break;
                case 4:
                    handleHistory();
                    break;
                case 5:
                    handleMiniStatement();
                    break;
                case 6:
                    if (authService.changePIN()) {
                        saveState();
                    }
                    break;
                case 7:
                    handleExit();
                    break;
                default:
                    ConsoleUI.showError("Invalid choice. Please select a valid menu option.");
            }
            if (isAuthenticated) {
                ConsoleUI.pressEnterToContinue(scanner);
            }
        }
    }

    /**
     * Handles cash withdrawals by interacting with the TransactionService.
     */
    public void handleWithdraw() {
        try {
            double amount = ConsoleUI.promptAmount(scanner, "Enter withdrawal amount: ₹");
            txnService.withdraw(amount);
            ConsoleUI.showSuccess("Withdrawal of ₹" + amount + " successful. Please collect your cash.");
            saveState();
        } catch (InsufficientFundsException e) {
            ConsoleUI.showError(e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (DailyLimitExceededException e) {
            ConsoleUI.showError(e.getMessage()); // Exception natively holds remaining limit info now
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (InvalidAmountException e) {
            ConsoleUI.showError(e.getReason());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (com.atm.exception.AccountLockedException e) {
            ConsoleUI.showError(e.getMessage());
            handleExit();
        } catch (NumberFormatException e) {
            ConsoleUI.showError("Please enter a valid number.");
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (java.util.InputMismatchException e) {
            ConsoleUI.showError("Invalid input type. Numbers only.");
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (com.atm.exception.ATMException e) {
            ConsoleUI.showError("ATM Error [" + e.getErrorCode() + "]: " + e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (Exception e) {
            ConsoleUI.showError("Unexpected error. Please try again.");
            logException(e);
            ConsoleUI.pressEnterToContinue(scanner);
        }
    }

    /**
     * Handles cash deposits by interacting with the TransactionService.
     */
    public void handleDeposit() {
        try {
            double amount = ConsoleUI.promptAmount(scanner, "Enter deposit amount: ₹");
            txnService.deposit(amount);
            ConsoleUI.showSuccess("Deposit of ₹" + amount + " successful.");
            saveState();
        } catch (InsufficientFundsException e) {
            ConsoleUI.showError(e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (DailyLimitExceededException e) {
            ConsoleUI.showError(e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (InvalidAmountException e) {
            ConsoleUI.showError(e.getReason());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (com.atm.exception.AccountLockedException e) {
            ConsoleUI.showError(e.getMessage());
            handleExit();
        } catch (NumberFormatException e) {
            ConsoleUI.showError("Please enter a valid number.");
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (java.util.InputMismatchException e) {
            ConsoleUI.showError("Invalid input type. Numbers only.");
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (com.atm.exception.ATMException e) {
            ConsoleUI.showError("ATM Error [" + e.getErrorCode() + "]: " + e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        } catch (Exception e) {
            ConsoleUI.showError("Unexpected error. Please try again.");
            logException(e);
            ConsoleUI.pressEnterToContinue(scanner);
        }
    }

    /**
     * Handles balance inquiries.
     * Records a balance inquiry transaction and displays the current balance.
     */
    public void handleBalance() {
        stmtService.showBalance();
        
        // Record the inquiry as a transaction event
        txnService.recordBalanceInquiry();
    }

    /**
     * Handles displaying the full transaction history.
     */
    public void handleHistory() {
        stmtService.showFullHistory();
    }

    /**
     * Handles displaying a mini statement (last 5 transactions).
     */
    public void handleMiniStatement() {
        stmtService.showMiniStatement();
    }

    /**
     * Safely shuts down the ATM session, saving all state and exiting.
     */
    public void handleExit() {
        ConsoleUI.showInfo(String.format("Account Holder: %s | Current Balance: ₹%,.2f", currentAccount.getAccountHolderName(), currentAccount.getBalance()));
        saveState();
        System.out.println("Thank you for banking with us. Goodbye! 👋");
        if (scanner != null) {
            scanner.close();
        }
        System.exit(0);
    }

    /**
     * Persists the current account state (balance, transactions, etc.) to storage.
     */
    public final void saveState() {
        if (storageManager != null && currentAccount != null) {
            storageManager.saveAccount(currentAccount);
            storageManager.saveTransactions(currentAccount.getTransactionHistory());
            if (DEBUG) {
                System.err.println("State saved successfully.");
            }
        }
    }

    /**
     * Logs an exception to a local text file without crashing the application.
     */
    private void logException(Exception e) {
        try {
            java.io.File dir = new java.io.File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (java.io.FileWriter fw = new java.io.FileWriter("data/error_log.txt", true);
                 java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                pw.println(java.time.LocalDateTime.now() + " - " + e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace(pw);
                pw.println("--------------------------------------------------");
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.err.println("Note: Could not write to error log file (FileNotFound).");
        } catch (java.io.IOException ioe) {
            System.err.println("Note: Error writing to log file: " + ioe.getMessage());
        }
    }
}
