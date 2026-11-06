import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaxiCompany {
    private final String name;
    private final List<Taxi> taxis = new ArrayList<>();
    private final Map<Integer, Taxi> taxisById = new HashMap<>();
    private final Map<String, List<Taxi>> taxisByType = new HashMap<>();
    private final Set<Integer> usedIds = new HashSet<>();

    public TaxiCompany(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required.");
        }
        this.name = name.trim();
    }

    public void addTaxi(Taxi taxi) {
        if (taxi == null || !taxi.getStatus().equals("Free")) {
            throw new IllegalArgumentException("Only a free taxi can be added.");
        }
        if (usedIds.contains(taxi.getId())) {
            throw new IllegalArgumentException("This ID has already been used. Enter a new one.");
        }
        for (Taxi existing : taxis) {
            if (existing.getLicensePlate().replace(" ", "").equalsIgnoreCase(
                    taxi.getLicensePlate().replace(" ", ""))) {
                throw new IllegalArgumentException("This license plate already exists.");
            }
        }
        taxis.add(taxi);
        usedIds.add(taxi.getId());
        rebuildIndexes();
    }
    public Taxi getTaxiById(int id) {
        return taxisById.get(id);
    }
    public void updateTaxi(int id, double rating) {
        Taxi taxi = requireFreeTaxi(id);
        taxi.setRating(rating);
        rebuildIndexes();
    }

    public void removeTaxi(int id) {
        Taxi taxi = requireFreeTaxi(id);
        taxis.remove(taxi);
        rebuildIndexes();
    }
    private void rebuildIndexes() {
        taxisById.clear();
        taxisByType.clear();
        for (Taxi taxi : taxis) {
            taxisById.put(taxi.getId(), taxi);
            String type = taxi.getServiceDescription();
            if (!taxisByType.containsKey(type)) {
                taxisByType.put(type, new ArrayList<Taxi>());
            }
            taxisByType.get(type).add(taxi);
        }
    }

    public List<Taxi> getTaxis() { return new ArrayList<>(taxis); }

    public List<Taxi> getTaxisByType(String type) {
        if (!taxisByType.containsKey(type)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(taxisByType.get(type));
    }

    public List<Taxi> getAvailableTaxis() { return filterTaxis(0); }
    public List<Taxi> filterTaxis(double minRating) {
        if (!Double.isFinite(minRating) || minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException("Minimum rating must be between 0 and 5.");
        }
        List<Taxi> result = new ArrayList<>();
        for (Taxi taxi : taxis) {
            if (taxi.getStatus().equals("Free") && taxi.getRating() >= minRating) {
                result.add(taxi);
            }
        }
        return result;
    }

    public List<Taxi> getSortedTaxis(int order) {
        List<Taxi> result = getTaxis();
        if (order == 1) {
            Collections.sort(result);
        } else if (order == 2) {
            Collections.sort(result, new BrandComparator());
        } else if (order == 3) {
            Collections.sort(result, new RatingComparator());
        } else if (order == 4) {
            Collections.sort(result, new FareComparator());
        } else {
            throw new IllegalArgumentException("Select a sort option from 1 to 4.");
        }
        return result;
    }

    public void showTaxis() { showList(taxis); }
    public void showAvailableTaxis() { showList(getAvailableTaxis()); }

    public void showList(List<Taxi> list) {
        if (list.isEmpty()) {
            System.out.println("No matching taxis.");
        }
        for (Taxi taxi : list) {
            taxi.displayInfo();
        }
    }

    public void printStatistics() {
        double totalRating = 0;
        for (Taxi taxi : taxis) {
            totalRating += taxi.getRating();
        }
        double average = 0;
        if (!taxis.isEmpty()) {
            average = totalRating / taxis.size();
        }
        System.out.println(name + ": taxis " + taxis.size() + ", available " + getAvailableTaxis().size());
        System.out.println("Average rating: " + Math.round(average * 100.0) / 100.0);
        for (String type : taxisByType.keySet()) {
            System.out.println(type + ": " + taxisByType.get(type).size());
        }
    }

    private Taxi requireFreeTaxi(int id) {
        Taxi taxi = getTaxiById(id);
        if (taxi == null) {
            throw new IllegalArgumentException("Taxi not found.");
        }
        if (!taxi.getStatus().equals("Free")) {
            throw new IllegalStateException("Complete or cancel the taxi request first.");
        }
        return taxi;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TaxiCompany)) {
            return false;
        }
        TaxiCompany other = (TaxiCompany) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }

    public List<Taxi> getSortedAvailableTaxis(int order) {
        List<Taxi> result = new ArrayList<>();
        for (Taxi taxi : getSortedTaxis(order)) {
            if (taxi.getStatus().equals("Free")) {
                result.add(taxi);
            }
        }
        return result;
    }
}
