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

    public DHLApp() {
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
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        tabbedPane.addTab("Track Package", new ImageIcon(), trackingPanel, "Track a specific package");
        tabbedPane.addTab("Create Package", new ImageIcon(), creationPanel, "Create a new package");
        tabbedPane.addTab("Find Packages", new ImageIcon(), finderPanel, "Search for packages");
        tabbedPane.addTab("System Monitor", new ImageIcon(), monitorPanel, "Monitor system status");
        
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

}