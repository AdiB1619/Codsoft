package com.atm.service;

import com.atm.model.BankAccount;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Service responsible for generating and displaying account statements.
 */
public class StatementService {

    private BankAccount account;
    private Scanner scanner;

    private static final boolean ANSI_ENABLED = !(System.getenv("OS") != null && System.getenv("OS").contains("Windows"));

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";

    /**
     * Constructs a StatementService.
     * 
     * @param account the account to operate on
     * @param scanner the shared scanner for user input
     */
    public StatementService(BankAccount account, Scanner scanner) {
        this.account = account;
        this.scanner = scanner;
    }

    /**
     * Prints a formatted balance display in a box.
     */
    public void showBalance() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
        String balanceStr = "₹" + new DecimalFormat("#,##0.00").format(account.getBalance());
        String limitStr = "₹" + new DecimalFormat("#,##0.00").format(account.getRemainingDailyLimit());
        
        System.out.println(colorWrap("┌──────────────────────────────────────┐", ANSI_CYAN));
        System.out.println(colorWrap("│           ACCOUNT BALANCE            │", ANSI_CYAN));
        printDivider(38);
        System.out.printf("│  Account : %-25s │%n", account.getAccountNumber());
        System.out.printf("│  Holder  : %-25s │%n", truncate(account.getAccountHolderName(), 25));
        System.out.printf("│  Balance : %-25s │%n", balanceStr);
        System.out.printf("│  Daily Limit Remaining: %-13s │%n", limitStr);
        System.out.printf("│  Date    : %-25s │%n", dateStr);
        System.out.println("└──────────────────────────────────────┘");
    }

    /**
     * Displays the last 5 transactions from the account history.
     */
    public void showMiniStatement() {
        System.out.print(buildStatementText(5, true));
    }

    /**
     * Returns the mini statement as a formatted String for file export.
     * 
     * @return formatted mini statement string (without ANSI codes)
     */
    public String generateMiniStatementText() {
        return buildStatementText(5, false);
    }

    /**
     * Displays all transactions paginated (10 per page).
     */
    public void showFullHistory() {
        List<Transaction> history = account.getTransactionHistory();
        int total = history.size();
        System.out.println("\nShowing " + total + " transactions");
        
        if (total == 0) {
            System.out.println("No transactions yet.");
            return;
        }

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) total / pageSize);

        for (int page = 0; page < totalPages; page++) {
            System.out.println(colorWrap("┌────────────────────────────────────────────────────────────┐", ANSI_CYAN));
            
            // Format title and ensure correct padding
            String title = String.format("TRANSACTION HISTORY (Page %d/%d)", (page + 1), totalPages);
            int paddingLeft = (60 - title.length()) / 2;
            int paddingRight = 60 - title.length() - paddingLeft;
            String paddedTitle = " ".repeat(paddingLeft) + title + " ".repeat(paddingRight);
            
            System.out.println(colorWrap("│" + paddedTitle + "│", ANSI_CYAN));
            printDivider(60);
            
            int start = page * pageSize;
            int end = Math.min(start + pageSize, total);
            
            for (int i = start; i < end; i++) {
                Transaction t = history.get(i);
                System.out.println(formatTransactionLine(t, 60, true));
            }
            System.out.println("└────────────────────────────────────────────────────────────┘");
            
            if (page < totalPages - 1) {
                System.out.print("Press ENTER for next page or Q to quit: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("Q")) {
                    break;
                }
            }
        }
    }

    /**
     * Helper to build the mini statement text, optionally with ANSI colors.
     */
    private String buildStatementText(int limit, boolean useAnsi) {
        List<Transaction> history = account.getTransactionHistory();
        StringBuilder sb = new StringBuilder();

        String headerColor = useAnsi ? ANSI_CYAN : "";
        String reset = useAnsi ? ANSI_RESET : "";

        sb.append(colorWrap("┌────────────────────────────────────────────────────────────┐\n", headerColor, reset));
        sb.append(colorWrap("│                       MINI STATEMENT                       │\n", headerColor, reset));
        sb.append(buildDivider(60)).append("\n");

        if (history.isEmpty()) {
            sb.append("│                    No transactions yet.                    │\n");
        } else {
            int count = Math.min(history.size(), limit);
            for (int i = 0; i < count; i++) {
                Transaction t = history.get(i);
                sb.append(formatTransactionLine(t, 60, useAnsi)).append("\n");
            }
        }
        sb.append("└────────────────────────────────────────────────────────────┘\n");
        return sb.toString();
    }

    /**
     * Formats a single transaction line to fit a specific box width.
     */
    private String formatTransactionLine(Transaction t, int boxWidth, boolean useAnsi) {
        String dateStr = t.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MMM HH:mm"));
        String desc = truncate(t.getType().getDisplayName(), 15);
        String amt = t.getType().getSymbol() + "₹" + new DecimalFormat("#,##0.00").format(t.getAmount());
        
        // We want the layout: │ [Date] Desc       |      Amt │
        String paddedAmt = String.format("%15s", amt);
        
        if (useAnsi && ANSI_ENABLED) {
            if (t.getType() == TransactionType.DEPOSIT || t.getType() == TransactionType.LOGIN_SUCCESS) {
                paddedAmt = ANSI_GREEN + paddedAmt + ANSI_RESET;
            } else if (t.getType() == TransactionType.WITHDRAWAL || t.getType() == TransactionType.LOGIN_FAILED) {
                paddedAmt = ANSI_RED + paddedAmt + ANSI_RESET;
            }
        }
        
        String core = String.format(" %-12s | %-15s | %s ", dateStr, desc, paddedAmt);
        
        // Calculate remaining padding to reach boxWidth
        int visibleCoreLength = 50; 
        int paddingLength = boxWidth - visibleCoreLength;
        String padding = " ".repeat(Math.max(0, paddingLength));
        
        return "│" + core + padding + "│";
    }

    /**
     * Prints "─" repeated width times inside a border.
     */
    private void printDivider(int width) {
        System.out.println(buildDivider(width));
    }

    private String buildDivider(int width) {
        StringBuilder sb = new StringBuilder("├");
        for (int i = 0; i < width; i++) {
            sb.append("─");
        }
        sb.append("┤");
        return sb.toString();
    }

    private String colorWrap(String text, String color) {
        return colorWrap(text, color, ANSI_RESET);
    }
    
    private String colorWrap(String text, String color, String reset) {
        if (ANSI_ENABLED && color != null && !color.isEmpty()) {
            return color + text + reset;
        }
        return text;
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}
