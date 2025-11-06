import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateStatusDialog extends JDialog {
    private DHLServer server;
    private Package packageInfo;
    private JComboBox<PackageStatus> statusCombo;
    private JTextField locationField;
    private JTextField descriptionField;
    private JButton updateButton;
    private JButton cancelButton;

    public UpdateStatusDialog(Window parent, DHLServer server, Package pkg) {
        super(parent, "Update Package Status - " + pkg.getTrackingNumber(), ModalityType.APPLICATION_MODAL);
        this.server = server;
        this.packageInfo = pkg;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setSize(450, 300);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        statusCombo = new JComboBox<>(PackageStatus.values());
        statusCombo.setSelectedItem(packageInfo.getStatus());
        
        locationField = new JTextField(20);
        descriptionField = new JTextField(20);
        
        updateButton = new JButton("Update Status");
        updateButton.setBackground(new Color(255, 204, 0));
        updateButton.setForeground(Color.BLACK);
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Current Status:"), gbc);
        gbc.gridx = 1;
        JLabel currentStatusLabel = new JLabel(packageInfo.getStatus().toString());
        currentStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(currentStatusLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("New Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePackageStatus();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void updatePackageStatus() {
        PackageStatus newStatus = (PackageStatus) statusCombo.getSelectedItem();
        String location = locationField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a location", "Validation Error", JOptionPane.ERROR_MESSAGE);
            locationField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description", "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return;
        }
        
        boolean success = server.updatePackageStatus(packageInfo.getTrackingNumber(), newStatus, location, description);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Package status updated successfully!\n\n" +
                "Tracking Number: " + packageInfo.getTrackingNumber() + "\n" +
                "New Status: " + newStatus + "\n" +
                "Location: " + location,
                "Update Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update package status", "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}