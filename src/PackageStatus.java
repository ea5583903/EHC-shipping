public enum PackageStatus {
    CREATED("Package created and ready for pickup"),
    PICKED_UP("Package picked up from sender"),
    IN_TRANSIT("Package in transit"),
    AT_SORTING_FACILITY("Package at sorting facility"),
    OUT_FOR_DELIVERY("Package out for delivery"),
    DELIVERED("Package delivered successfully"),
    DELIVERY_ATTEMPTED("Delivery attempted but failed"),
    HELD_AT_FACILITY("Package held at local facility"),
    RETURNED_TO_SENDER("Package returned to sender"),
    LOST("Package lost in transit"),
    DAMAGED("Package damaged");

    private final String description;

    PackageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name().replace("_", " ");
    }
}