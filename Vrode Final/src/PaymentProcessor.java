public class PaymentProcessor {
    public boolean processPayment(double amount, String method) {
        if (!Double.isFinite(amount) || amount < 0
                || (!"Cash".equals(method) && !"Card".equals(method))) {
            return false;
        }
        System.out.println("Payment (simulation): " + amount + " KGS, " + method);
        return true;
    }
}
