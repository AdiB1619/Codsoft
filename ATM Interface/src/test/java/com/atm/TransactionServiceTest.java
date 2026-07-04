package com.atm;

import com.atm.exception.DailyLimitExceededException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;
import com.atm.model.BankAccount;
import com.atm.model.TransactionType;
import com.atm.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    private BankAccount account;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        // Fresh account with ₹10,000 balance and "1234" PIN
        account = new BankAccount("ACC-0001", "Rahul Sharma", "1234", 10000.0);
        service = new TransactionService(account);
    }

    @Nested
    @DisplayName("Deposit Tests")
    class DepositTests {

        @Test
        @DisplayName("Deposit valid amount updates balance")
        void testDepositValidAmount_updatesBalance() {
            service.deposit(5000.0);
            assertEquals(15000.0, account.getBalance(), "Balance should increase by 5000");
        }

        @Test
        @DisplayName("Deposit zero amount throws InvalidAmountException")
        void testDepositZeroAmount_throwsInvalidAmountException() {
            assertThrows(InvalidAmountException.class, () -> service.deposit(0.0));
        }

        @Test
        @DisplayName("Deposit negative amount throws InvalidAmountException")
        void testDepositNegativeAmount_throwsInvalidAmountException() {
            assertThrows(InvalidAmountException.class, () -> service.deposit(-500.0));
        }

        @Test
        @DisplayName("Deposit exceeds maximum single deposit throws InvalidAmountException")
        void testDepositExceedsMaxSingleDeposit_throwsInvalidAmountException() {
            // max single deposit is 100000.0
            assertThrows(InvalidAmountException.class, () -> service.deposit(200000.0));
        }

        @Test
        @DisplayName("Deposit adds transaction to history")
        void testDepositAddsTransactionToHistory() {
            service.deposit(5000.0);
            assertEquals(1, account.getTransactionHistory().size());
            assertEquals(TransactionType.DEPOSIT, account.getTransactionHistory().get(0).getType());
            assertNotNull(account.getTransactionHistory().get(0).getTransactionId());
        }
    }

    @Nested
    @DisplayName("Withdrawal Tests")
    class WithdrawalTests {

        @Test
        @DisplayName("Withdraw valid amount updates balance")
        void testWithdrawValidAmount_updatesBalance() {
            service.withdraw(1000.0);
            assertEquals(9000.0, account.getBalance(), "Balance should decrease by 1000");
        }

        @Test
        @DisplayName("Withdraw causes insufficient funds throws InsufficientFundsException")
        void testWithdrawCausesInsufficientFunds_throwsException() {
            // Withdraw ₹10,000 (would break minimum balance of ₹500)
            assertThrows(InsufficientFundsException.class, () -> service.withdraw(10000.0));
        }

        @Test
        @DisplayName("Withdraw non-multiple of 100 throws InvalidAmountException")
        void testWithdrawNonMultipleOf100_throwsInvalidAmountException() {
            assertThrows(InvalidAmountException.class, () -> service.withdraw(150.0));
        }

        @Test
        @DisplayName("Withdraw exceeds daily limit throws DailyLimitExceededException")
        void testWithdrawExceedsDailyLimit_throwsDailyLimitExceededException() {
            // Setup: deposit enough to have the funds without hitting single-deposit max
            service.deposit(80000.0); // balance becomes 90000.0
            
            // Daily limit is 50,000. Trying to withdraw 60,000 should fail.
            assertThrows(DailyLimitExceededException.class, () -> service.withdraw(60000.0));
        }

        @Test
        @DisplayName("Withdraw adds transaction to history")
        void testWithdrawAddsTransactionToHistory() {
            service.withdraw(1000.0);
            assertEquals(1, account.getTransactionHistory().size());
            assertEquals(TransactionType.WITHDRAWAL, account.getTransactionHistory().get(0).getType());
            assertNotNull(account.getTransactionHistory().get(0).getTransactionId());
        }
    }

    @Nested
    @DisplayName("Balance Inquiry Tests")
    class BalanceInquiryTests {

        @Test
        @DisplayName("Balance inquiry adds transaction record")
        void testBalanceInquiry_addsTransactionRecord() {
            service.recordBalanceInquiry();
            assertEquals(1, account.getTransactionHistory().size());
            assertEquals(TransactionType.BALANCE_INQUIRY, account.getTransactionHistory().get(0).getType());
        }

        @Test
        @DisplayName("Balance inquiry does not change balance")
        void testBalanceInquiry_doesNotChangeBalance() {
            double initialBalance = account.getBalance();
            service.recordBalanceInquiry();
            assertEquals(initialBalance, account.getBalance(), "Balance should remain unchanged");
        }
    }
}
