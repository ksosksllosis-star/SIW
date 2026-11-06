public class GPSDevice {
    private final String serialNumber;

    public GPSDevice(String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("GPS serial number is required.");
        }
        this.serialNumber = serialNumber.trim();
    }

    public void showLocation() {
        System.out.println("GPS: location sent (simulation).");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GPSDevice)) {
            return false;
        }
        GPSDevice other = (GPSDevice) obj;
        return serialNumber.equals(other.serialNumber);
    }

    @Override
    public int hashCode() { return serialNumber.hashCode(); }
}
