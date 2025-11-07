import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccountDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private boolean accountCreated = false;
    
    public CreateAccountDialog(Frame parent) {
        super(parent, "Create EHC Account", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        createButton = new JButton("Create Account");
        createButton.setBackground(new Color(255, 204, 0));
        createButton.setForeground(Color.BLACK);
        createButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        
        statusLabel = new JLabel("Create your new EHC account");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 204, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("EHC Account Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.RED);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);
        
        // Add requirements label
        gbc.gridy = 4;
        JLabel requirementsLabel = new JLabel("<html><center>Requirements:<br>• Username: 3+ chars, letters/numbers/underscore only<br>• Password: 3+ characters</center></html>");
        requirementsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        requirementsLabel.setForeground(Color.DARK_GRAY);
        requirementsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(requirementsLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Allow Enter key progression
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(e -> createAccount());
        
        // Set focus to username field when dialog opens
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                usernameField.requestFocus();
            }
        });
    }
    
    private void createAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        UserAccountManager accountManager = UserAccountManager.getInstance();
        String error = accountManager.getAccountCreationError(username, password, confirmPassword);
        
        if (error != null) {
            statusLabel.setText(error);
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        statusLabel.setText("Creating account...");
        statusLabel.setForeground(Color.BLUE);
        
        // Create account
        SwingUtilities.invokeLater(() -> {
            if (accountManager.createAccount(username, password, confirmPassword)) {
                accountCreated = true;
                statusLabel.setText("Account created successfully! You can now login.");
                statusLabel.setForeground(Color.GREEN);
                
                JOptionPane.showMessageDialog(this, 
                    "Account created successfully!\nUsername: " + username + "\nEmail: " + username + "@ehc.com\n\nYou can now login with your credentials.",
                    "Account Created", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                Timer timer = new Timer(2000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText("Failed to create account. Please try again.");
                statusLabel.setForeground(Color.RED);
            }
        });
    }
    
    public boolean isAccountCreated() {
        return accountCreated;
    }
    
    public String getCreatedUsername() {
        return accountCreated ? usernameField.getText().trim() : null;
    }
}