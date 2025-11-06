import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackageCreationPanel extends JPanel {
    private DHLServer server;
    private DHLApp parentApp;
    private JTextField senderNameField;
    private JTextField senderAddressField;
    private JTextField recipientNameField;
    private JTextField recipientAddressField;
    private JTextField weightField;
    private JTextField descriptionField;
    private JButton createButton;
    private JButton clearButton;
    private JTextArea resultArea;

    public PackageCreationPanel(DHLServer server, DHLApp parentApp) {
        this.server = server;
        this.parentApp = parentApp;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        senderNameField = new JTextField(25);
        senderAddressField = new JTextField(25);
        recipientNameField = new JTextField(25);
        recipientAddressField = new JTextField(25);
        weightField = new JTextField(10);
        descriptionField = new JTextField(25);
        
        createButton = new JButton("Create Package");
        createButton.setBackground(new Color(255, 204, 0)); // DHL Yellow
        createButton.setForeground(Color.BLACK);
        createButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearButton = new JButton("Clear Form");
        clearButton.setBackground(Color.LIGHT_GRAY);
        clearButton.setForeground(Color.BLACK);
        
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Package Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Sender Information
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel senderTitle = new JLabel("SENDER INFORMATION");
        senderTitle.setFont(new Font("Arial", Font.BOLD, 14));
        senderTitle.setForeground(Color.RED);
        formPanel.add(senderTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Sender Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(senderNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Sender Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(senderAddressField, gbc);
        
        // Recipient Information
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel recipientTitle = new JLabel("RECIPIENT INFORMATION");
        recipientTitle.setFont(new Font("Arial", Font.BOLD, 14));
        recipientTitle.setForeground(Color.RED);
        formPanel.add(recipientTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Recipient Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(recipientNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Recipient Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(recipientAddressField, gbc);
        
        // Package Information
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel packageTitle = new JLabel("PACKAGE INFORMATION");
        packageTitle.setFont(new Font("Arial", Font.BOLD, 14));
        packageTitle.setForeground(Color.RED);
        formPanel.add(packageTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        formPanel.add(weightField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Result area
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Creation Result"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private void setupEventHandlers() {
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPackage();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    private void createPackage() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String senderName = senderNameField.getText().trim();
            String senderAddress = senderAddressField.getText().trim();
            String recipientName = recipientNameField.getText().trim();
            String recipientAddress = recipientAddressField.getText().trim();
            double weight = Double.parseDouble(weightField.getText().trim());
            String description = descriptionField.getText().trim();
            
            Package newPackage = server.createPackage(senderName, senderAddress, 
                                                    recipientName, recipientAddress, 
                                                    weight, description);
            
            displayCreationResult(newPackage);
            clearForm();
            
            // Refresh the main app to update package counts
            if (parentApp != null) {
                parentApp.refreshApp();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid weight (decimal number)", 
                                        "Invalid Weight", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (senderNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sender name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            senderNameField.requestFocus();
            return false;
        }
        
        if (senderAddressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sender address is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            senderAddressField.requestFocus();
            return false;
        }
        
        if (recipientNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipient name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            recipientNameField.requestFocus();
            return false;
        }
        
        if (recipientAddressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipient address is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            recipientAddressField.requestFocus();
            return false;
        }
        
        if (weightField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Weight is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            weightField.requestFocus();
            return false;
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void displayCreationResult(Package pkg) {
        StringBuilder result = new StringBuilder();
        result.append("═══════════════════════════════════════════════════════════════\n");
        result.append("                 PACKAGE CREATED SUCCESSFULLY\n");
        result.append("═══════════════════════════════════════════════════════════════\n\n");
        result.append("Tracking Number: ").append(pkg.getTrackingNumber()).append("\n");
        result.append("Status: ").append(pkg.getStatus()).append("\n");
        result.append("Created: ").append(pkg.getCreatedAt()).append("\n\n");
        result.append("From: ").append(pkg.getSenderName()).append("\n");
        result.append("To: ").append(pkg.getRecipientName()).append("\n");
        result.append("Weight: ").append(String.format("%.2f kg", pkg.getWeight())).append("\n");
        result.append("Description: ").append(pkg.getDescription()).append("\n\n");
        result.append("Please save the tracking number for future reference.\n");
        result.append("═══════════════════════════════════════════════════════════════\n");
        
        resultArea.setText(result.toString());
        resultArea.setCaretPosition(0);
    }

    private void clearForm() {
        senderNameField.setText("");
        senderAddressField.setText("");
        recipientNameField.setText("");
        recipientAddressField.setText("");
        weightField.setText("");
        descriptionField.setText("");
        resultArea.setText("");
        senderNameField.requestFocus();
    }
}