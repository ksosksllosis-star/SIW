import java.util.Comparator;

public class FareComparator implements Comparator<Taxi> {
    @Override
    public int compare(Taxi first, Taxi second) {
        return Double.compare(first.calculateFare(10), second.calculateFare(10));
    }
}
