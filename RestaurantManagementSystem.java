import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RestaurantManagementSystem {

    static Map<Integer, String> menuIndex = new LinkedHashMap<>();
    static Map<String, MenuItem> menuItems = new LinkedHashMap<>();
    static Map<String, Integer> inventory = new HashMap<>();
    static Map<String, Double> sales = new HashMap<>();
    static List<Map<String, Integer>> allOrders = new ArrayList<>();
    static List<DeliveryOrder> deliveries = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static int itemCounter = 1;

    static String merchantId = generateUniqueID("merchant");
    static String terminalId = generateUniqueID("terminal");

    public static void main(String[] args) {
        initializeMenuAndInventory();
        System.out.println("🍽===== Welcome to I'LL B. RESTO Management System =====🍽");

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": viewMenu(); break;
                case "2": addMenuItem(); break;
                case "3": placeOrder(); break;
                case "4": viewOrdersSummary(); break;
                case "5": viewInventory(); break;
                case "6": viewSalesReport(); break;
                case "7": viewYourOrders(); break;
                case "8": viewDeliveries(); break;
                case "9":
                    System.out.println("👋 Thank you for using the system. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Invalid choice, please enter a number between 1 and 9.");
            }
        }
    }

    static void printMainMenu() {
        System.out.println("\n📋 Main Menu:");
        System.out.println("1. 🍽 View Menu");
        System.out.println("2. ➕ Add Menu Item");
        System.out.println("3. 🛒 Place Order");
        System.out.println("4. 📊 View Orders Summary");
        System.out.println("5. 📦 View Inventory");
        System.out.println("6. 💹 View Sales Report");
        System.out.println("7. 📋 View Your Orders");
        System.out.println("8. 🚚 View Deliveries COD");
        System.out.println("9. ❌ Exit");
        System.out.print("➡ Enter your choice: ");
    }

    static void initializeMenuAndInventory() {
        String[] itemNames = {"Adobo", "Sinigang na Baboy", "Lechon Kawali", "Pancit Canton", 
                             "Halo-Halo", "Kare-Kare", "Lumpia", "Bistek Tagalog"};
        double[] itemPrices = {150.00, 180.00, 250.00, 120.00, 90.00, 220.00, 100.00, 170.00};
        
        for (int i = 0; i < itemNames.length; i++) {
            String code = String.format("WO%09d", itemCounter++);
            menuItems.put(code, new MenuItem(code, itemNames[i], itemPrices[i]));
            inventory.put(code, 30);
            sales.put(code, 0.0);
        }

        updateMenuIndex();
    }

    static void updateMenuIndex() {
        menuIndex.clear();
        int i = 1;
        for (String code : menuItems.keySet()) {
            menuIndex.put(i++, code);
        }
    }

    static void viewMenu() {
        System.out.println("\n🍽------ Menu ------");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-4s %-11s %-20s %-8s %-8s %-8s %-10s %-15s%n", 
                         "No.", "Item Code", "Item Name", "Price", "Tax", "VAT", "Total", "Stock Status");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");

        int itemNo = 1;
        for (MenuItem item : menuItems.values()) {
            int stock = inventory.get(item.itemCode);
            String stockStatus = stock == 0 ? "🔴 OUT OF STOCK" : "🟢 " + stock + " available";
            
            System.out.printf("%-4d %-11s %-20s ₱%-7.2f ₱%-7.2f ₱%-7.2f ₱%-9.2f %-15s%n", 
                             itemNo++, item.itemCode, item.name, item.basePrice, 
                             item.tax, item.vat, item.getTotalPrice(), stockStatus);
        }
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
    }

    static void addMenuItem() {
        System.out.println("\n➕ Add New Menu Item:");
        
        String name;
        while (true) {
            System.out.print("📝 Enter the name of the new item: ");
            name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("❌ Item name cannot be empty.");
                continue;
            }
            
            if (!name.matches("[a-zA-Z0-9 ]+")) {
                System.out.println("❌ Item name must contain only letters, numbers, and spaces.");
                continue;
            }
            
            boolean exists = false;
            for (MenuItem item : menuItems.values()) {
                if (item.name.equalsIgnoreCase(name)) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                System.out.println("❌ Item with this name already exists.");
                continue;
            }
            
            break;
        }

        double price;
        while (true) {
            System.out.print("💰 Enter base price: ₱");
            try {
                price = Double.parseDouble(scanner.nextLine().trim());
                if (price <= 0) {
                    System.out.println("❌ Price must be greater than 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid price.");
            }
        }

        int stock;
        while (true) {
            System.out.print("📦 Enter initial stock: ");
            try {
                stock = Integer.parseInt(scanner.nextLine().trim());
                if (stock < 0) {
                    System.out.println("❌ Stock cannot be negative.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }

        String code = String.format("WO%09d", itemCounter++);
        menuItems.put(code, new MenuItem(code, name, price));
        inventory.put(code, stock);
        sales.put(code, 0.0);

        updateMenuIndex();
        
        System.out.println("✅ Successfully added new menu item:");
        System.out.println("   📋 Item Code: " + code);
        System.out.println("   🍽 Name: " + name);
        System.out.println("   💰 Base Price: ₱" + String.format("%.2f", price));
        MenuItem newItem = menuItems.get(code);
        System.out.println("   📊 Tax (3%): ₱" + String.format("%.2f", newItem.tax));
        System.out.println("   📊 VAT (5%): ₱" + String.format("%.2f", newItem.vat));
        System.out.println("   💵 Total Price: ₱" + String.format("%.2f", newItem.getTotalPrice()));
        System.out.println("   📦 Initial Stock: " + stock);
    }

static void placeOrder() {
    Map<String, Integer> order = new LinkedHashMap<>();
    System.out.println("\n🛒 Place Your Order (type '0' to finish):");
    viewMenu();

    while (true) {
        System.out.print("➡ Enter item number (or 0 to finish): ");
        String input = scanner.nextLine().trim();
        int itemNo;
        
        try {
            itemNo = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number. Please enter a valid item number.");
            continue;
        }
        
        if (itemNo == 0) break;
        
        if (!menuIndex.containsKey(itemNo)) {
            System.out.println("❌ Invalid item number. Please choose from the menu.");
            continue;
        }
        
        String itemCode = menuIndex.get(itemNo);
        MenuItem menuItem = menuItems.get(itemCode);
        int availableStock = inventory.get(itemCode);
        
        if (availableStock == 0) {
            System.out.println("❌ Sorry, " + menuItem.name + " is currently OUT OF STOCK!");
            continue;
        }
        
        System.out.print("🔢 Enter quantity for " + menuItem.name + " (Available: " + availableStock + "): ");
        int qty;
        
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid quantity.");
            continue;
        }
        
        if (qty <= 0) {
            System.out.println("❌ Quantity must be greater than 0.");
            continue;
        }
        
        if (qty > availableStock) {
            System.out.println("❌ Insufficient stock! Available: " + availableStock);
            continue;
        }
        
        order.put(itemCode, order.getOrDefault(itemCode, 0) + qty);
        inventory.put(itemCode, availableStock - qty);
        sales.put(itemCode, sales.get(itemCode) + menuItem.getTotalPrice() * qty);
        
        System.out.println("✅ Added " + qty + " x " + menuItem.name + " to your order.");
        
        // Show current order status after each item is added
        System.out.println("\n📋 Current Order Items:");
        System.out.println("─────────────────────────────────────────────────────");
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            MenuItem item = menuItems.get(entry.getKey());
            System.out.println("• " + entry.getValue() + " x " + item.name + " - ₱" + 
                             String.format("%.2f", item.getTotalPrice() * entry.getValue()));
        }
        System.out.println("─────────────────────────────────────────────────────");
    }

    if (order.isEmpty()) {
        System.out.println("❌ No items ordered.");
        return;
    }

    // Enhanced order confirmation with detailed breakdown
    System.out.println("\n🎯 CONFIRMING YOUR ORDER");
    System.out.println("═══════════════════════════════════════════════════════");
    displayOrderSummary(order);

    double subtotal = 0;
    double totalTax = 0;
    double totalVat = 0;
    
    for (Map.Entry<String, Integer> entry : order.entrySet()) {
        MenuItem item = menuItems.get(entry.getKey());
        int qty = entry.getValue();
        subtotal += item.basePrice * qty;
        totalTax += item.tax * qty;
        totalVat += item.vat * qty;
    }
    
    double grandTotal = subtotal + totalTax + totalVat;

    // Show detailed price breakdown before confirmation
    System.out.println("\n💰 PRICE BREAKDOWN:");
    System.out.println("─────────────────────────────────────────────────────");
    System.out.printf("Subtotal (Base Price):     ₱%,.2f%n", subtotal);
    System.out.printf("Tax (3%%):                  ₱%,.2f%n", totalTax);
    System.out.printf("VAT (5%%):                  ₱%,.2f%n", totalVat);
    System.out.println("─────────────────────────────────────────────────────");
    System.out.printf("TOTAL BEFORE DISCOUNT:     ₱%,.2f%n", grandTotal);
    System.out.println("─────────────────────────────────────────────────────");

    // Order confirmation prompt
    while (true) {
        System.out.print("\n✅ Confirm your order? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("N")) {
            System.out.println("❌ Order cancelled. Restoring inventory...");
            // Restore inventory for cancelled order
            for (Map.Entry<String, Integer> entry : order.entrySet()) {
                String itemCode = entry.getKey();
                int qty = entry.getValue();
                MenuItem item = menuItems.get(itemCode);
                inventory.put(itemCode, inventory.get(itemCode) + qty);
                sales.put(itemCode, sales.get(itemCode) - (item.getTotalPrice() * qty));
            }
            System.out.println("✅ Inventory restored. Order cancelled.");
            return;
        } else if (confirm.equals("Y")) {
            System.out.println("✅ Order confirmed!");
            break;
        } else {
            System.out.println("❌ Please enter Y to confirm or N to cancel.");
        }
    }

    System.out.print("🎟 Enter promo code (if any, or press Enter to skip): ");
    String promoCode = scanner.nextLine().trim();
    double discount = applyPromoCode(promoCode, grandTotal);
    grandTotal -= discount;

    // Show final total after discount
    if (discount > 0) {
        System.out.println("\n💰 UPDATED TOTAL WITH DISCOUNT:");
        System.out.println("─────────────────────────────────────────────────────");
        System.out.printf("Total Before Discount:     ₱%,.2f%n", grandTotal + discount);
        System.out.printf("Discount Applied:          -₱%,.2f%n", discount);
        System.out.println("─────────────────────────────────────────────────────");
        System.out.printf("FINAL TOTAL:               ₱%,.2f%n", grandTotal);
        System.out.println("─────────────────────────────────────────────────────");
    }

    System.out.println("\n💳 Select Payment Method:");
    System.out.println("1. 💵 Cash");
    System.out.println("2. 💳 Credit Card");
    System.out.println("3. 📱 GCash");
    System.out.println("4. 🚚 Cash on Delivery");
    
    String method;
    while (true) {
        System.out.print("➡ Choose payment method (1-4): ");
        method = scanner.nextLine().trim();
        if (method.matches("[1-4]")) {
            break;
        }
        System.out.println("❌ Please select a valid payment method (1-4).");
    }

    String paymentDetails = handlePaymentMethod(method);

    // Handle Cash on Delivery separately
    if (method.equals("4")) {
        handleCOD(order, subtotal, totalVat, totalTax, grandTotal, promoCode, discount);
        return;
    }

    // For all other payment methods (Cash, Credit Card, GCash), handle payment input
    double amountReceived = 0.0;
    double change = 0.0;

    // Allow user to input the total amount received before printing receipt
    System.out.println("\n💰 PAYMENT PROCESSING");
    System.out.println("═══════════════════════════════════════════════════════");
    System.out.printf("📊 Order Total: ₱%.2f%n", grandTotal);
    
    while (true) {
        if (method.equals("1")) {
            // For cash, they need to pay at least the total amount
            System.out.print("💵 Enter cash amount received: ₱");
        } else if (method.equals("2")) {
            // For credit card, ask for confirmation of exact amount or if they want to add tip
            System.out.printf("💳 Enter amount charged to card (minimum ₱%.2f): ₱", grandTotal);
        } else if (method.equals("3")) {
            // For GCash, ask for confirmation of exact amount or if they want to add tip
            System.out.printf("📱 Enter GCash amount received (minimum ₱%.2f): ₱", grandTotal);
        }
        
        try {
            amountReceived = Double.parseDouble(scanner.nextLine().trim());
            
            if (amountReceived < grandTotal) {
                System.out.printf("❌ Insufficient payment! Amount due: ₱%.2f, Received: ₱%.2f%n", 
                                grandTotal, amountReceived);
                continue;
            }
            
            change = amountReceived - grandTotal;
            
            if (change > 0) {
                if (method.equals("1")) {
                    System.out.printf("✅ Cash payment accepted! Change to give: ₱%.2f%n", change);
                } else {
                    System.out.printf("✅ Payment accepted! Tip amount: ₱%.2f%n", change);
                }
            } else {
                System.out.println("✅ Exact payment received!");
            }
            break;
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid amount.");
        }
    }

    // For non-cash payments, confirm payment was processed
    if (method.equals("2") || method.equals("3")) {
        while (true) {
            System.out.print("✅ Confirm payment was successfully processed? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (confirm.equals("Y")) {
                System.out.println("✅ Payment confirmed and processed successfully!");
                break;
            } else if (confirm.equals("N")) {
                System.out.println("⚠ Payment processing failed. Please try again or use different payment method.");
                return; // Exit and let them try again
            } else {
                System.out.println("❌ Please enter Y or N.");
            }
        }
    }

    // Final confirmation before printing receipt
    System.out.println("\n🧾 GENERATING RECEIPT...");
    System.out.print("📝 Press Enter to print receipt: ");
    scanner.nextLine(); // Wait for user input before printing receipt
    
    System.out.println("Printing receipt...");
    
    // Small delay for better user experience
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        // Handle interruption
    }

    allOrders.add(new LinkedHashMap<>(order));
    printReceipt(order, subtotal, totalVat, totalTax, grandTotal, method, amountReceived, change, paymentDetails, promoCode, discount);
    
    
    System.out.println("\n🎉 ORDER COMPLETED SUCCESSFULLY!");
    System.out.println("═══════════════════════════════════════════════════════════════");
    System.out.println("📋 Order Summary:");
    System.out.printf("   • Total Items: %d%n", order.values().stream().mapToInt(Integer::intValue).sum());
    System.out.printf("   • Total Amount: ₱%,.2f%n", grandTotal);
    System.out.printf("   • Payment Method: %s%n", getPaymentMethodName(method));
    System.out.printf("   • Amount Received: ₱%,.2f%n", amountReceived);
    System.out.printf("   • Change/Tip: ₱%,.2f%n", change);
    System.out.println("🍽 Your order will be prepared shortly!");
    System.out.println("═══════════════════════════════════════════════════════════════");
}
    static void displayOrderSummary(Map<String, Integer> order) {
        System.out.println("\n📋 ===== ORDER SUMMARY =====");
        System.out.println("═════════════════════════════════════════════════════════════════════");
        System.out.printf("%-11s %-20s %-5s %-8s %-8s %-8s %-10s%n", 
                         "Item Code", "Item Name", "Qty", "Price", "Tax", "VAT", "Subtotal");
        System.out.println("══════════════════════════════════════════════════════════════════════");
        
        double orderSubtotal = 0;
        double orderTax = 0;
        double orderVat = 0;
        
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            MenuItem item = menuItems.get(entry.getKey());
            int qty = entry.getValue();
            double itemSubtotal = item.basePrice * qty;
            double itemTax = item.tax * qty;
            double itemVat = item.vat * qty;
            double itemTotal = itemSubtotal + itemTax + itemVat;
            
            System.out.printf("%-11s %-20s %-5d ₱%-7.2f ₱%-7.2f ₱%-7.2f ₱%-9.2f%n", 
                             item.itemCode, item.name, qty, item.basePrice, 
                             item.tax, item.vat, itemTotal);
            
            orderSubtotal += itemSubtotal;
            orderTax += itemTax;
            orderVat += itemVat;
        }
        
        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-47s ₱%-7.2f ₱%-7.2f ₱%-9.2f%n", "TOTALS:", orderTax, orderVat, orderSubtotal + orderTax + orderVat);
        System.out.println("══════════════════════════════════════════════════════════════════════");
    }

    static String handlePaymentMethod(String method) {
        String paymentDetails = "";

        if (method.equals("2")) {
            while (true) {
                System.out.print("💳 Enter 16-digit Card Number: ");
                paymentDetails = scanner.nextLine().trim();

                if (!paymentDetails.matches("\\d{16}")) {
                    System.out.println("❌ Card number must be exactly 16 digits.");
                    continue;
                }

                if (!isValidCard(paymentDetails)) {
                    System.out.println("❌ Invalid card number. Please check and try again.");
                    continue;
                }

                paymentDetails = "🔍 Card: ****-****-****-" + paymentDetails.substring(12);
                break;
            }
        } else if (method.equals("3")) {
            while (true) {
                System.out.print("📱 Enter 13-digit GCash Reference Number: ");
                paymentDetails = scanner.nextLine().trim();

                if (!paymentDetails.matches("\\d{13}")) {
                    System.out.println("❌ GCash reference must be exactly 13 digits.");
                    continue;
                }

                paymentDetails = "🔍 GCash Ref#: *********" + paymentDetails.substring(9);
                break;
            }
        } else if (method.equals("4")) {
            paymentDetails = "🚚 Cash On Delivery";
        }

        return paymentDetails;
    }

    static boolean isValidCard(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }

            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

   static void handleCOD(Map<String, Integer> order, double subtotal, double vat, double tax, double total,
                      String promoCode, double discount) {
    System.out.println("\n🚚 ===== CASH ON DELIVERY SETUP =====");
    
    String name;
    while (true) {
        System.out.print("👤 Customer Name: ");
        name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            continue;
        }
        
        if (!name.matches("[a-zA-Z0-9 ]+")) {
            System.out.println("❌ Name must contain only letters, numbers, and spaces.");
            continue;
        }
        
        break;
    }

    String address;
    while (true) {
        System.out.print("🏠 Delivery Address: ");
        address = scanner.nextLine().trim();
        
        if (address.isEmpty()) {
            System.out.println("❌ Address cannot be empty.");
            continue;
        }
        
        if (address.length() < 10) {
            System.out.println("❌ Please provide a complete address (minimum 10 characters).");
            continue;
        }
        
        break;
    }

    String phone;
    while (true) {
        System.out.print("📞 Contact Number: ");
        phone = scanner.nextLine().trim();
        
        if (!phone.matches("\\d{11}")) {
            System.out.println("❌ Contact number must be exactly 11 digits.");
            continue;
        }
        
        break;
    }

    String deliveryId = generateUniqueID("delivery");
    DeliveryOrder delivery = new DeliveryOrder(deliveryId, name, address, phone, order, total, discount, promoCode);
    deliveries.add(delivery);

    System.out.println("\n✅ COD Order Successfully Placed!");
    System.out.println("═══════════════════════════════════════════════════════════════");
    System.out.println("🆔 Delivery ID: " + deliveryId);
    System.out.println("👤 Customer: " + name);
    System.out.println("🏠 Address: " + address);
    System.out.println("📞 Contact: " + phone);
    System.out.println("💰 Total Amount: ₱" + String.format("%.2f", total));
    System.out.println("🚚 Payment Method: Cash on Delivery");
    System.out.println("📅 Order Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    System.out.println("═══════════════════════════════════════════════════════════════");
    System.out.println("📞 You will be contacted for delivery confirmation.");
    System.out.println("⏰ Estimated delivery time: 30-45 minutes");
    
    // Add receipt printing for COD orders
    System.out.println("\n🧾 GENERATING RECEIPT...");
    System.out.print("📝 Press Enter to print receipt: ");
    scanner.nextLine(); // Wait for user input before printing receipt
    
    System.out.println("Printing receipt...");
    
    // Small delay for better user experience
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        // Handle interruption
    }

    // Add the order to allOrders for tracking
    allOrders.add(new LinkedHashMap<>(order));
    

    System.out.println("\n=================== OFFICIAL RECEIPT ===================");
    System.out.println("                    I'LL B. RESTO                    ");
    System.out.println("                Maguikay, Mandaue City               ");
    System.out.println("                 Contact: 09329412313                ");
    System.out.println("=========================================================");
    System.out.println("🆔 Delivery ID: " + deliveryId);
    System.out.println("👤 Customer: " + name);
    System.out.println("🏠 Address: " + address);
    System.out.println("📞 Contact: " + phone);
    System.out.println("📅 Date/Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    System.out.println("=========================================================");
    System.out.printf("%-11s %-15s %5s %8s %10s%n", "Item Code", "Item", "Qty", "Price", "Amount");
    System.out.println("---------------------------------------------------------");

    for (Map.Entry<String, Integer> entry : order.entrySet()) {
        MenuItem item = menuItems.get(entry.getKey());
        int qty = entry.getValue();
        double itemTotal = item.getTotalPrice() * qty;
        String itemName = item.name.length() > 15 ? item.name.substring(0, 12) + "..." : item.name;

        System.out.printf("%-11s %-15s %5d %8.2f %10.2f%n",
                item.itemCode, itemName, qty, item.getTotalPrice(), itemTotal);
    }

    System.out.println("---------------------------------------------------------");
    System.out.printf("%-35s %15s%n", "Subtotal:", "₱" + String.format("%.2f", subtotal));
    System.out.printf("%-35s %15s%n", "Tax (3%):", "₱" + String.format("%.2f", tax));
    System.out.printf("%-35s %15s%n", "VAT (5%):", "₱" + String.format("%.2f", vat));

    if (!promoCode.isEmpty() && discount > 0) {
        System.out.printf("%-35s %15s%n", "Promo Discount (" + promoCode + "):", "-₱" + String.format("%.2f", discount));
    }

    System.out.println("---------------------------------------------------------");
    System.out.printf("%-35s %15s%n", "TOTAL AMOUNT:", "₱" + String.format("%.2f", total));
    System.out.println("=========================================================");
    System.out.println("💳 Payment Method: Cash on Delivery");
    System.out.println("=========================================================");
    System.out.println("🎉 Thank you for dining with I'LL B. RESTO! 🍽");
    System.out.println("Please be ready with your payment upon delivery.");
    System.out.println("=========================================================");
}



static void printReceipt(Map<String, Integer> order, double subtotal, double vat, double tax,
                          double total, String paymentMethod, double amountReceived, double change, 
                          String paymentDetails, String promoCode, double discount) {
    System.out.println("\n=================== OFFICIAL RECEIPT ===================");
    System.out.println("                    I'LL B. RESTO                    ");
    System.out.println("                Maguikay, Mandaue City               ");
    System.out.println("                 Contact: 09329412313                ");
    System.out.println("=========================================================");
    System.out.println("🏪 Merchant ID: " + merchantId);
    System.out.println("🖥 Terminal ID: " + terminalId);
    System.out.println("📅 Date/Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    System.out.println("🆔 Transaction ID: " + generateUniqueID("txn"));
    System.out.println("=========================================================");
    System.out.printf("%-11s %-15s %5s %8s %10s%n", "Item Code", "Item", "Qty", "Price", "Amount");
    System.out.println("---------------------------------------------------------");

    for (Map.Entry<String, Integer> entry : order.entrySet()) {
        MenuItem item = menuItems.get(entry.getKey());
        int qty = entry.getValue();
        double itemTotal = item.getTotalPrice() * qty;
        String itemName = item.name.length() > 15 ? item.name.substring(0, 12) + "..." : item.name;
        
        System.out.printf("%-11s %-15s %5d %8.2f %10.2f%n", 
                         item.itemCode, itemName, qty, item.getTotalPrice(), itemTotal);
    }

    System.out.println("---------------------------------------------------------");
    System.out.printf("%-35s %15s%n", "Subtotal:", "₱" + String.format("%.2f", subtotal));
    System.out.printf("%-35s %15s%n", "Tax (3%):", "₱" + String.format("%.2f", tax));
    System.out.printf("%-35s %15s%n", "VAT (5%):", "₱" + String.format("%.2f", vat));

    if (!promoCode.isEmpty() && discount > 0) {
        System.out.printf("%-35s %15s%n", "Promo Discount (" + promoCode + "):", "-₱" + String.format("%.2f", discount));
    }

    System.out.println("---------------------------------------------------------");
    System.out.printf("%-35s %15s%n", "TOTAL AMOUNT:", "₱" + String.format("%.2f", total));
    System.out.println("=========================================================");
    System.out.printf("💳 Payment Method: %s%n", getPaymentMethodName(paymentMethod));
    
    if (!paymentDetails.isEmpty()) {
        System.out.println("📋 Payment Details: " + paymentDetails);
    }
    
    System.out.printf("💵 Amount Received:       %s%n", "₱" + String.format("%.2f", amountReceived));
    
    if (paymentMethod.equals("1")) { // Cash payment
        System.out.printf("💰 Change Given:          %s%n", "₱" + String.format("%.2f", change));
    } else if (change > 0) { // Credit card or GCash with tip
        System.out.printf("🎁 Tip Amount:            %s%n", "₱" + String.format("%.2f", change));
    }

    System.out.println("=========================================================");
    System.out.println("      🎉 Thank you for dining with I'LL B. RESTO! 🍽");
    System.out.println("         Please come again and enjoy your meal!         ");
    System.out.println("         For feedback: contact@illbresto.com           ");
    System.out.println("=========================================================");
}

    static String getPaymentMethodName(String method) {
        switch (method) {
            case "1": return "Cash";
            case "2": return "Credit Card";
            case "3": return "GCash";
            case "4": return "Cash on Delivery";
            default: return "Unknown";
        }
    }

    static double applyPromoCode(String code, double total) {
        if (code.isEmpty()) return 0.0;
        
        if (code.equalsIgnoreCase("SAVE10")) {
            System.out.println("🎉 Promo Code Applied: 10% discount!");
            return total * 0.10;
        } else if (code.equalsIgnoreCase("WELCOME5")) {
            System.out.println("🎉 Promo Code Applied: 5% discount!");
            return total * 0.05;
        } else if (code.equalsIgnoreCase("STUDENT")) {
            System.out.println("🎉 Student Discount Applied: 15% discount!");
            return total * 0.15;
        }
        
        System.out.println("❌ Invalid promo code entered.");
        return 0.0;
    }

    static void viewOrdersSummary() {
        if (allOrders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }

        System.out.println("\n📊 ===== ORDERS SUMMARY REPORT =====");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-11s %-20s %-8s %-10s %-10s %-10s %-12s%n",
            "Item Code", "Item Name", "Qty Sold", "Base Price", "Tax", "VAT", "Total Sales");
        System.out.println("═══════════════════════════════════════════════════════════════════════");

        Map<String, Integer> totalQtySold = new HashMap<>();
        Map<String, Double> totalSalesAmount = new HashMap<>();

        for (Map<String, Integer> order : allOrders) {
            for (Map.Entry<String, Integer> entry : order.entrySet()) {
                String itemCode = entry.getKey();
                int qty = entry.getValue();
                MenuItem item = menuItems.get(itemCode);
                
                totalQtySold.put(itemCode, totalQtySold.getOrDefault(itemCode, 0) + qty);
                totalSalesAmount.put(itemCode, totalSalesAmount.getOrDefault(itemCode, 0.0) + (item.getTotalPrice() * qty));
            }
        }

        double grandTotal = 0;
        for (String itemCode : totalQtySold.keySet()) {
            MenuItem item = menuItems.get(itemCode);
            int qtySold = totalQtySold.get(itemCode);
            double itemTotal = totalSalesAmount.get(itemCode);
            
            System.out.printf("%-11s %-20s %-8d ₱%-9.2f ₱%-9.2f ₱%-9.2f ₱%-11.2f%n",
                item.itemCode, item.name, qtySold, item.basePrice, item.tax, item.vat, itemTotal);
            
            grandTotal += itemTotal;
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-60s ₱%-11.2f%n", "GRAND TOTAL SALES:", grandTotal);
        System.out.printf("%-60s %-12d%n", "TOTAL ORDERS PROCESSED:", allOrders.size());
        System.out.println("═══════════════════════════════════════════════════════════════════════");
    }

    static void viewInventory() {
        System.out.println("\n📦 ===== INVENTORY STATUS =====");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.printf("%-11s %-20s %-12s %-15s%n", "Item Code", "Item Name", "Stock", "Status");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        for (Map.Entry<String, MenuItem> entry : menuItems.entrySet()) {
            String code = entry.getKey();
            MenuItem item = entry.getValue();
            int stock = inventory.get(code);
            String status;
            
            if (stock == 0) {
                status = "🔴 OUT OF STOCK";
            } else if (stock <= 5) {
                status = "🟡 LOW STOCK";
            } else {
                status = "🟢 IN STOCK";
            }
            
            System.out.printf("%-11s %-20s %-12d %-15s%n", code, item.name, stock, status);
        }
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
static void viewSalesReport() {
        System.out.println("\n💹 ===== SALES REPORT =====");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-11s %-20s %-12s %-15s%n", "Item Code", "Item Name", "Units Sold", "Total Revenue");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        
        double totalRevenue = 0;
        for (Map.Entry<String, MenuItem> entry : menuItems.entrySet()) {
            String code = entry.getKey();
            MenuItem item = entry.getValue();
            double revenue = sales.get(code);
            int unitsSold = revenue > 0 ? (int)(revenue / item.getTotalPrice()) : 0;
            
            System.out.printf("%-11s %-20s %-12d ₱%-14.2f%n", code, item.name, unitsSold, revenue);
            totalRevenue += revenue;
        }
        
        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-44s ₱%-14.2f%n", "TOTAL REVENUE:", totalRevenue);
        System.out.println("════════════════════════════════════════════════════════════════════════");
    }

    static void viewYourOrders() {
        if (allOrders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }
        

        System.out.println("\n📋 ===== YOUR ORDERS =====");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        
        for (int i = 0; i < allOrders.size(); i++) {
            Map<String, Integer> order = allOrders.get(i);
            System.out.println("🛒 Order #" + (i + 1) + ":");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.printf("%-11s %-20s %-8s %-12s%n", "Item Code", "Item Name", "Quantity", "Total");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            
            double orderTotal = 0;
            for (Map.Entry<String, Integer> entry : order.entrySet()) {
                MenuItem item = menuItems.get(entry.getKey());
                int qty = entry.getValue();
                double itemTotal = item.getTotalPrice() * qty;
                
                System.out.printf("%-11s %-20s %-8d ₱%-11.2f%n", 
                    item.itemCode, item.name, qty, itemTotal);
                orderTotal += itemTotal;
            }
            
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.printf("%-40s ₱%-11.2f%n", "Order Total:", orderTotal);
            System.out.println("═══════════════════════════════════════════════════════════════════════");
        }
    }

    static void viewDeliveries() {
        if (deliveries.isEmpty()) {
            System.out.println("No COD deliveries have been placed yet.");
            return;
        }

        System.out.println("\n🚚 ===== CASH ON DELIVERY ORDERS =====");
        System.out.println("════════════════════════════════════════════════════════════════════════════");
        
        // Display all deliveries with index numbers
        for (int i = 0; i < deliveries.size(); i++) {
            DeliveryOrder delivery = deliveries.get(i);
            System.out.println("📦 Order #" + (i + 1));
            System.out.println("🆔 Delivery ID: " + delivery.deliveryId);
            System.out.println("👤 Customer: " + delivery.customerName);
            System.out.println("🏠 Address: " + delivery.address);
            System.out.println("📞 Contact: " + delivery.phone);
            System.out.println("📅 Order Date: " + delivery.orderDate);
            System.out.println("💰 Total Amount: ₱" + String.format("%.2f", delivery.totalAmount));
            System.out.println("📋 Status: " + (delivery.isApproved ? "✅ APPROVED" : "⏳ PENDING"));
            
            if (!delivery.promoCode.isEmpty() && delivery.discount > 0) {
                System.out.println("🎟 Promo Code: " + delivery.promoCode + " (Discount: ₱" + String.format("%.2f", delivery.discount) + ")");
            }
            
            
            System.out.println("📋 Items Ordered:");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.printf("%-11s %-20s %-8s %-12s%n", "Item Code", "Item Name", "Quantity", "Total");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            
            for (Map.Entry<String, Integer> entry : delivery.orderItems.entrySet()) {
                MenuItem item = menuItems.get(entry.getKey());
                int qty = entry.getValue();
                double itemTotal = item.getTotalPrice() * qty;
                
                System.out.printf("%-11s %-20s %-8d ₱%-11.2f%n", 
                    item.itemCode, item.name, qty, itemTotal);
            }
            
            System.out.println("════════════════════════════════════════════════════════════════════════════");
        }

        // COD Management Options
        System.out.println("\n🔧 COD MANAGEMENT OPTIONS:");
        System.out.println("1. ✅ Approve COD Order");
        System.out.println("2. 📋 View Order Details");
        System.out.println("3. 🔙 Back to Main Menu");
        
        while (true) {
            System.out.print("➡ Enter your choice (1-3): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    approveCODOrder();
                    return;
                case "2":
                    viewCODOrderDetails();
                    return;
                case "3":
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    static void approveCODOrder() {
        // Show only pending orders
        List<Integer> pendingOrderIndices = new ArrayList<>();
        System.out.println("\n⏳ PENDING COD ORDERS:");
        System.out.println("════════════════════════════════════════════════════════════════════════════");
        
        for (int i = 0; i < deliveries.size(); i++) {
            DeliveryOrder delivery = deliveries.get(i);
            if (!delivery.isApproved) {
                pendingOrderIndices.add(i);
                System.out.println("📦 Order #" + (pendingOrderIndices.size()));
                System.out.println("🆔 Delivery ID: " + delivery.deliveryId);
                System.out.println("👤 Customer: " + delivery.customerName);
                System.out.println("🏠 Address: " + delivery.address);
                System.out.println("📞 Contact: " + delivery.phone);
                System.out.println("💰 Total Amount: ₱" + String.format("%.2f", delivery.totalAmount));
                System.out.println("════════════════════════════════════════════════════════════════════════════");
            }
        }
        
        if (pendingOrderIndices.isEmpty()) {
            System.out.println("No pending COD orders to approve.");
            return;
        }
        
        while (true) {
            System.out.print("➡ Enter order number to approve (or 0 to cancel): ");
            try {
                int orderNum = Integer.parseInt(scanner.nextLine().trim());
                
                if (orderNum == 0) {
                    return;
                }
                
                if (orderNum < 1 || orderNum > pendingOrderIndices.size()) {
                    System.out.println("❌ Invalid order number. Please try again.");
                    continue;
                }
                
                int actualIndex = pendingOrderIndices.get(orderNum - 1);
                DeliveryOrder delivery = deliveries.get(actualIndex);
                
                // Confirm approval
                System.out.println("\n📋 ORDER DETAILS TO APPROVE:");
                System.out.println("🆔 Delivery ID: " + delivery.deliveryId);
                System.out.println("👤 Customer: " + delivery.customerName);
                System.out.println("🏠 Address: " + delivery.address);
                System.out.println("💰 Total Amount: ₱" + String.format("%.2f", delivery.totalAmount));
                
                while (true) {
                    System.out.print("✅ Confirm approval? (Y/N): ");
                    String confirm = scanner.nextLine().trim().toUpperCase();
                    
                    if (confirm.equals("Y")) {
                        delivery.isApproved = true;
                        delivery.approvalDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        
                        System.out.println("✅ COD Order Successfully Approved!");
                        System.out.println("🆔 Delivery ID: " + delivery.deliveryId);
                        System.out.println("👤 Customer: " + delivery.customerName);
                        System.out.println("📅 Approval Date: " + delivery.approvalDate);
                        System.out.println("🚚 Status: APPROVED - Ready for Delivery");
                        return;
                    } else if (confirm.equals("N")) {
                        System.out.println("❌ Order approval cancelled.");
                        return;
                    } else {
                        System.out.println("❌ Please enter Y or N.");
                    }
                }
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    static void viewCODOrderDetails() {
        System.out.println("\n📋 SELECT ORDER TO VIEW DETAILS:");
        System.out.println("════════════════════════════════════════════════════════════════════════════");
        
        for (int i = 0; i < deliveries.size(); i++) {
            DeliveryOrder delivery = deliveries.get(i);
            System.out.printf("📦 Order #%d - %s - %s%n", 
                (i + 1), delivery.customerName, 
                (delivery.isApproved ? "✅ APPROVED" : "⏳ PENDING"));
        }
        
        while (true) {
            System.out.print("➡ Enter order number to view details (or 0 to cancel): ");
            try {
                int orderNum = Integer.parseInt(scanner.nextLine().trim());
                
                if (orderNum == 0) {
                    return;
                }
                
                if (orderNum < 1 || orderNum > deliveries.size()) {
                    System.out.println("❌ Invalid order number. Please try again.");
                    continue;
                }
                
                DeliveryOrder delivery = deliveries.get(orderNum - 1);
                
                System.out.println("\n📋 ===== DETAILED ORDER INFORMATION =====");
                System.out.println("════════════════════════════════════════════════════════════════════════════");
                System.out.println("🆔 Delivery ID: " + delivery.deliveryId);
                System.out.println("👤 Customer Name: " + delivery.customerName);
                System.out.println("🏠 Delivery Address: " + delivery.address);
                System.out.println("📞 Contact Number: " + delivery.phone);
                System.out.println("📅 Order Date: " + delivery.orderDate);
                System.out.println("📋 Status: " + (delivery.isApproved ? "✅ APPROVED" : "⏳ PENDING"));
                
                if (delivery.isApproved && delivery.approvalDate != null) {
                    System.out.println("📅 Approval Date: " + delivery.approvalDate);
                }
                
                System.out.println("💰 Total Amount: ₱" + String.format("%.2f", delivery.totalAmount));
                
                if (!delivery.promoCode.isEmpty() && delivery.discount > 0) {
                    System.out.println("🎟 Promo Code Applied: " + delivery.promoCode);
                    System.out.println("💸 Discount Amount: ₱" + String.format("%.2f", delivery.discount));
                }
                
                System.out.println("\n📦 ORDERED ITEMS:");
                System.out.println("─────────────────────────────────────────────────────────────────────");
                System.out.printf("%-11s %-20s %-8s %-10s %-12s%n", "Item Code", "Item Name", "Quantity", "Unit Price", "Total");
                System.out.println("─────────────────────────────────────────────────────────────────────");
                
                for (Map.Entry<String, Integer> entry : delivery.orderItems.entrySet()) {
                    MenuItem item = menuItems.get(entry.getKey());
                    int qty = entry.getValue();
                    double unitPrice = item.getTotalPrice();
                    double itemTotal = unitPrice * qty;
                    
                    System.out.printf("%-11s %-20s %-8d ₱%-9.2f ₱%-11.2f%n", 
                        item.itemCode, item.name, qty, unitPrice, itemTotal);
                }
                
                System.out.println("════════════════════════════════════════════════════════════════════════════");
                return;
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    static String generateUniqueID(String prefix) {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 4-digit random number
        return prefix.toUpperCase() + "_" + timestamp + "_" + randomNum;
    }

    private static void printReceipt(Map<String, Integer> order, double subtotal, double vat, double tax, double total, String deliveryId, String name, String address, String phone, String promoCode, double discount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // MenuItem class
    static class MenuItem {
        String itemCode;
        String name;
        double basePrice;
        double tax;
        double vat;

        public MenuItem(String itemCode, String name, double basePrice) {
            this.itemCode = itemCode;
            this.name = name;
            this.basePrice = basePrice;
            this.tax = basePrice * 0.03; // 3% tax
            this.vat = basePrice * 0.05; // 5% VAT
        }

        public double getTotalPrice() {
            return basePrice + tax + vat;
        }
    }

    // DeliveryOrder class
    static class DeliveryOrder {
        String deliveryId;
        String customerName;
        String address;
        String phone;
        Map<String, Integer> orderItems;
        double totalAmount;
        double discount;
        String promoCode;
        String orderDate;
        boolean isApproved;
        String approvalDate;

        public DeliveryOrder(String deliveryId, String customerName, String address, String phone,
                           Map<String, Integer> orderItems, double totalAmount, double discount, String promoCode) {
            this.deliveryId = deliveryId;
            this.customerName = customerName;
            this.address = address;
            this.phone = phone;
            this.orderItems = new LinkedHashMap<>(orderItems);
            this.totalAmount = totalAmount;
            this.discount = discount;
            this.promoCode = promoCode;
            this.orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.isApproved = false; // Default to pending approval
            this.approvalDate = null;
        }
    }
}