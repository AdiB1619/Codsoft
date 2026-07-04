# 🏧 ATM Banking System — Java Console Application

![Java Version](https://img.shields.io/badge/Java-17-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Active-success.svg)

A robust, object-oriented console application simulating a real-world ATM interface. 
Built as a Virtual Internship Project, focusing on clean architecture, solid error handling, and file-based data persistence.

## ✨ Features
- 🔐 **Secure Authentication**: SHA-256 hashed PINs with a 3-attempt lockout mechanism.
- 💵 **Cash Deposits**: Validates positive amounts with upper limits per transaction.
- 🏧 **Cash Withdrawals**: Enforces ₹100 denomination multiples, minimum balance, and ₹50,000 daily limits.
- 📊 **Balance Inquiry**: View formatted, up-to-date account balances and remaining daily limits.
- 📜 **Transaction History**: Tracks all account activity with full timestamps and unique transaction IDs.
- 🧾 **Mini Statement**: Quickly view the 5 most recent transactions.
- 🔄 **PIN Management**: Securely change your PIN by verifying your current one first.
- 💾 **State Persistence**: Uses local `.txt` and `.csv` files to preserve accounts and transactions across reboots.
- 🛡️ **Robust Error Handling**: Deep custom exception hierarchy ensuring the application never crashes unpredictably.
- 🎨 **Rich UI Output**: Clean ASCII banners, structured boxes, and localized currency (₹) formatting.

## 🏗️ Project Architecture
The project adheres to a clean `com.atm` package structure, separating concerns across domains:

```text
src/main/java/com/atm/
├── exception/        # Custom unchecked exceptions (ATMException, InsufficientFundsException, etc.)
├── machine/          # The core controller and state manager (ATMMachine)
├── main/             # The application entry point (Main)
├── model/            # Plain Java objects (BankAccount, Transaction, TransactionType)
├── service/          # Business logic (AuthenticationService, TransactionService, StatementService)
├── storage/          # File I/O operations and persistence (FileStorageManager)
└── util/             # Static helpers (ConsoleUI, InputValidator)
```

## 🧩 OOP Design
The system uses modern Object-Oriented principles to keep components decoupled and highly cohesive.
The `ATMMachine` acts as the central controller, holding the active `BankAccount` session. It delegates business logic to specialized services like the `TransactionService` (for processing financial math and limits) and `AuthenticationService` (for PIN validation). 
- **Service Layer Pattern**: Operations are encapsulated into dedicated services rather than bloating the domain models.
- **Repository Pattern**: `FileStorageManager` abstracts away all `java.io` filesystem operations, meaning the core ATM logic doesn't care if data comes from a text file, CSV, or (eventually) a database.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+ (optional, for running tests and packaging)

### Clone and Run
You can compile and run this project using standard Java commands:

```bash
git clone https://github.com/yourusername/java-atm-banking-system.git
cd java-atm-banking-system
javac -d out -sourcepath src/main/java src/main/java/com/atm/main/Main.java
java -cp out com.atm.main.Main
```

**Or with Maven:**
```bash
mvn clean package
java -jar target/atm-banking-system-1.0.0.jar
```

## 👤 Demo Account
If no existing data is found on first launch, the system automatically creates a demo account:

| Field | Value |
|-------|-------|
| **Account Name** | Rahul Sharma |
| **Account No.** | `ACC-0001` |
| **PIN** | `1234` |
| **Starting Balance**| ₹10,000.00 |

## 📸 Sample Output

**Main Menu**
```text
╔═══════════════════════════════════════════╗
║     🏧  JAVA BANKING SYSTEM  v1.0         ║
║         Powered by [Your Name]            ║
╚═══════════════════════════════════════════╝
┌─────────────────────────────┐
│        MAIN MENU            │
├─────────────────────────────┤
│  1. Check Balance           │
│  2. Deposit Cash            │
│  3. Withdraw Cash           │
│  4. Transaction History     │
│  5. Mini Statement          │
│  6. Change PIN              │
│  7. Quit                    │
└─────────────────────────────┘
Enter your choice (1-7):
```

**Balance Display**
```text
┌──────────────────────────────────┐
│         ACCOUNT BALANCE          │
├──────────────────────────────────┤
│  Account : ACC-0001              │
│  Holder  : Rahul Sharma          │
│  Balance : ₹15,000.00            │
│  Date    : 15-Jun-2024 14:32     │
└──────────────────────────────────┘
```

## 🧪 Running Tests
The project includes a robust JUnit 5 test suite validating core transactional logic. To run the tests:
```bash
mvn test
```

## 🛣️ Future Enhancements
- Multiple accounts support
- REST API integration
- GUI with JavaFX
- Database integration with JDBC

## 📄 License
MIT

## 🙋 Author
**[Your Name]** — Virtual Internship Project 2024
