import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserAccountManager {
    private static UserAccountManager instance;
    private final String ACCOUNTS_FILE = System.getProperty("user.home") + "/.ehc_accounts";
    private Map<String, String> accounts; // username -> hashed password
    
    private UserAccountManager() {
        accounts = new HashMap<>();
        loadAccounts();
    }
    
    public static UserAccountManager getInstance() {
        if (instance == null) {
            instance = new UserAccountManager();
        }
        return instance;
    }
    
    public boolean createAccount(String username, String password, String confirmPassword) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (password == null || password.length() < 3) {
            return false; // Minimum password length
        }
        
        if (!password.equals(confirmPassword)) {
            return false; // Passwords don't match
        }
        
        username = username.trim().toLowerCase();
        
        // Check if username already exists
        if (accounts.containsKey(username)) {
            return false;
        }
        
        // Validate username format (no special characters except underscore)
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return false;
        }
        
        // Hash password and store account
        String hashedPassword = hashPassword(password);
        accounts.put(username, hashedPassword);
        saveAccounts();
        
        return true;
    }
    
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        username = username.trim().toLowerCase();
        String hashedPassword = hashPassword(password);
        
        return hashedPassword.equals(accounts.get(username));
    }
    
    public boolean userExists(String username) {
        if (username == null) {
            return false;
        }
        return accounts.containsKey(username.trim().toLowerCase());
    }
    
    public int getTotalUsers() {
        return accounts.size();
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if SHA-256 not available
            return String.valueOf(password.hashCode());
        }
    }
    
    private void loadAccounts() {
        try {
            File accountsFile = new File(ACCOUNTS_FILE);
            if (accountsFile.exists()) {
                Properties props = new Properties();
                FileInputStream fis = new FileInputStream(ACCOUNTS_FILE);
                props.load(fis);
                fis.close();
                
                for (Object key : props.keySet()) {
                    accounts.put((String) key, props.getProperty((String) key));
                }
            } else {
                // Create default admin account
                createDefaultAccount();
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            createDefaultAccount();
        }
    }
    
    private void saveAccounts() {
        try {
            Properties props = new Properties();
            for (Map.Entry<String, String> entry : accounts.entrySet()) {
                props.setProperty(entry.getKey(), entry.getValue());
            }
            
            FileOutputStream fos = new FileOutputStream(ACCOUNTS_FILE);
            props.store(fos, "EHC User Accounts");
            fos.close();
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }
    
    private void createDefaultAccount() {
        // Create default admin account for demonstration
        String defaultPassword = hashPassword("admin123");
        accounts.put("admin", defaultPassword);
        saveAccounts();
    }
    
    public String getAccountCreationError(String username, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        
        username = username.trim();
        
        if (username.length() < 3) {
            return "Username must be at least 3 characters long";
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Username can only contain letters, numbers, and underscores";
        }
        
        if (userExists(username)) {
            return "Username already exists";
        }
        
        if (password == null || password.length() < 3) {
            return "Password must be at least 3 characters long";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        
        return null; // No error
    }
}