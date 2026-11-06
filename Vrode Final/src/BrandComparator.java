import java.util.Comparator;

public class BrandComparator implements Comparator<Taxi> {
    @Override
    public int compare(Taxi first, Taxi second) {
        return first.getBrand().compareToIgnoreCase(second.getBrand());
    }
}
