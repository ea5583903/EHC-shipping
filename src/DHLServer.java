import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DHLServer {
    private Map<String, Package> packages;
    private Random random;
    private boolean isRunning;

    public DHLServer() {
        this.packages = new ConcurrentHashMap<>();
        this.random = new Random();
        this.isRunning = false;
    }

    public String generateTrackingNumber() {
        String trackingNumber;
        do {
            trackingNumber = "EHC" + String.format("%09d", random.nextInt(1000000000));
        } while (packages.containsKey(trackingNumber));
        return trackingNumber;
    }

    public Package createPackage(String senderName, String senderAddress, 
                               String recipientName, String recipientAddress, 
                               double weight, String description) {
        String trackingNumber = generateTrackingNumber();
        Package newPackage = new Package(trackingNumber, senderName, senderAddress, 
                                       recipientName, recipientAddress, weight, description);
        packages.put(trackingNumber, newPackage);
        return newPackage;
    }

    public Package findPackage(String trackingNumber) {
        return packages.get(trackingNumber);
    }

    public List<Package> findPackagesBySender(String senderName) {
        return packages.values().stream()
                .filter(pkg -> pkg.getSenderName().toLowerCase().contains(senderName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Package> findPackagesByRecipient(String recipientName) {
        return packages.values().stream()
                .filter(pkg -> pkg.getRecipientName().toLowerCase().contains(recipientName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Package> findPackagesByStatus(PackageStatus status) {
        return packages.values().stream()
                .filter(pkg -> pkg.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean updatePackageStatus(String trackingNumber, PackageStatus newStatus, String location, String description) {
        Package pkg = packages.get(trackingNumber);
        if (pkg != null) {
            pkg.updateStatus(newStatus, location, description);
            return true;
        }
        return false;
    }

    public List<Package> getAllPackages() {
        return new ArrayList<>(packages.values());
    }

    public int getTotalPackages() {
        return packages.size();
    }

    public Map<PackageStatus, Long> getPackageStatusSummary() {
        return packages.values().stream()
                .collect(Collectors.groupingBy(Package::getStatus, Collectors.counting()));
    }

    public void start() {
        isRunning = true;
        System.out.println("EHC Server started successfully!");
        System.out.println("Total packages in system: " + getTotalPackages());
    }

    public void stop() {
        isRunning = false;
        System.out.println("EHC Server stopped.");
    }

    public boolean isRunning() {
        return isRunning;
    }
}