package com.atm.storage;

import com.atm.model.BankAccount;
import com.atm.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all reading and writing of data to/from the filesystem.
 */
public class FileStorageManager {

    private static final String DATA_DIR = "data/";
    private static final String ACCOUNT_FILE = DATA_DIR + "account.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "transactions.txt";
    private static final String ERROR_LOG_FILE = DATA_DIR + "error_log.txt";

    /**
     * Creates data/ directory if it doesn't exist.
     */
    public void ensureDataDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    /**
     * Writes account fields in pipe-delimited format to ACCOUNT_FILE.
     * Also saves the account's transaction history to TRANSACTIONS_FILE.
     *
     * @param account the account to save
     */
    public void saveAccount(BankAccount account) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE))) {
            writer.write("accountNumber|" + account.getAccountNumber());
            writer.newLine();
            writer.write("accountHolderName|" + account.getAccountHolderName());
            writer.newLine();
            writer.write("hashedPIN|" + account.getHashedPIN());
            writer.newLine();
            writer.write("balance|" + account.getBalance());
            writer.newLine();
            writer.write("dailyWithdrawnAmount|" + account.getDailyWithdrawnAmount());
            writer.newLine();
            writer.write("lastWithdrawalDate|" + (account.getLastWithdrawalDate() != null ? account.getLastWithdrawalDate().toString() : "null"));
            writer.newLine();
            writer.write("isLocked|" + account.isLocked());
            writer.newLine();
            writer.write("failedAttempts|" + account.getFailedAttempts());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error saving account details: " + e.getMessage(), e);
        }
    }

    /**
     * Reads ACCOUNT_FILE line by line and reconstructs a BankAccount object.
     * Returns null if file doesn't exist or if it's corrupted.
     *
     * @return the loaded BankAccount, or null if loading failed
     */
    public BankAccount loadAccount() {
        File file = new File(ACCOUNT_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String accountNumber = null;
            String accountHolderName = null;
            String hashedPIN = null;
            double balance = 0.0;
            double dailyWithdrawnAmount = 0.0;
            LocalDate lastWithdrawalDate = null;
            boolean isLocked = false;
            int failedAttempts = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;
                String key = parts[0];
                String value = parts[1];

                switch (key) {
                    case "accountNumber":
                        accountNumber = value;
                        break;
                    case "accountHolderName":
                        accountHolderName = value;
                        break;
                    case "hashedPIN":
                        hashedPIN = value;
                        break;
                    case "balance":
                        balance = Double.parseDouble(value);
                        break;
                    case "dailyWithdrawnAmount":
                        dailyWithdrawnAmount = Double.parseDouble(value);
                        break;
                    case "lastWithdrawalDate":
                        if (!"null".equals(value)) {
                            lastWithdrawalDate = LocalDate.parse(value);
                        }
                        break;
                    case "isLocked":
                        isLocked = Boolean.parseBoolean(value);
                        break;
                    case "failedAttempts":
                        failedAttempts = Integer.parseInt(value);
                        break;
                }
            }

            if (accountNumber == null || accountHolderName == null) {
                return null;
            }

            // Create account with a dummy PIN, then overwrite with the loaded hashed PIN
            BankAccount account = new BankAccount(accountNumber, accountHolderName, "0000", balance);
            account.setHashedPIN(hashedPIN);
            account.setDailyWithdrawnAmount(dailyWithdrawnAmount);
            account.setLastWithdrawalDate(lastWithdrawalDate);
            account.setLocked(isLocked);
            account.setFailedAttempts(failedAttempts);

            return account;
        } catch (Exception e) {
            System.err.println("Error: Account file is corrupted. " + e.getMessage());
            return null;
        }
    }

    /**
     * Overwrites TRANSACTIONS_FILE with all transactions formatted as CSV.
     *
     * @param transactions the list of transactions to save
     */
    public void saveTransactions(List<Transaction> transactions) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.write("transactionId,type,amount,balanceAfter,timestamp,description");
            writer.newLine();
            for (Transaction t : transactions) {
                writer.write(t.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Reads TRANSACTIONS_FILE line by line and reconstructs Transaction objects.
     *
     * @return the list of loaded transactions, or empty list if none
     */
    public List<Transaction> loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        List<Transaction> transactions = new ArrayList<>();
        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    transactions.add(Transaction.fromCSV(line));
                } catch (Exception e) {
                    System.err.println("Warning: failed to parse transaction line: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading transactions: " + e.getMessage(), e);
        }
        
        return transactions;
    }

    /**
     * Appends error content to the ERROR_LOG_FILE.
     *
     * @param content the error message content to log
     */
    public void appendToErrorLog(String content) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ERROR_LOG_FILE, true))) {
            writer.write(LocalDateTime.now().toString() + " - " + content);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing to error log: " + e.getMessage(), e);
        }
    }
}
