import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class SystemMonitorPanel extends JPanel {
    private DHLServer server;
    private JLabel totalPackagesLabel;
    private JLabel serverStatusLabel;
    private JTable statusTable;
    private DefaultTableModel statusTableModel;
    private JTextArea recentActivityArea;
    private JButton refreshButton;
    private Timer autoRefreshTimer;

    public SystemMonitorPanel(DHLServer server) {
        this.server = server;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        totalPackagesLabel = new JLabel("0");
        totalPackagesLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalPackagesLabel.setForeground(Color.RED);
        
        serverStatusLabel = new JLabel("RUNNING");
        serverStatusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        serverStatusLabel.setForeground(Color.GREEN);
        
        String[] columns = {"Package Status", "Count", "Percentage"};
        statusTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statusTable = new JTable(statusTableModel);
        statusTable.setFont(new Font("Arial", Font.PLAIN, 12));
        statusTable.setRowHeight(25);
        statusTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        statusTable.getTableHeader().setBackground(new Color(255, 204, 0));
        
        recentActivityArea = new JTextArea(10, 40);
        recentActivityArea.setEditable(false);
        recentActivityArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        recentActivityArea.setBackground(Color.WHITE);
        
        refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(new Color(255, 204, 0));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with key metrics
        JPanel metricsPanel = createMetricsPanel();
        add(metricsPanel, BorderLayout.NORTH);
        
        // Center panel with status table and activity
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new TitledBorder("Package Status Distribution"));
        statusPanel.add(new JScrollPane(statusTable), BorderLayout.CENTER);
        
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(new TitledBorder("System Information"));
        activityPanel.add(new JScrollPane(recentActivityArea), BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statusPanel, activityPanel);
        splitPane.setResizeWeight(0.6);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createMetricsPanel() {
        JPanel metricsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        metricsPanel.setBorder(new TitledBorder("System Overview"));
        metricsPanel.setPreferredSize(new Dimension(0, 120));
        
        // Total Packages Card
        JPanel totalPackagesCard = createMetricCard("Total Packages", totalPackagesLabel, Color.BLUE);
        metricsPanel.add(totalPackagesCard);
        
        // Server Status Card
        JPanel serverStatusCard = createMetricCard("Server Status", serverStatusLabel, Color.GREEN);
        metricsPanel.add(serverStatusCard);
        
        return metricsPanel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        
        JPanel valuePanel = new JPanel(new FlowLayout());
        valuePanel.setOpaque(false);
        valuePanel.add(valueLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        autoRefreshTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        autoRefreshTimer.start();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            updateMetrics();
            updateStatusTable();
            updateSystemInfo();
        });
    }

    private void updateMetrics() {
        totalPackagesLabel.setText(String.valueOf(server.getTotalPackages()));
        serverStatusLabel.setText(server.isRunning() ? "RUNNING" : "STOPPED");
        serverStatusLabel.setForeground(server.isRunning() ? Color.GREEN : Color.RED);
    }

    private void updateStatusTable() {
        statusTableModel.setRowCount(0);
        
        Map<PackageStatus, Long> statusSummary = server.getPackageStatusSummary();
        long totalPackages = server.getTotalPackages();
        
        for (PackageStatus status : PackageStatus.values()) {
            Long count = statusSummary.getOrDefault(status, 0L);
            double percentage = totalPackages > 0 ? (count.doubleValue() / totalPackages) * 100 : 0;
            
            statusTableModel.addRow(new Object[]{
                status.toString(),
                count,
                String.format("%.1f%%", percentage)
            });
        }
    }

    private void updateSystemInfo() {
        StringBuilder info = new StringBuilder();
        
        info.append("═══════════════════════════════════════════════════════════════\n");
        info.append("                        SYSTEM STATUS\n");
        info.append("═══════════════════════════════════════════════════════════════\n\n");
        
        info.append("Server Status: ").append(server.isRunning() ? "RUNNING" : "STOPPED").append("\n");
        info.append("Total Packages: ").append(server.getTotalPackages()).append("\n");
        info.append("Last Refresh: ").append(new java.util.Date()).append("\n\n");
        
        info.append("PACKAGE STATUS BREAKDOWN:\n");
        Map<PackageStatus, Long> statusSummary = server.getPackageStatusSummary();
        long totalPackages = server.getTotalPackages();
        
        for (Map.Entry<PackageStatus, Long> entry : statusSummary.entrySet()) {
            if (entry.getValue() > 0) {
                double percentage = (entry.getValue().doubleValue() / totalPackages) * 100;
                info.append(String.format("- %s: %d packages (%.1f%%)\n", 
                    entry.getKey().toString(), entry.getValue(), percentage));
            }
        }
        
        info.append("\n");
        info.append("SYSTEM CAPABILITIES:\n");
        info.append("- Package Tracking: ✓ Active\n");
        info.append("- Package Creation: ✓ Active\n");
        info.append("- Package Search: ✓ Active\n");
        info.append("- Status Updates: ✓ Active\n");
        info.append("- Real-time Monitoring: ✓ Active\n\n");
        
        info.append("AUTO-REFRESH: Every 10 seconds\n");
        info.append("═══════════════════════════════════════════════════════════════\n");
        
        recentActivityArea.setText(info.toString());
        recentActivityArea.setCaretPosition(0);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }
}