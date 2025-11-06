import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrackingEvent {
    private LocalDateTime timestamp;
    private String event;
    private String description;

    public TrackingEvent(LocalDateTime timestamp, String event, String description) {
        this.timestamp = timestamp;
        this.event = event;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEvent() {
        return event;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: %s", timestamp.format(formatter), event, description);
    }
}