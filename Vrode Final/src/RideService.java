import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class RideService {
    private final TaxiCompany company;
    private final PaymentProcessor paymentProcessor;
    private final Map<Integer, Customer> customers = new HashMap<>();
    private final List<RideRequest> history = new ArrayList<>();
    private final Queue<RideRequest> queue = new ArrayDeque<>();
    private int nextId = 1;

    public RideService(TaxiCompany company, PaymentProcessor paymentProcessor) {
        if (company == null || paymentProcessor == null) {
            throw new IllegalArgumentException("Company and payment processor are required.");
        }
        this.company = company;
        this.paymentProcessor = paymentProcessor;
    }

    public void addCustomer(Customer customer) {
        if (customer == null || customers.containsKey(customer.getId())) {
            throw new IllegalArgumentException("Check the customer: the ID must be unique.");
        }
        customers.put(customer.getId(), customer);
    }

    public double calculateFare(Taxi taxi, double distance) {
        if (taxi == null) {
            throw new IllegalArgumentException("Taxi not found.");
        }
        return taxi.calculateFare(distance);
    }
    public RideRequest createRequest(int customerId, int taxiId, String pickup,
                                     String destination, double distance, String method) {
        Customer customer = customers.get(customerId);
        Taxi taxi = company.getTaxiById(taxiId);
        if (customer == null || taxi == null) {
            throw new IllegalArgumentException("Customer or taxi not found.");
        }
        if (!taxi.getStatus().equals("Free")) {
            throw new IllegalStateException("This taxi is already busy or reserved.");
        }
        RideRequest request = new RideRequest(nextId, customer, taxi, pickup, destination, distance, method);
        queue.offer(request);
        history.add(request);
        taxi.setStatus("Reserved");
        nextId++;
        return request;
    }
    public RideRequest processNextRequest() {
        RideRequest request = queue.poll();
        if (request == null) {
            System.out.println("The queue is empty.");
            return null;
        }
        request.start();
        request.getTaxi().setStatus("Busy");
        System.out.println("Taxi dispatched. Request " + request.getId());
        request.getTaxi().getGpsDevice().showLocation();
        if (request.getTaxi() instanceof PremiumService) {
            PremiumService premium = (PremiumService) request.getTaxi();
            premium.providePremiumService();
            premium.offerWater();
        }
        return request;
    }

    public void completeRide(int id) {
        RideRequest request = findRequest(id);
        if (!request.getStatus().equals("Active")) {
            throw new IllegalStateException("Only an active ride can be completed.");
        }
        if (!paymentProcessor.processPayment(request.getFare(), request.getPaymentMethod())) {
            throw new IllegalStateException("Payment failed. The ride remains active.");
        }
        request.complete();
        request.getTaxi().setStatus("Free");
        System.out.println("Ride completed. Taxi available.");
    }

    public void cancelRequest(int id) {
        RideRequest request = findRequest(id);
        if (!request.getStatus().equals("Waiting")) {
            throw new IllegalStateException("Only a waiting request can be cancelled.");
        }
        queue.remove(request);
        request.cancel();
        request.getTaxi().setStatus("Free");
        System.out.println("Request cancelled. Taxi available.");
    }

    public RideRequest findRequest(int id) {
        for (RideRequest request : history) {
            if (request.getId() == id) {
                return request;
            }
        }
        throw new IllegalArgumentException("Request not found.");
    }

    public List<RideRequest> getHistory() { return new ArrayList<>(history); }
    public List<RideRequest> getQueue() { return new ArrayList<>(queue); }
    public Set<Customer> getCustomersForTaxi(int taxiId) {
        Set<Customer> result = new HashSet<>();
        for (RideRequest request : history) {
            if (request.getTaxi().getId() == taxiId && request.getStatus().equals("Completed")) {
                result.add(request.getCustomer());
            }
        }
        return result;
    }

    public void showRequests() {
        System.out.println("Requests in queue: " + queue.size());
        if (history.isEmpty()) {
            System.out.println("The history is empty.");
        }
        for (RideRequest request : history) {
            request.displayInfo();
        }
    }

    public double getRevenue() {
        double total = 0;
        for (RideRequest request : history) {
            if (request.getStatus().equals("Completed")) {
                total += request.getFare();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public void printStatistics() {
        int completed = 0;
        for (RideRequest request : history) {
            if (request.getStatus().equals("Completed")) {
                completed++;
            }
        }
        System.out.println("Requests: " + history.size() + ", queued: " + queue.size()
                + ", completed: " + completed + ", revenue: " + getRevenue() + " KGS");
    }

    public Customer getOrCreateCustomer(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        name = name.trim();
        int nextCustomerId = 1;
        for (Customer customer : customers.values()) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
            nextCustomerId = Math.max(nextCustomerId, customer.getId() + 1);
        }
        Customer customer = new Customer(nextCustomerId, name);
        addCustomer(customer);
        return customer;
    }

    public void showCustomerRequests(int customerId) {
        boolean found = false;
        for (RideRequest request : history) {
            if (request.getCustomer().getId() == customerId) {
                request.displayInfo();
                found = true;
            }
        }
        if (!found) {
            System.out.println("You have no ride requests yet.");
        }
    }

    public void cancelCustomerRequest(int customerId, int requestId) {
        for (RideRequest request : history) {
            if (request.getId() == requestId && request.getCustomer().getId() == customerId) {
                cancelRequest(requestId);
                return;
            }
        }
        throw new IllegalArgumentException("Request not found for this customer.");
    }

    public void showWaitingQueue() {
        System.out.println("Waiting requests: " + queue.size());
        for (RideRequest request : queue) {
            request.displayInfo();
        }
    }

    public void showActiveRides() {
        boolean found = false;
        for (RideRequest request : history) {
            if (request.getStatus().equals("Active")) {
                request.displayInfo();
                found = true;
            }
        }
        if (!found) {
            System.out.println("There are no active rides.");
        }
    }
}
