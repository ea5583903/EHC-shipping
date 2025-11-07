import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileFinderPanel extends JPanel {
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JTextField pathField;
    private JButton browseButton;
    private JButton refreshButton;
    private JButton sendFileButton;
    private JLabel statusLabel;
    private VirtualMailPanel mailPanel;
    
    public FileFinderPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialDirectory();
    }
    
    public FileFinderPanel(VirtualMailPanel mailPanel) {
        this.mailPanel = mailPanel;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialDirectory();
    }
    
    private void initializeComponents() {
        // File tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Computer");
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.setRootVisible(true);
        
        // File table
        String[] columns = {"Name", "Type", "Size", "Modified"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fileTable = new JTable(tableModel);
        fileTable.setFont(new Font("Arial", Font.PLAIN, 11));
        fileTable.setRowHeight(20);
        fileTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        fileTable.getTableHeader().setBackground(new Color(255, 204, 0));
        
        // Path field and buttons
        pathField = new JTextField(System.getProperty("user.home"));
        browseButton = new JButton("Browse");
        refreshButton = new JButton("Refresh");
        sendFileButton = new JButton("Send File/Folder");
        
        browseButton.setBackground(new Color(255, 204, 0));
        browseButton.setForeground(Color.BLACK);
        refreshButton.setBackground(Color.LIGHT_GRAY);
        refreshButton.setForeground(Color.BLACK);
        sendFileButton.setBackground(Color.BLUE);
        sendFileButton.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with path controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new TitledBorder("File Browser"));
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JLabel("Path: "), BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(browseButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(sendFileButton);
        pathPanel.add(buttonPanel, BorderLayout.EAST);
        
        topPanel.add(pathPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left side - tree view
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setBorder(new TitledBorder("Directory Tree"));
        treeScrollPane.setPreferredSize(new Dimension(250, 400));
        splitPane.setLeftComponent(treeScrollPane);
        
        // Right side - file details
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        tableScrollPane.setBorder(new TitledBorder("File Details"));
        splitPane.setRightComponent(tableScrollPane);
        
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCurrentDirectory();
            }
        });
        
        pathField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDirectory(pathField.getText());
            }
        });
        
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendSelectedFile();
            }
        });
        
        fileTree.addTreeSelectionListener(e -> {
            if (e.getPath() != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                if (node.getUserObject() instanceof File) {
                    File selectedFile = (File) node.getUserObject();
                    if (selectedFile.isDirectory()) {
                        loadFileDetails(selectedFile);
                        pathField.setText(selectedFile.getAbsolutePath());
                    }
                }
            }
        });
        
        fileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = fileTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                        File currentDir = new File(pathField.getText());
                        File selectedFile = new File(currentDir, fileName);
                        
                        if (selectedFile.isDirectory()) {
                            loadDirectory(selectedFile.getAbsolutePath());
                        } else {
                            openFile(selectedFile);
                        }
                    }
                }
            }
        });
    }
    
    private void loadInitialDirectory() {
        String userHome = System.getProperty("user.home");
        loadDirectory(userHome);
    }
    
    private void loadDirectory(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                statusLabel.setText("Invalid directory: " + path);
                return;
            }
            
            pathField.setText(dir.getAbsolutePath());
            updateFileTree(dir);
            loadFileDetails(dir);
            statusLabel.setText("Loaded: " + dir.getAbsolutePath());
            
        } catch (Exception e) {
            statusLabel.setText("Error loading directory: " + e.getMessage());
        }
    }
    
    private void updateFileTree(File rootDir) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDir);
        treeModel.setRoot(root);
        
        try {
            addDirectoriesToTree(root, rootDir, 2); // Limit depth to avoid performance issues
        } catch (Exception e) {
            // Handle permission errors gracefully
        }
        
        fileTree.expandRow(0);
    }
    
    private void addDirectoriesToTree(DefaultMutableTreeNode parentNode, File parentDir, int depth) {
        if (depth <= 0) return;
        
        try {
            File[] children = parentDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory() && !child.isHidden()) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                        parentNode.add(childNode);
                        addDirectoriesToTree(childNode, child, depth - 1);
                    }
                }
            }
        } catch (Exception e) {
            // Handle permission errors gracefully
        }
    }
    
    private void loadFileDetails(File directory) {
        tableModel.setRowCount(0);
        
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                
                for (File file : files) {
                    String name = file.getName();
                    String type = file.isDirectory() ? "Directory" : getFileExtension(file);
                    String size = file.isDirectory() ? "" : formatFileSize(file.length());
                    String modified = dateFormat.format(new Date(file.lastModified()));
                    
                    tableModel.addRow(new Object[]{name, type, size, modified});
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading file details: " + e.getMessage());
        }
    }
    
    private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser(pathField.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadDirectory(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void refreshCurrentDirectory() {
        loadDirectory(pathField.getText());
    }
    
    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                statusLabel.setText("Opening: " + file.getName());
            } else {
                statusLabel.setText("Cannot open file - Desktop not supported");
            }
        } catch (Exception e) {
            statusLabel.setText("Error opening file: " + e.getMessage());
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1).toUpperCase() : "File";
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    private void sendSelectedFile() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a file or folder to send", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
        File currentDir = new File(pathField.getText());
        File selectedFile = new File(currentDir, fileName);
        
        // Get recipient email
        String recipient = JOptionPane.showInputDialog(this, 
            "Enter recipient email (format: username@ehc.com):", 
            "Send File/Folder", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (recipient == null || recipient.trim().isEmpty()) {
            return; // User cancelled
        }
        
        recipient = recipient.trim();
        
        // Validate email format
        if (!recipient.toLowerCase().endsWith("@ehc.com")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid email format. Please use format: [username]@ehc.com", 
                "Invalid Email", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create message content
        String fileType = selectedFile.isDirectory() ? "Folder" : "File";
        String subject = fileType + " shared: " + fileName;
        String message = String.format(
            "A %s has been shared with you:\n\n" +
            "Name: %s\n" +
            "Path: %s\n" +
            "Type: %s\n" +
            "%s" +
            "\nShared via EHC File Finder system.",
            fileType.toLowerCase(),
            fileName,
            selectedFile.getAbsolutePath(),
            selectedFile.isDirectory() ? "Directory" : getFileExtension(selectedFile),
            selectedFile.isFile() ? "Size: " + formatFileSize(selectedFile.length()) + "\n" : ""
        );
        
        if (mailPanel != null) {
            mailPanel.sendFileShareMessage("system@ehc.com", recipient, subject, message);
            statusLabel.setText(fileType + " '" + fileName + "' sent to " + recipient);
            
            JOptionPane.showMessageDialog(this, 
                fileType + " '" + fileName + "' has been sent to " + recipient, 
                "File Shared Successfully", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Mail system not available. Cannot send file.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}