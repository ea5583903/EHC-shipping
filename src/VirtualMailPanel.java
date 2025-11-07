import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VirtualMailPanel extends JPanel {
    private List<VirtualMail> mailList;
    private DefaultTableModel mailTableModel;
    private JTable mailTable;
    private JTextArea messageArea;
    private JTextField subjectField;
    private JTextField fromField;
    private JTextField toField;
    private JButton sendButton;
    private JButton deleteButton;
    private JButton replyButton;
    private JLabel statusLabel;
    
    public VirtualMailPanel() {
        mailList = new ArrayList<>();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSampleMails();
    }
    
    private void initializeComponents() {
        // Mail table
        String[] columns = {"From", "Subject", "Date", "Status"};
        mailTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mailTable = new JTable(mailTableModel);
        mailTable.setFont(new Font("Arial", Font.PLAIN, 11));
        mailTable.setRowHeight(25);
        mailTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        mailTable.getTableHeader().setBackground(new Color(255, 204, 0));
        mailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Message composition area
        messageArea = new JTextArea(10, 40);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        // Input fields
        subjectField = new JTextField(30);
        
        UserSession session = UserSession.getInstance();
        String defaultEmail = session.isLoggedIn() ? session.getCurrentEmail() : "user@ehc.com";
        fromField = new JTextField(defaultEmail, 20);
        toField = new JTextField(20);
        
        // Buttons
        sendButton = new JButton("Send Mail");
        sendButton.setBackground(new Color(255, 204, 0));
        sendButton.setForeground(Color.BLACK);
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        
        replyButton = new JButton("Reply");
        replyButton.setBackground(Color.BLUE);
        replyButton.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Virtual Mail System Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - mail list
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new TitledBorder("Inbox"));
        
        JScrollPane mailScrollPane = new JScrollPane(mailTable);
        mailScrollPane.setPreferredSize(new Dimension(0, 200));
        topPanel.add(mailScrollPane, BorderLayout.CENTER);
        
        JPanel mailButtonPanel = new JPanel(new FlowLayout());
        mailButtonPanel.add(deleteButton);
        mailButtonPanel.add(replyButton);
        topPanel.add(mailButtonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Middle panel - message viewer/composer
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(new TitledBorder("Message"));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        middlePanel.add(messageScrollPane, BorderLayout.CENTER);
        
        add(middlePanel, BorderLayout.CENTER);
        
        // Bottom panel - composition controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new TitledBorder("Compose New Mail"));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(fromField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(toField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(subjectField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        fieldsPanel.add(sendButton, gbc);
        
        bottomPanel.add(fieldsPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        mailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedMail();
            }
        });
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMail();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMail();
            }
        });
        
        replyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replyToMail();
            }
        });
    }
    
    private void loadSampleMails() {
        // Add some sample mails
        mailList.add(new VirtualMail(
            "system@ehc.com",
            "user@ehc.com",
            "Welcome to EHC Virtual Mail",
            "Welcome to the EHC Package Tracking System Virtual Mail feature. You can now send and receive messages within the system.",
            new Date(),
            "Unread"
        ));
        
        mailList.add(new VirtualMail(
            "admin@ehc.com",
            "user@ehc.com",
            "Package Delivery Notification",
            "Your package TRK123456789 has been successfully delivered to the recipient.",
            new Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            "Read"
        ));
        
        mailList.add(new VirtualMail(
            "support@ehc.com",
            "user@ehc.com",
            "System Maintenance Notice",
            "The system will undergo maintenance on Sunday from 2:00 AM to 4:00 AM. Some services may be temporarily unavailable.",
            new Date(System.currentTimeMillis() - 7200000), // 2 hours ago
            "Unread"
        ));
        
        refreshMailTable();
    }
    
    private void refreshMailTable() {
        mailTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        
        for (VirtualMail mail : mailList) {
            mailTableModel.addRow(new Object[]{
                mail.getFrom(),
                mail.getSubject(),
                dateFormat.format(mail.getDate()),
                mail.getStatus()
            });
        }
        
        statusLabel.setText("Total messages: " + mailList.size());
    }
    
    private void displaySelectedMail() {
        int selectedRow = mailTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < mailList.size()) {
            VirtualMail selectedMail = mailList.get(selectedRow);
            
            StringBuilder mailContent = new StringBuilder();
            mailContent.append("From: ").append(selectedMail.getFrom()).append("\n");
            mailContent.append("To: ").append(selectedMail.getTo()).append("\n");
            mailContent.append("Subject: ").append(selectedMail.getSubject()).append("\n");
            mailContent.append("Date: ").append(selectedMail.getDate().toString()).append("\n");
            mailContent.append("Status: ").append(selectedMail.getStatus()).append("\n\n");
            mailContent.append("Message:\n");
            mailContent.append(selectedMail.getMessage());
            
            messageArea.setText(mailContent.toString());
            messageArea.setCaretPosition(0);
            
            // Mark as read
            if ("Unread".equals(selectedMail.getStatus())) {
                selectedMail.setStatus("Read");
                refreshMailTable();
            }
        }
    }
    
    private void sendMail() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();
        String from = fromField.getText().trim();
        
        if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields (To, Subject, Message)", 
                "Missing Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email format
        if (!isValidEHCEmail(to)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid email format. Please use format: [username]@ehc.com", 
                "Invalid Email", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEHCEmail(from)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid sender email format. Please use format: [username]@ehc.com", 
                "Invalid Email", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        VirtualMail newMail = new VirtualMail(from, to, subject, message, new Date(), "Sent");
        mailList.add(0, newMail); // Add to beginning
        
        // Clear composition fields
        toField.setText("");
        subjectField.setText("");
        messageArea.setText("");
        
        refreshMailTable();
        statusLabel.setText("Mail sent successfully to " + to);
        
        // Show confirmation
        JOptionPane.showMessageDialog(this, 
            "Mail sent successfully!", 
            "Mail Sent", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteMail() {
        int selectedRow = mailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a mail to delete", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this mail?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            mailList.remove(selectedRow);
            messageArea.setText("");
            refreshMailTable();
            statusLabel.setText("Mail deleted successfully");
        }
    }
    
    private void replyToMail() {
        int selectedRow = mailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a mail to reply to", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        VirtualMail selectedMail = mailList.get(selectedRow);
        
        // Set up reply
        toField.setText(selectedMail.getFrom());
        subjectField.setText("Re: " + selectedMail.getSubject());
        
        StringBuilder replyMessage = new StringBuilder();
        replyMessage.append("\n\n--- Original Message ---\n");
        replyMessage.append("From: ").append(selectedMail.getFrom()).append("\n");
        replyMessage.append("Date: ").append(selectedMail.getDate().toString()).append("\n");
        replyMessage.append("Subject: ").append(selectedMail.getSubject()).append("\n\n");
        replyMessage.append(selectedMail.getMessage());
        
        messageArea.setText(replyMessage.toString());
        messageArea.setCaretPosition(0);
        
        statusLabel.setText("Replying to mail from " + selectedMail.getFrom());
    }
    
    private boolean isValidEHCEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Check if email ends with @ehc.com
        if (!email.toLowerCase().endsWith("@ehc.com")) {
            return false;
        }
        
        // Check if there's a username before @ehc.com
        String username = email.substring(0, email.length() - "@ehc.com".length());
        if (username.isEmpty() || username.contains("@") || username.contains(" ")) {
            return false;
        }
        
        return true;
    }
    
    public void sendDemoMessage() {
        // Create a demo message from admin to manager
        VirtualMail demoMail = new VirtualMail(
            "admin@ehc.com",
            "manager@ehc.com", 
            "System Status Update",
            "Hello Manager,\n\nThe EHC Package Tracking System has been successfully updated with new features:\n\n1. File Finder - Browse system files\n2. Virtual Mail - Internal messaging system\n\nAll systems are running smoothly.\n\nBest regards,\nAdmin Team",
            new Date(),
            "Sent"
        );
        
        mailList.add(0, demoMail);
        refreshMailTable();
        statusLabel.setText("Demo message sent from admin@ehc.com to manager@ehc.com");
    }
    
    public void sendFileShareMessage(String from, String to, String subject, String message) {
        VirtualMail fileMail = new VirtualMail(from, to, subject, message, new Date(), "Sent");
        mailList.add(0, fileMail);
        refreshMailTable();
    }
}

// Inner class for VirtualMail
class VirtualMail {
    private String from;
    private String to;
    private String subject;
    private String message;
    private Date date;
    private String status;
    
    public VirtualMail(String from, String to, String subject, String message, Date date, String status) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.date = date;
        this.status = status;
    }
    
    // Getters and setters
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}