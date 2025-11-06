import javax.swing.*;
import java.awt.*;

public class PackageDetailsDialog extends JDialog {
    private Package packageInfo;

    public PackageDetailsDialog(Window parent, Package pkg) {
        super(parent, "Package Details - " + pkg.getTrackingNumber(), ModalityType.APPLICATION_MODAL);
        this.packageInfo = pkg;
        
        initializeComponents();
        setupLayout();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBackground(Color.WHITE);
        
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════════════════════════════\n");
        details.append("                     PACKAGE DETAILS\n");
        details.append("═══════════════════════════════════════════════════════════════\n\n");
        
        details.append("Tracking Number: ").append(packageInfo.getTrackingNumber()).append("\n");
        details.append("Current Status:  ").append(packageInfo.getStatus()).append("\n");
        details.append("Last Updated:    ").append(packageInfo.getLastUpdated()).append("\n");
        details.append("Created:         ").append(packageInfo.getCreatedAt()).append("\n\n");
        
        details.append("SENDER INFORMATION:\n");
        details.append("Name:    ").append(packageInfo.getSenderName()).append("\n");
        details.append("Address: ").append(packageInfo.getSenderAddress()).append("\n\n");
        
        details.append("RECIPIENT INFORMATION:\n");
        details.append("Name:    ").append(packageInfo.getRecipientName()).append("\n");
        details.append("Address: ").append(packageInfo.getRecipientAddress()).append("\n\n");
        
        details.append("PACKAGE INFORMATION:\n");
        details.append("Weight:      ").append(String.format("%.2f kg", packageInfo.getWeight())).append("\n");
        details.append("Description: ").append(packageInfo.getDescription()).append("\n\n");
        
        details.append("═══════════════════════════════════════════════════════════════\n");
        details.append("                    TRACKING HISTORY\n");
        details.append("═══════════════════════════════════════════════════════════════\n\n");
        
        for (TrackingEvent event : packageInfo.getTrackingHistory()) {
            details.append(event.toString()).append("\n");
        }
        
        details.append("\n═══════════════════════════════════════════════════════════════\n");
        
        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
}