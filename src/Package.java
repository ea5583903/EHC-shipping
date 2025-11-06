import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Package {
    private String trackingNumber;
    private String senderName;
    private String senderAddress;
    private String recipientName;
    private String recipientAddress;
    private PackageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private List<TrackingEvent> trackingHistory;
    private double weight;
    private String description;

    public Package(String trackingNumber, String senderName, String senderAddress, 
                   String recipientName, String recipientAddress, double weight, String description) {
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.weight = weight;
        this.description = description;
        this.status = PackageStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.trackingHistory = new ArrayList<>();
        addTrackingEvent("Package created", "Initial package creation");
    }

    public void updateStatus(PackageStatus newStatus, String location, String description) {
        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();
        addTrackingEvent(newStatus.toString(), location + " - " + description);
    }

    private void addTrackingEvent(String event, String description) {
        trackingHistory.add(new TrackingEvent(LocalDateTime.now(), event, description));
    }

    // Getters
    public String getTrackingNumber() { return trackingNumber; }
    public String getSenderName() { return senderName; }
    public String getSenderAddress() { return senderAddress; }
    public String getRecipientName() { return recipientName; }
    public String getRecipientAddress() { return recipientAddress; }
    public PackageStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public List<TrackingEvent> getTrackingHistory() { return trackingHistory; }
    public double getWeight() { return weight; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("Package[%s] from %s to %s - Status: %s", 
                           trackingNumber, senderName, recipientName, status);
    }
}