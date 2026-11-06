public class LuxuryTaxi extends Taxi implements PremiumService {
    private boolean hasWater;

    public LuxuryTaxi(int id, String plate, double rating,
                      String status, String brand, boolean hasWater) {
        super(id, plate, rating, status, brand);
        this.hasWater = hasWater;
    }

    public boolean getHasWater() { return hasWater; }
    public void setHasWater(boolean hasWater) { this.hasWater = hasWater; }

    @Override
    public double calculateFare(double distance) {
        checkDistance(distance);
        return Math.round((150 + distance * 30) * 100.0) / 100.0;
    }

    @Override
    public String getServiceDescription() { return "Luxury"; }

    @Override
    public void providePremiumService() {
        System.out.println("Premium service included.");
    }

    @Override
    public void offerWater() {
        if (hasWater) {
            System.out.println("Water is available in the taxi.");
        } else {
            System.out.println("Water is not available.");
        }
    }
}
