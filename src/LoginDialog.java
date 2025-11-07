import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton createAccountButton;
    private JLabel statusLabel;
    private boolean loginSuccessful = false;
    
    public LoginDialog(Frame parent) {
        super(parent, "EHC Login", true);
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
        
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(255, 204, 0));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        
        createAccountButton = new JButton("Create Account");
        createAccountButton.setBackground(Color.BLUE);
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        statusLabel = new JLabel("<html><center>Enter your EHC credentials or create a new account<br><small>Default: admin / admin123</small></center></html>");
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
        
        JLabel titleLabel = new JLabel("EHC Package System", SwingConstants.CENTER);
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
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateAccountDialog();
            }
        });
        
        // Allow Enter key to login
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());
        
        // Set focus to username field when dialog opens
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                usernameField.requestFocus();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        // Validate username format (no @ symbol, will be appended with @ehc.com)
        if (username.contains("@")) {
            statusLabel.setText("Username should not contain @ symbol");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(Color.BLUE);
        
        // Perform actual login validation
        SwingUtilities.invokeLater(() -> {
            UserAccountManager accountManager = UserAccountManager.getInstance();
            
            if (accountManager.validateLogin(username, password)) {
                UserSession session = UserSession.getInstance();
                session.login(username, password);
                
                loginSuccessful = true;
                statusLabel.setText("Login successful! Welcome " + username);
                statusLabel.setForeground(Color.GREEN);
                
                Timer timer = new Timer(1000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText("Invalid credentials. Please try again.");
                statusLabel.setForeground(Color.RED);
                passwordField.setText("");
                passwordField.requestFocus();
            }
        });
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    private void showCreateAccountDialog() {
        CreateAccountDialog createDialog = new CreateAccountDialog((Frame) getParent());
        createDialog.setVisible(true);
        
        if (createDialog.isAccountCreated()) {
            String newUsername = createDialog.getCreatedUsername();
            if (newUsername != null) {
                usernameField.setText(newUsername);
                passwordField.setText("");
                passwordField.requestFocus();
                statusLabel.setText("Account created! Please login with your new credentials.");
                statusLabel.setForeground(Color.GREEN);
            }
        }
    }
}