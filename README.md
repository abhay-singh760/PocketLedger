<div align="center">

# 💸 PocketLedger

### A lightweight, offline command-line expense tracker built with Java

Record expenses, group spending by category, and see where your money goes, all from your terminal. No database, no internet, no setup.

[![CI](https://img.shields.io/github/actions/workflow/status/YOUR_USERNAME/PocketLedger/ci.yml?branch=main&style=for-the-badge&logo=githubactions&logoColor=white&label=CI)](https://github.com/YOUR_USERNAME/PocketLedger/actions)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/Tests-JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

![Storage](https://img.shields.io/badge/storage-local_CSV-informational?style=flat-square)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-blue?style=flat-square)
![Maintained](https://img.shields.io/badge/maintained-yes-success?style=flat-square)

[Features](#-features) · [Quick Start](#-quick-start) · [Commands](#-command-reference) · [Data Storage](#-data-storage) · [Testing](#-testing) · [Roadmap](#-roadmap) · [Contributing](#-contributing)

</div>

---

## 📖 Overview

**PocketLedger** is a command-line personal expense manager. It lets you record daily expenses, organize them by category, view your total spending, and delete entries by ID.

Records are saved to a local CSV file, so your data persists between runs and stays on your machine. There is no external database and no internet connection required.

## ✨ Features

| Feature | Description |
|---|---|
| ➕ **Add expenses** | Record an amount, category, and description |
| 📋 **Transaction history** | List every recorded expense with ID, date, amount, category, and description |
| 🧮 **Expense summary** | See total spending across all transactions |
| 🗂️ **Category analytics** | View spending grouped by category |
| 🗑️ **Delete transactions** | Remove an expense by its unique ID |
| 💾 **Persistent storage** | Records are saved automatically to a local CSV file |
| 🛡️ **Input validation** | Rejects invalid amounts and empty required fields |
| 📄 **CSV-safe** | Handles commas and quotation marks in descriptions |
| ✅ **Unit tested** | JUnit 5 test coverage for core behavior |
| 🔁 **Continuous integration** | GitHub Actions runs the tests on every push and pull request |

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application development |
| Maven | Build and dependency management |
| JUnit 5 | Unit testing |
| CSV | Local data persistence |
| GitHub Actions | Continuous integration |

## 🚀 Quick Start

### Prerequisites

- JDK 17 or later
- Apache Maven 3.8 or later
- Git (optional)

### Install and run

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/PocketLedger.git
cd PocketLedger

# 2. Build and package
mvn clean package

# 3. See available commands
java -jar target/pocketledger-1.0.0.jar help
```

### Run the tests

```bash
mvn test
```

## 💻 Command Reference

```text
java -jar target/pocketledger-1.0.0.jar <command> [arguments]
```

| Command | Usage | Description |
|---|---|---|
| `add` | `add <amount> <category> "<description>"` | Record a new expense |
| `list` | `list` | Show all recorded expenses |
| `summary` | `summary` | Show spending per category and the overall total |
| `delete` | `delete <id>` | Remove the expense with the given ID |
| `help` | `help` | Show usage information |

> [!TIP]
> Wrap descriptions that contain spaces in quotes, e.g. `"Bus ticket"`.

### Examples

**Add an expense**

```bash
java -jar target/pocketledger-1.0.0.jar add 250 Food "Lunch"
```

```text
Expense added successfully (ID: 1).
```

**List all expenses**

```bash
java -jar target/pocketledger-1.0.0.jar list
```

Shows the transaction ID, date, amount, category, and description of each expense.

**View the summary**

```bash
java -jar target/pocketledger-1.0.0.jar summary
```

**Delete an expense**

```bash
java -jar target/pocketledger-1.0.0.jar delete 1
```

## 📊 Example Workflow

```text
$ java -jar target/pocketledger-1.0.0.jar add 250 Food "Lunch"
Expense added successfully (ID: 1).

$ java -jar target/pocketledger-1.0.0.jar add 120 Travel "Bus ticket"
Expense added successfully (ID: 2).

$ java -jar target/pocketledger-1.0.0.jar summary

EXPENSE SUMMARY
  Food                   250.00
  Travel                 120.00
------------------------------
  TOTAL                  370.00
```

*Output shown for illustration.*

## 💾 Data Storage

Expenses are stored locally in:

```text
data/expenses.csv
```

The `data/` directory and the CSV file are created automatically when you record your first expense.

```csv
id,date,amount,category,description
1,2026-11-14,250.00,"Food","Lunch"
2,2026-11-14,120.00,"Travel","Bus ticket"
```

Because the data is plain CSV, you can open it in any spreadsheet app, back it up by copying the file, or version it however you like.

## 🏗️ Project Structure

The project is deliberately simple to keep the codebase easy to read and maintain.

```text
PocketLedger/
├── src/
│   ├── main/java/
│   │   └── PocketLedger.java        Application and CLI logic
│   └── test/java/
│       └── PocketLedgerTest.java    JUnit 5 tests
├── .github/
│   └── workflows/
│       └── ci.yml                   CI: build and test on every push
├── .gitignore
├── LICENSE
├── pom.xml
└── README.md
```

## 🧪 Testing

The test suite covers:

- Saving and retrieving expenses
- Category-wise totals
- Overall expenditure
- Deleting existing transactions
- Rejecting non-positive amounts
- Special characters in CSV descriptions

```bash
mvn clean test
```

## 🔁 Continuous Integration

A GitHub Actions workflow runs on every push and pull request. It:

1. Checks out the repository
2. Sets up Java 17
3. Caches Maven dependencies
4. Runs the automated test suite

This catches regressions before changes are merged.

## ⚠️ Current Limitations

- Designed for a single local user
- Uses a local CSV file instead of a database
- Does not support simultaneous writes from multiple processes
- No cloud synchronization
- No graphical interface

## 🗺️ Roadmap

- [ ] Monthly and yearly expense reports
- [ ] Date-range filtering
- [ ] Custom budget limits and alerts
- [ ] CSV report export
- [ ] Expense editing
- [ ] Interactive terminal interface
- [ ] Graphical spending analytics

Have an idea? [Open an issue](https://github.com/YOUR_USERNAME/PocketLedger/issues) to discuss it.

## 🤝 Contributing

Contributions and suggestions are welcome.

1. **Fork** the repository
2. **Create a branch:** `git checkout -b feature/your-feature`
3. **Make your changes** and add or update tests
4. **Verify locally:** `mvn clean verify`
5. **Commit:** `git commit -m "Add your feature"`
6. **Push and open a Pull Request:** `git push origin feature/your-feature`

Please make sure all tests pass before opening a PR. CI runs the same build on every pull request.

## 🐛 Reporting Issues

Found a bug? [Open an issue](https://github.com/YOUR_USERNAME/PocketLedger/issues) with:

- The command you ran
- The output you expected and the output you got
- Your Java version (`java -version`)

## 📄 License

Released under the [MIT License](LICENSE).

---

<div align="center">

**Built with Java ☕**

*Simple expense tracking. Clean code. Better financial awareness.*

If PocketLedger is useful to you, consider giving it a ⭐ on GitHub.

</div>
