public abstract class Taxi implements Comparable<Taxi> {
    private final int id;
    private final String licensePlate;
    private final String brand;
    private double rating;
    private String status;
    private final GPSDevice gpsDevice;

    public Taxi(int id, String licensePlate, double rating, String status, String brand) {
        if (id <= 0 || licensePlate == null || licensePlate.trim().isEmpty()
                || brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Check the ID, license plate and brand.");
        }
        this.id = id;
        this.licensePlate = licensePlate.trim();
        this.brand = brand.trim();
        setRating(rating);
        setStatus(status);
        gpsDevice = new GPSDevice("GPS-" + id);
    }

    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getBrand() { return brand; }
    public double getRating() { return rating; }
    public String getStatus() { return status; }
    public GPSDevice getGpsDevice() { return gpsDevice; }

    public void setRating(double rating) {
        if (!Double.isFinite(rating) || rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }
        this.rating = rating;
    }
    void setStatus(String status) {
        if (!"Free".equals(status) && !"Reserved".equals(status) && !"Busy".equals(status)) {
            throw new IllegalArgumentException("Invalid taxi status.");
        }
        this.status = status;
    }

    protected void checkDistance(double distance) {
        if (!Double.isFinite(distance) || distance <= 0 || distance > 1000) {
            throw new IllegalArgumentException("Distance must be greater than 0 and at most 1000 km.");
        }
    }

    public void displayInfo() {
        System.out.println("ID " + id + " | " + brand + " | " + licensePlate
                + " | rating " + rating + " | " + status + " | " + getServiceDescription()
                + " | 10 km: " + calculateFare(10) + " KGS");
    }

    public abstract double calculateFare(double distance);
    public abstract String getServiceDescription();
    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Taxi)) {
            return false;
        }
        Taxi other = (Taxi) obj;
        return id == other.id;
    }

    @Override
    public final int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public final int compareTo(Taxi other) {
        return Integer.compare(id, other.id);
    }
}
