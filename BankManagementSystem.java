import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class BankAccount {
    private static int accountCounter = 0;

    private String accountNumber;
    private String accountHolder;
    private double balance;
    private String username;
    private String password;

    public BankAccount(String accountHolder, String username, String password) {
        this.accountNumber = generateAccountNumber();
        this.accountHolder = accountHolder;
        this.balance = 500; // Minimum balance requirement
        this.username = username;
        this.password = password;
    }

    private String generateAccountNumber() {
        accountCounter++;
        return "ACC" + accountCounter;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (balance - amount >= 500) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public void transfer(BankAccount recipient, double amount) {
        if (withdraw(amount)) {
            recipient.deposit(amount);
        } else {
            System.out.println("Insufficient funds for the transfer.");
        }
    }
}

public class BankManagementSystem extends JFrame {
    private BankAccount[] accounts;
    private BankAccount currentAccount;

    public BankManagementSystem() {
        accounts = new BankAccount[10]; // Maximum of 10 accounts
        creation();
    }

    private void creation() {
        setTitle("Bank Management System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // to keep the output screen in centre of screen

        createLoginPanel();
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                String enteredPassword = new String(passwordField.getPassword());
                login(enteredUsername, enteredPassword);
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(createAccountButton);

        add(loginPanel);
        setVisible(true);
    }

    private void login(String enteredUsername, String enteredPassword) {
        BankAccount userAccount = findAccountByUsername(enteredUsername);

        if (userAccount != null && userAccount.getPassword().equals(enteredPassword)) {
            currentAccount = userAccount;
            showOperationsDialog();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Login failed.");
        }
    }

    private void showOperationsDialog() {
        StringBuilder message = new StringBuilder("Welcome, " + currentAccount.getAccountHolder() + "!\n\n");
        message.append("Choose an operation:\n");
        message.append("1. Deposit\n");
        message.append("2. Withdraw\n");
        message.append("3. Check Balance\n");
        message.append("4. Transfer\n");
        message.append("5. Display Account Holder Details\n");
        message.append("6. Logout");

        String[] options = {"Deposit", "Withdraw", "Check Balance", "Transfer", "Display Account Holder Details", "Logout"};
        int choice = JOptionPane.showOptionDialog(this, message.toString(), "Banking Operations",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        performOperation(choice);
    }

    private void performOperation(int choice) {
        switch (choice) {
            case 0: // Deposit
                double depositAmount = getAmount("Enter the amount to deposit:");
                currentAccount.deposit(depositAmount);
                showMessage("Deposit successful!\nNew balance: $" + currentAccount.getBalance());
                showOperationsDialog();
                break;

            case 1: // Withdraw
                double withdrawAmount = getAmount("Enter the amount to withdraw:");
                if (currentAccount.withdraw(withdrawAmount)) {
                    showMessage("Withdrawal successful!\nNew balance: $" + currentAccount.getBalance());
                } else {
                    showMessage("Insufficient funds. Withdrawal failed.");
                }
                showOperationsDialog();
                break;

            case 2: // Check Balance
                showMessage("Account Balance: $" + currentAccount.getBalance());
                showOperationsDialog();
                break;

            case 3: // Transfer
                String recipientAccountNumber = JOptionPane.showInputDialog("Enter recipient's account number:");
                BankAccount recipient = findAccountByAccountNumber(recipientAccountNumber);

                if (recipient != null) {
                    double transferAmount = getAmount("Enter the amount to transfer:");
                    currentAccount.transfer(recipient, transferAmount);
                    showMessage("Transfer successful!\nNew balance: $" + currentAccount.getBalance());
                } else {
                    showMessage("Recipient account not found.");
                }
                showOperationsDialog();
                break;

            case 4: // Display Account Holder Details
                showAccountHolderDetails();
                showOperationsDialog();
                break;

            case 5: // Logout
                currentAccount = null;
                createLoginPanel();
                break;
        }
    }

    private double getAmount(String message) {
        return Double.parseDouble(JOptionPane.showInputDialog(message));
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private BankAccount findAccountByUsername(String username) {
        for (BankAccount account : accounts) {
            if (account != null && account.getUsername().equals(username)) {
                return account;
            }
        }
        return null;
    }

    private BankAccount findAccountByAccountNumber(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account != null && account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private void showAccountHolderDetails() {
        StringBuilder details = new StringBuilder("Account Holder Details:\n");
        details.append("Name: " + currentAccount.getAccountHolder() + "\n");
        details.append("Username: " + currentAccount.getUsername() + "\n");
        details.append("Account Number: " + currentAccount.getAccountNumber() + "\n");
        details.append("Balance: $" + currentAccount.getBalance() + "\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Account Holder Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createAccount() {
        String name = JOptionPane.showInputDialog("Enter your name:");
        String username = JOptionPane.showInputDialog("Set your username:");
        String password = JOptionPane.showInputDialog("Set your password:");

        BankAccount newAccount = new BankAccount(name, username, password);

        // Find the first available slot in the array to store the new account
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] == null) {
                accounts[i] = newAccount;
                break;
            }
        }

        JOptionPane.showMessageDialog(this, "Account created successfully!\nAccount Number: " +
                newAccount.getAccountNumber() + "\nInitial Balance: $" + newAccount.getBalance());
    }

    public static void main(String[] args) {
        new BankManagementSystem();
    }
}