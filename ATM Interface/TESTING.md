# ATM Console Application - Manual Test Plan

This document outlines the manual test scenarios for validating the ATM Banking System's functionality. 

## 1. Authentication
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-AUTH-01` | Correct PIN | `1234` | Login successful, Main Menu is displayed. | `[ ]` |
| `TC-AUTH-02` | Wrong PIN (1st Attempt) | `9999` | "Incorrect PIN. 2 attempt(s) remaining." | `[ ]` |
| `TC-AUTH-03` | Wrong PIN (2nd Attempt) | `9999`, then `8888` | "Incorrect PIN. 1 attempt(s) remaining." | `[ ]` |
| `TC-AUTH-04` | Wrong PIN (3rd Attempt / Lockout) | `9999`, `8888`, `7777` | "Your account is locked. Please contact bank support." The application forcefully exits. | `[ ]` |

## 2. Deposit Operations
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-DEP-01` | Valid Deposit | `1000` | "Deposit of ₹1000.0 successful." | `[ ]` |
| `TC-DEP-02` | Deposit Zero Amount | `0` | "Invalid amount: Amount must be greater than zero." | `[ ]` |
| `TC-DEP-03` | Deposit Negative Amount | `-500` | "Invalid amount: Amount must be greater than zero." | `[ ]` |
| `TC-DEP-04` | Deposit Exceeds Max Limit | `200000` | "Invalid amount: Amount exceeds maximum allowed per transaction." | `[ ]` |

## 3. Withdrawal Operations
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-WDL-01` | Valid Withdrawal | `500` | "Withdrawal of ₹500.0 successful. Please collect your cash." | `[ ]` |
| `TC-WDL-02` | Withdrawal in Multiples | `1000` | "Withdrawal of ₹1000.0 successful." | `[ ]` |
| `TC-WDL-03` | Non-Multiple of ₹100 | `150` | "Invalid amount: Amount must be in multiples of ₹100." | `[ ]` |
| `TC-WDL-04` | Breaches Minimum Balance | `9600` (on a ₹10,000 balance) | "Insufficient funds. Available: ₹10,000.00 (minimum balance ₹500 must be maintained)" | `[ ]` |
| `TC-WDL-05` | Hits Daily Limit | `55000` (assuming sufficient funds) | "Daily withdrawal limit exceeded. Remaining today: ₹50,000.00" | `[ ]` |
| `TC-WDL-06` | Withdrawal from Locked Account | Manipulate `account.txt` to `isLocked\|true` | Authentication blocks entry completely; displays locked message on boot. | `[ ]` |

## 4. Balance Inquiries
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-BAL-01` | View Account Balance | Option `1` | Renders a formatted ASCII box showing Account, Holder, Date, and correct Balance. | `[ ]` |
| `TC-BAL-02` | View Remaining Daily Limit | Option `1` | Renders "Daily Limit Remaining: ₹XX,XXX.00" alongside the balance. | `[ ]` |

## 5. Transaction History
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-HIS-01` | Empty History | Fresh Account -> Option `4` | "No transactions yet." | `[ ]` |
| `TC-HIS-02` | Single Transaction | Perform 1 deposit -> Option `4` | Displays 1 descending transaction record with Timestamp, ID, Type, Amount, and Balance. | `[ ]` |
| `TC-HIS-03` | 10+ Transactions (Pagination/List) | Perform 10+ transactions -> Option `4` | Renders all transactions correctly formatted (descending order). | `[ ]` |

## 6. Mini Statement
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-MIN-01` | Request Mini Statement | Option `5` (with >5 transactions in history) | Displays exactly the 5 most recent transactions, ignoring older ones. | `[ ]` |

## 7. File Persistence & State
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-PER-01` | Quit & Reopen Data Retention | Deposit `1000`, Exit (Option `7`), Restart App | Balance is ₹11,000.00, history contains the deposit. The `data/` text files are updated successfully. | `[ ]` |

## 8. Application Exit
| TC-ID | Scenario | Input | Expected Output | Pass/Fail |
|---|---|---|---|---|
| `TC-EXT-01` | Graceful Exit | Option `7` | Prints "Account Holder: [Name] | Current Balance: ₹X.XX", saves state one final time, and outputs "Thank you for banking with us. Goodbye! 👋". Application terminates safely with code 0. | `[ ]` |
