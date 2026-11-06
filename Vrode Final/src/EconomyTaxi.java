public class EconomyTaxi extends Taxi implements Discountable {
    private int discountPercent;

    public EconomyTaxi(int id, String plate, double rating,
                       String status, String brand, int discountPercent) {
        super(id, plate, rating, status, brand);
        setDiscountPercent(discountPercent);
    }

    @Override
    public int getDiscountPercent() { return discountPercent; }

    public void setDiscountPercent(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100%.");
        }
        this.discountPercent = discountPercent;
    }

    @Override
    public double calculateFare(double distance) {
        checkDistance(distance);
        double price = (50 + distance * 15) * (1 - discountPercent / 100.0);
        return Math.round(price * 100.0) / 100.0;
    }

    @Override
    public String getServiceDescription() {
        return "Economy";
    }
}
