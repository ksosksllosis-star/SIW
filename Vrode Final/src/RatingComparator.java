import java.util.Comparator;

public class RatingComparator implements Comparator<Taxi> {
    @Override
    public int compare(Taxi first, Taxi second) {
        return Double.compare(second.getRating(), first.getRating());
    }
}
