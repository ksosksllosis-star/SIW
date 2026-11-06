import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        TaxiCompany company = new TaxiCompany("City Taxi");
        company.addTaxi(new EconomyTaxi(1, "09 677 RFB", 4.2, "Free", "Hyundai", 10));
        company.addTaxi(new LuxuryTaxi(2, "02 327 BVZ", 4.9, "Free", "Mercedes", true));
        company.addTaxi(new EconomyTaxi(3, "01 456 ABC", 4.6, "Free", "Toyota", 5));
        RideService service = new RideService(company, new PaymentProcessor());
        String role = "";
        Customer customer = null;
        boolean running = true;

        while (running) {
            try {
                if (role.isEmpty()) {
                    System.out.println("\nCity Taxi");
                    System.out.println("1. Customer\n2. Administrator\n0. Exit");
                    String choice = readText(scanner, "Select your role: ");
                    switch (choice) {
                        case "1":
                            String name = readText(scanner, "Enter your name: ");
                            customer = service.getOrCreateCustomer(name);
                            role = "Customer";
                            System.out.println("Welcome, " + customer.getName()
                                    + "! Your customer ID: " + customer.getId());
                            break;
                        case "2":
                            role = "Administrator";
                            break;
                        case "0":
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid menu option.");
                    }
                    continue;
                }

                boolean stay;
                if (role.equals("Customer")) {
                    stay = customerMenu(scanner, company, service, customer);

                } else {
                    stay = adminMenu(scanner, company, service);
                }
                if (!stay) {
                    role = "";
                    customer = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a valid number.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NoSuchElementException e) {
                running = false;
            }
        }
        scanner.close();
        System.out.println("Goodbye!");
    }

    private static boolean customerMenu(Scanner scanner, TaxiCompany company,
                                        RideService service, Customer customer) {
        System.out.println("\nCustomer: " + customer.getName());
        System.out.println("1. Show available taxis\n2. Filter available taxis by rating");
        System.out.println("3. Sort available taxis\n4. Book a taxi");
        System.out.println("5. Cancel my waiting request\n6. Show my rides\n0. Back to role selection");
        String choice = readText(scanner, "Your choice: ");
        switch (choice) {
            case "1":
                company.showAvailableTaxis();
                break;
            case "2":
                double rating = readDouble(scanner, "Minimum rating (0-5): ");
                company.showList(company.filterTaxis(rating));
                break;
            case "3":
                company.showList(company.getSortedAvailableTaxis(readSortOrder(scanner)));
                break;
            case "4":
                bookTaxi(scanner, company, service, customer);
                break;
            case "5":
                service.cancelCustomerRequest(customer.getId(), readInt(scanner, "Waiting request ID: "));
                break;
            case "6":
                service.showCustomerRequests(customer.getId());
                break;
            case "0":
                return false;
            default:
                System.out.println("Invalid menu option.");
        }
        return true;
    }

    private static boolean adminMenu(Scanner scanner, TaxiCompany company, RideService service) {
        System.out.println("\nAdministrator");
        System.out.println("1. Show all taxis\n2. Find a taxi by ID\n3. Add a taxi");
        System.out.println("4. Update a taxi rating\n5. Remove a taxi\n6. Sort all taxis");
        System.out.println("7. Show statistics\n8. Show waiting queue\n9. Dispatch the next taxi");
        System.out.println("10. Show active rides\n11. Complete a ride\n12. Show all requests");
        System.out.println("0. Back to role selection");
        String choice = readText(scanner, "Your choice: ");
        switch (choice) {
            case "1":
                company.showTaxis();
                break;
            case "2":
                Taxi taxi = company.getTaxiById(readInt(scanner, "Taxi ID: "));
                if (taxi == null) {
                    System.out.println("Taxi not found.");
                } else {
                    taxi.displayInfo();
                }
                break;
            case "3":
                addTaxi(scanner, company);
                break;
            case "4":
                int id = readInt(scanner, "Taxi ID: ");
                double rating = readDouble(scanner, "New rating (0-5): ");
                company.updateTaxi(id, rating);
                System.out.println("Rating updated.");
                break;
            case "5":
                company.removeTaxi(readInt(scanner, "Taxi ID to remove: "));
                System.out.println("Taxi removed.");
                break;
            case "6":
                company.showList(company.getSortedTaxis(readSortOrder(scanner)));
                break;
            case "7":
                company.printStatistics();
                service.printStatistics();
                break;
            case "8":
                service.showWaitingQueue();
                break;
            case "9":
                service.processNextRequest();
                break;
            case "10":
                service.showActiveRides();
                break;
            case "11":
                service.completeRide(readInt(scanner, "Active ride ID: "));
                break;
            case "12":
                service.showRequests();
                break;
            case "0":
                return false;
            default:
                System.out.println("Invalid menu option.");
        }
        return true;
    }

    private static void addTaxi(Scanner scanner, TaxiCompany company) {
        int id = readInt(scanner, "New taxi ID: ");
        int type = readInt(scanner, "Type (1 - Economy, 2 - Luxury): ");
        if (type != 1 && type != 2) {
            throw new IllegalArgumentException("Taxi type must be 1 or 2.");
        }
        String brand = readText(scanner, "Brand: ");
        String plate = readText(scanner, "License plate: ");
        double rating = readDouble(scanner, "Rating (0-5): ");
        if (type == 1) {
            int discount = readInt(scanner, "Discount (0-100%): ");
            company.addTaxi(new EconomyTaxi(id, plate, rating, "Free", brand, discount));
        } else {
            boolean water = readYesNo(scanner, "Water available? (yes/no): ");
            company.addTaxi(new LuxuryTaxi(id, plate, rating, "Free", brand, water));
        }
        System.out.println("Taxi added.");
    }

    private static void bookTaxi(Scanner scanner, TaxiCompany company,
                                 RideService service, Customer customer) {
        company.showAvailableTaxis();
        if (company.getAvailableTaxis().isEmpty()) {
            return;
        }
        int taxiId = readInt(scanner, "Available taxi ID: ");
        Taxi taxi = company.getTaxiById(taxiId);
        if (taxi == null || !taxi.getStatus().equals("Free")) {
            throw new IllegalArgumentException("Select an available taxi.");
        }
        String pickup = readText(scanner, "Pickup: ");
        String destination = readText(scanner, "Destination: ");
        if (pickup.isEmpty() || destination.isEmpty() || pickup.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException("Enter two different, non-empty addresses.");
        }
        double distance = readDouble(scanner, "Distance (km): ");
        double fare = service.calculateFare(taxi, distance);
        String method = readText(scanner, "Payment method (Cash/Card): ");
        if (method.equalsIgnoreCase("Cash")) {
            method = "Cash";
        } else if (method.equalsIgnoreCase("Card")) {
            method = "Card";
        } else {
            throw new IllegalArgumentException("Payment method must be Cash or Card.");
        }
        System.out.println("Fare: " + fare + " KGS");
        if (readYesNo(scanner, "Confirm booking? (yes/no): ")) {
            RideRequest request = service.createRequest(customer.getId(), taxiId,
                    pickup, destination, distance, method);
            System.out.println("Request " + request.getId() + " added to the queue. Taxi reserved.");
        } else {
            System.out.println("Booking not created.");
        }
    }

    private static int readSortOrder(Scanner scanner) {
        System.out.println("1. ID\n2. Brand\n3. Rating (highest first)\n4. Fare for 10 km (lowest first)");
        return readInt(scanner, "Sort by: ");
    }

    private static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            String answer = readText(scanner, prompt);
            if (answer.equalsIgnoreCase("yes")) {
                return true;
            }
            if (answer.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Enter yes or no.");
        }
    }

    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(Scanner scanner, String prompt) {
        return Integer.parseInt(readText(scanner, prompt));
    }

    private static double readDouble(Scanner scanner, String prompt) {
        return Double.parseDouble(readText(scanner, prompt).replace(',', '.'));
    }
}
