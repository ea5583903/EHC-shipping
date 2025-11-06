import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PackageFinderPanel extends JPanel {
    private DHLServer server;
    private DHLApp parentApp;
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JComboBox<PackageStatus> statusCombo;
    private JButton searchButton;
    private JButton clearButton;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JLabel resultsCountLabel;

    public PackageFinderPanel(DHLServer server, DHLApp parentApp) {
        this.server = server;
        this.parentApp = parentApp;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        String[] searchTypes = {"Sender Name", "Recipient Name", "Package Status"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        
        searchField = new JTextField(20);
        
        statusCombo = new JComboBox<>(PackageStatus.values());
        statusCombo.setVisible(false);
        
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(255, 204, 0)); // DHL Yellow
        searchButton.setForeground(Color.BLACK);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearButton = new JButton("Clear");
        clearButton.setBackground(Color.LIGHT_GRAY);
        clearButton.setForeground(Color.BLACK);
        
        String[] columns = {"Tracking Number", "Sender", "Recipient", "Status", "Weight (kg)", "Last Updated"};
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        resultsTable.getTableHeader().setBackground(new Color(255, 204, 0));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        resultsCountLabel = new JLabel("No search performed yet");
        resultsCountLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        resultsCountLabel.setForeground(Color.GRAY);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = createResultsPanel();
        add(resultsPanel, BorderLayout.CENTER);
        
        // Bottom panel with actions
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(new TitledBorder("Search Packages"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Search by:"), gbc);
        
        gbc.gridx = 1;
        searchPanel.add(searchTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Search term:"), gbc);
        
        gbc.gridx = 1;
        searchPanel.add(searchField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        searchPanel.add(statusCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        searchPanel.add(searchButton, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        searchPanel.add(clearButton, gbc);
        
        return searchPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(new TitledBorder("Search Results"));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.add(resultsCountLabel);
        resultsPanel.add(countPanel, BorderLayout.SOUTH);
        
        return resultsPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewDetailsButton = new JButton("View Package Details");
        viewDetailsButton.setBackground(Color.BLUE);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.addActionListener(e -> viewSelectedPackageDetails());
        buttonPanel.add(viewDetailsButton);
        
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.setBackground(Color.ORANGE);
        updateStatusButton.setForeground(Color.BLACK);
        updateStatusButton.addActionListener(e -> updateSelectedPackageStatus());
        buttonPanel.add(updateStatusButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        JLabel instructionLabel = new JLabel("Select a package to view details/update status, or double-click to track");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        instructionLabel.setForeground(Color.GRAY);
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionPanel.add(instructionLabel);
        bottomPanel.add(instructionPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }

    private void setupEventHandlers() {
        searchTypeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSearchFieldVisibility();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
        
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (searchField.isVisible()) {
                    performSearch();
                }
            }
        });
        
        // Add double-click functionality to the results table
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = resultsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String trackingNumber = (String) resultsTableModel.getValueAt(selectedRow, 0);
                        if (parentApp != null) {
                            parentApp.switchToTrackingTab(trackingNumber);
                        }
                    }
                }
            }
        });
    }

    private void updateSearchFieldVisibility() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        boolean isStatusSearch = "Package Status".equals(searchType);
        
        searchField.setVisible(!isStatusSearch);
        statusCombo.setVisible(isStatusSearch);
        
        revalidate();
        repaint();
    }

    private void performSearch() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        List<Package> results = null;
        
        try {
            switch (searchType) {
                case "Sender Name":
                    String senderName = searchField.getText().trim();
                    if (senderName.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter a sender name", "Search Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    results = server.findPackagesBySender(senderName);
                    break;
                    
                case "Recipient Name":
                    String recipientName = searchField.getText().trim();
                    if (recipientName.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter a recipient name", "Search Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    results = server.findPackagesByRecipient(recipientName);
                    break;
                    
                case "Package Status":
                    PackageStatus selectedStatus = (PackageStatus) statusCombo.getSelectedItem();
                    results = server.findPackagesByStatus(selectedStatus);
                    break;
            }
            
            displayResults(results, searchType);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(List<Package> packages, String searchType) {
        resultsTableModel.setRowCount(0);
        
        if (packages != null && !packages.isEmpty()) {
            for (Package pkg : packages) {
                resultsTableModel.addRow(new Object[]{
                    pkg.getTrackingNumber(),
                    pkg.getSenderName(),
                    pkg.getRecipientName(),
                    pkg.getStatus().toString(),
                    String.format("%.2f", pkg.getWeight()),
                    pkg.getLastUpdated().toString()
                });
            }
            
            resultsCountLabel.setText(String.format("Found %d package(s) for %s search", packages.size(), searchType.toLowerCase()));
            resultsCountLabel.setForeground(Color.BLACK);
        } else {
            resultsCountLabel.setText("No packages found for the search criteria");
            resultsCountLabel.setForeground(Color.RED);
        }
    }

    private void clearResults() {
        resultsTableModel.setRowCount(0);
        searchField.setText("");
        resultsCountLabel.setText("No search performed yet");
        resultsCountLabel.setForeground(Color.GRAY);
        statusCombo.setSelectedIndex(0);
    }

    private void viewSelectedPackageDetails() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a package from the table", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String trackingNumber = (String) resultsTableModel.getValueAt(selectedRow, 0);
        Package pkg = server.findPackage(trackingNumber);
        
        if (pkg != null) {
            PackageDetailsDialog dialog = new PackageDetailsDialog(SwingUtilities.getWindowAncestor(this), pkg);
            dialog.setVisible(true);
        }
    }

    private void updateSelectedPackageStatus() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a package from the table", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String trackingNumber = (String) resultsTableModel.getValueAt(selectedRow, 0);
        Package pkg = server.findPackage(trackingNumber);
        
        if (pkg != null) {
            UpdateStatusDialog dialog = new UpdateStatusDialog(SwingUtilities.getWindowAncestor(this), server, pkg);
            dialog.setVisible(true);
            
            // Refresh the current search results
            performSearch();
        }
    }
}