public class Customer {
    private final int id;
    private final String name;

    public Customer(int id, String name) {
        if (id <= 0 || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Check the customer ID and name.");
        }
        this.id = id;
        this.name = name.trim();
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
