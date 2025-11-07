import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DHLApp extends JFrame {
    private DHLServer server;
    private JTabbedPane tabbedPane;
    private PackageTrackingPanel trackingPanel;
    private PackageCreationPanel creationPanel;
    private PackageFinderPanel finderPanel;
    private SystemMonitorPanel monitorPanel;
    private FileFinderPanel fileFinderPanel;
    private VirtualMailPanel virtualMailPanel;

    public DHLApp() {
        // Check login status
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            showLoginDialog();
        }
        
        server = new DHLServer();
        server.start();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("EHC Package Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        trackingPanel = new PackageTrackingPanel(server);
        creationPanel = new PackageCreationPanel(server, this);
        finderPanel = new PackageFinderPanel(server, this);
        monitorPanel = new SystemMonitorPanel(server);
        virtualMailPanel = new VirtualMailPanel();
        fileFinderPanel = new FileFinderPanel(virtualMailPanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        tabbedPane.addTab("Track Package", new ImageIcon(), trackingPanel, "Track a specific package");
        tabbedPane.addTab("Create Package", new ImageIcon(), creationPanel, "Create a new package");
        tabbedPane.addTab("Find Packages", new ImageIcon(), finderPanel, "Search for packages");
        tabbedPane.addTab("System Monitor", new ImageIcon(), monitorPanel, "Monitor system status");
        tabbedPane.addTab("File Finder", new ImageIcon(), fileFinderPanel, "Browse system files");
        tabbedPane.addTab("Virtual Mail", new ImageIcon(), virtualMailPanel, "Send and receive virtual mail");
        
        add(tabbedPane, BorderLayout.CENTER);
        
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 204, 0)); // DHL Yellow
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("EHC Package Tracking System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.RED); // DHL Red
        
        JLabel subtitleLabel = new JLabel("Professional Package Management Solution", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.BLACK);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Add user info panel
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            JPanel userPanel = new JPanel(new FlowLayout());
            userPanel.setOpaque(false);
            
            JLabel userLabel = new JLabel("Logged in as: " + session.getCurrentEmail());
            userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            userLabel.setForeground(Color.BLACK);
            
            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Arial", Font.PLAIN, 10));
            logoutButton.addActionListener(e -> logout());
            
            userPanel.add(userLabel);
            userPanel.add(logoutButton);
            headerPanel.add(userPanel, BorderLayout.EAST);
        }
        
        return headerPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.LIGHT_GRAY);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Server Status: RUNNING | Total Packages: " + server.getTotalPackages());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        
        return statusPanel;
    }

    private void setupEventHandlers() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                server.stop();
                System.exit(0);
            }
        });
        
        Timer statusUpdateTimer = new Timer(5000, e -> updateStatusBar());
        statusUpdateTimer.start();
        
        // Add tab change listener to demonstrate Virtual Mail
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            // When Virtual Mail tab (index 5) is selected, send a demo message
            if (selectedIndex == 5 && virtualMailPanel != null) {
                SwingUtilities.invokeLater(() -> virtualMailPanel.sendDemoMessage());
            }
        });
    }

    private void updateStatusBar() {
        SwingUtilities.invokeLater(() -> {
            try {
                Component statusPanelContainer = getContentPane().getComponent(2);
                if (statusPanelContainer instanceof JPanel) {
                    JPanel panel = (JPanel) statusPanelContainer;
                    if (panel.getComponentCount() > 0) {
                        Component statusComponent = panel.getComponent(0);
                        if (statusComponent instanceof JLabel) {
                            ((JLabel) statusComponent).setText("Server Status: " + 
                                (server.isRunning() ? "RUNNING" : "STOPPED") + 
                                " | Total Packages: " + server.getTotalPackages());
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore status bar update errors
            }
            
            monitorPanel.refreshData();
        });
    }

    public void refreshApp() {
        updateStatusBar();
    }
    
    public void switchToTrackingTab(String trackingNumber) {
        tabbedPane.setSelectedIndex(0); // Track Package is the first tab
        trackingPanel.setTrackingNumber(trackingNumber);
    }
    
    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        
        if (!loginDialog.isLoginSuccessful()) {
            System.exit(0); // Exit if login cancelled
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.getInstance().logout();
            dispose();
            System.exit(0);
        }
    }

}