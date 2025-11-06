import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackageTrackingPanel extends JPanel {
    private DHLServer server;
    private JTextField trackingNumberField;
    private JButton trackButton;
    private JTextArea resultArea;
    private JScrollPane scrollPane;

    public PackageTrackingPanel(DHLServer server) {
        this.server = server;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        trackingNumberField = new JTextField(20);
        trackButton = new JButton("Track Package");
        trackButton.setBackground(new Color(255, 204, 0)); // DHL Yellow
        trackButton.setForeground(Color.BLACK);
        trackButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(new TitledBorder("Package Tracking"));
        
        searchPanel.add(new JLabel("Tracking Number:"));
        searchPanel.add(trackingNumberField);
        searchPanel.add(trackButton);
        
        add(searchPanel, BorderLayout.NORTH);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Tracking Results"));
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(resultPanel, BorderLayout.CENTER);
        
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel instructionLabel = new JLabel("Enter a tracking number and click Track Package");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        instructionLabel.setForeground(Color.GRAY);
        instructionPanel.add(instructionLabel);
        
        add(instructionPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        trackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackPackage();
            }
        });
        
        trackingNumberField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackPackage();
            }
        });
    }

    private void trackPackage() {
        String trackingNumber = trackingNumberField.getText().trim();
        
        if (trackingNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a tracking number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Package pkg = server.findPackage(trackingNumber);
        
        if (pkg != null) {
            displayPackageDetails(pkg);
        } else {
            resultArea.setText("Package not found with tracking number: " + trackingNumber + "\n\n" +
                             "No packages found in the system yet.\n" +
                             "Create a new package using the 'Create Package' tab.");
        }
    }

    private void displayPackageDetails(Package pkg) {
        StringBuilder details = new StringBuilder();
        
        details.append("═══════════════════════════════════════════════════════════════\n");
        details.append("                     PACKAGE DETAILS\n");
        details.append("═══════════════════════════════════════════════════════════════\n\n");
        
        details.append("Tracking Number: ").append(pkg.getTrackingNumber()).append("\n");
        details.append("Current Status:  ").append(pkg.getStatus()).append("\n");
        details.append("Last Updated:    ").append(pkg.getLastUpdated()).append("\n\n");
        
        details.append("SENDER INFORMATION:\n");
        details.append("Name:    ").append(pkg.getSenderName()).append("\n");
        details.append("Address: ").append(pkg.getSenderAddress()).append("\n\n");
        
        details.append("RECIPIENT INFORMATION:\n");
        details.append("Name:    ").append(pkg.getRecipientName()).append("\n");
        details.append("Address: ").append(pkg.getRecipientAddress()).append("\n\n");
        
        details.append("PACKAGE INFORMATION:\n");
        details.append("Weight:      ").append(String.format("%.2f kg", pkg.getWeight())).append("\n");
        details.append("Description: ").append(pkg.getDescription()).append("\n\n");
        
        details.append("═══════════════════════════════════════════════════════════════\n");
        details.append("                    TRACKING HISTORY\n");
        details.append("═══════════════════════════════════════════════════════════════\n\n");
        
        for (TrackingEvent event : pkg.getTrackingHistory()) {
            details.append(event.toString()).append("\n");
        }
        
        details.append("\n═══════════════════════════════════════════════════════════════\n");
        
        resultArea.setText(details.toString());
        resultArea.setCaretPosition(0);
    }
    
    public void setTrackingNumber(String trackingNumber) {
        trackingNumberField.setText(trackingNumber);
        trackPackage();
    }
}