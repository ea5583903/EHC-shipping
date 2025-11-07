import java.io.*;
import java.util.Properties;

public class UserSession {
    private static UserSession instance;
    private String currentUser;
    private String currentEmail;
    private boolean isLoggedIn;
    private Properties sessionProps;
    private final String SESSION_FILE = System.getProperty("user.home") + "/.ehc_session";
    
    private UserSession() {
        sessionProps = new Properties();
        loadSession();
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public boolean login(String username, String password) {
        UserAccountManager accountManager = UserAccountManager.getInstance();
        
        if (accountManager.validateLogin(username, password)) {
            this.currentUser = username.trim().toLowerCase();
            this.currentEmail = this.currentUser + "@ehc.com";
            this.isLoggedIn = true;
            
            saveSession();
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.currentUser = null;
        this.currentEmail = null;
        this.isLoggedIn = false;
        clearSession();
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public String getCurrentEmail() {
        return currentEmail;
    }
    
    private void saveSession() {
        try {
            sessionProps.setProperty("username", currentUser);
            sessionProps.setProperty("email", currentEmail);
            sessionProps.setProperty("logged_in", "true");
            sessionProps.setProperty("session_time", String.valueOf(System.currentTimeMillis()));
            
            FileOutputStream fos = new FileOutputStream(SESSION_FILE);
            sessionProps.store(fos, "EHC User Session");
            fos.close();
        } catch (IOException e) {
            System.err.println("Error saving session: " + e.getMessage());
        }
    }
    
    private void loadSession() {
        try {
            File sessionFile = new File(SESSION_FILE);
            if (sessionFile.exists()) {
                FileInputStream fis = new FileInputStream(SESSION_FILE);
                sessionProps.load(fis);
                fis.close();
                
                // Check if session is still valid (within 24 hours)
                String sessionTimeStr = sessionProps.getProperty("session_time");
                if (sessionTimeStr != null) {
                    long sessionTime = Long.parseLong(sessionTimeStr);
                    long currentTime = System.currentTimeMillis();
                    long hoursDiff = (currentTime - sessionTime) / (1000 * 60 * 60);
                    
                    if (hoursDiff < 24) { // Session valid for 24 hours
                        this.currentUser = sessionProps.getProperty("username");
                        this.currentEmail = sessionProps.getProperty("email");
                        this.isLoggedIn = "true".equals(sessionProps.getProperty("logged_in"));
                    } else {
                        clearSession();
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading session: " + e.getMessage());
            clearSession();
        }
    }
    
    private void clearSession() {
        try {
            File sessionFile = new File(SESSION_FILE);
            if (sessionFile.exists()) {
                sessionFile.delete();
            }
            sessionProps.clear();
        } catch (Exception e) {
            System.err.println("Error clearing session: " + e.getMessage());
        }
    }
}