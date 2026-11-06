public class RideRequest {
    private final int id;
    private final Customer customer;
    private final Taxi taxi;
    private final String pickup;
    private final String destination;
    private final double distance;
    private final double fare;
    private final String paymentMethod;
    private String status = "Waiting";

    public RideRequest(int id, Customer customer, Taxi taxi, String pickup,
                       String destination, double distance, String paymentMethod) {
        if (id <= 0 || customer == null || taxi == null) {
            throw new IllegalArgumentException("A valid ID, customer and taxi are required.");
        }
        if (pickup == null || destination == null || pickup.trim().isEmpty()
                || destination.trim().isEmpty() || pickup.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Enter two different, non-empty addresses.");
        }
        if (!"Cash".equals(paymentMethod) && !"Card".equals(paymentMethod)) {
            throw new IllegalArgumentException("Payment method must be Cash or Card.");
        }
        this.id = id;
        this.customer = customer;
        this.taxi = taxi;
        this.pickup = pickup.trim();
        this.destination = destination.trim();
        this.distance = distance;
        this.fare = taxi.calculateFare(distance);
        this.paymentMethod = paymentMethod;
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Taxi getTaxi() { return taxi; }
    public double getFare() { return fare; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    void start() { status = "Active"; }
    void complete() { status = "Completed"; }
    void cancel() { status = "Cancelled"; }

    public void displayInfo() {
        System.out.println("Request " + id + " | " + customer.getName() + " | taxi " + taxi.getId()
                + " | " + pickup + " -> " + destination + " | " + distance + " km"
                + " | " + fare + " KGS | " + paymentMethod + " | " + status);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RideRequest)) {
            return false;
        }
        RideRequest other = (RideRequest) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
