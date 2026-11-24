import java.io.*;
import java.util.Scanner;

/**
 * PS 33 - Product Price Comparator
 * Console application to compare product prices across stores.
 * Concepts: Array, Sorting, Searching, Methods, File Handling.
 * Data file: products.txt (format: id,product,store,price)
 */
public class ProductPriceComparator {

    // ---------- Record class ----------
    static class Listing {
        String id;
        String product;
        String store;
        double price;

        Listing(String id, String product, String store, double price) {
            this.id = id;
            this.product = product;
            this.store = store;
            this.price = price;
        }

        @Override
        public String toString() {
            return id + " | " + product + " | " + store + " | " + formatPrice(price);
        }
    }

    // ---------- Data ----------
    static final String FILE_NAME = "products.txt";
    static Listing[] listings = new Listing[10];
    static int count = 0;       // number of records stored
    static int nextIdNumber = 101; // first generated ID is P101

    // ---------- Main ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadFromFile();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    addListing(sc);
                    break;
                case "2":
                    displayAll();
                    break;
                case "3":
                    sortByPrice();
                    break;
                case "4":
                    searchCheapest(sc);
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }

        saveToFile();
        System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
        sc.close();
    }

    static void printMenu() {
        System.out.println();
        System.out.println("===== Product Price Comparator =====");
        System.out.println("1. Add Listing");
        System.out.println("2. Display All");
        System.out.println("3. Sort by Price (low to high)");
        System.out.println("4. Search Product (show cheapest)");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------- Feature 1: Add ----------
    static void addListing(Scanner sc) {
        System.out.print("Product: ");
        String product = sc.nextLine().trim().replace(",", " ");
        System.out.print("Store: ");
        String store = sc.nextLine().trim().replace(",", " ");
        System.out.print("Price: ");
        String priceText = sc.nextLine().trim();

        if (product.isEmpty() || store.isEmpty()) {
            System.out.println("Product and store name cannot be empty.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid price. Price must be greater than 0.");
            return;
        }

        if (!(price > 0)) {
            System.out.println("Invalid price. Price must be greater than 0.");
            return;
        }

        String id = "P" + nextIdNumber;
        nextIdNumber++;
        Listing item = new Listing(id, product, store, price);
        addToArray(item);
        System.out.println("Product added. ID: " + item);
    }

    // Stores a record in the array, growing it manually when full
    static void addToArray(Listing item) {
        if (count == listings.length) {
            Listing[] bigger = new Listing[listings.length * 2];
            for (int i = 0; i < count; i++) {
                bigger[i] = listings[i];
            }
            listings = bigger;
        }
        listings[count] = item;
        count++;
    }

    // ---------- Feature 2: Display all ----------
    static void displayAll() {
        if (count == 0) {
            System.out.println("No listings available.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + listings[i]);
        }
    }

    // ---------- Feature 3: Sort by price (manual selection sort) ----------
    static void sortByPrice() {
        if (count == 0) {
            System.out.println("No listings available.");
            return;
        }

        // Work on a copy so original ID order stays intact
        Listing[] sorted = new Listing[count];
        for (int i = 0; i < count; i++) {
            sorted[i] = listings[i];
        }

        for (int i = 0; i < count - 1; i++) {
            int min = i;
            for (int j = i + 1; j < count; j++) {
                if (sorted[j].price < sorted[min].price) {
                    min = j;
                }
            }
            Listing temp = sorted[i];
            sorted[i] = sorted[min];
            sorted[min] = temp;
        }

        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + sorted[i]);
        }
    }

    // ---------- Feature 4: Search cheapest (manual linear search) ----------
    static void searchCheapest(Scanner sc) {
        System.out.print("Enter product name to search: ");
        String name = sc.nextLine().trim();

        Listing cheapest = findCheapest(name);
        if (cheapest == null) {
            System.out.println("Product not found.");
        } else {
            System.out.println("Cheapest: " + cheapest);
        }
    }

    static Listing findCheapest(String name) {
        Listing best = null;
        for (int i = 0; i < count; i++) {
            if (listings[i].product.equalsIgnoreCase(name)) {
                if (best == null || listings[i].price < best.price) {
                    best = listings[i];
                }
            }
        }
        return best;
    }

    // ---------- File handling ----------
    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // first run: nothing to load
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 4) continue;
                try {
                    double price = Double.parseDouble(parts[3].trim());
                    Listing item = new Listing(parts[0].trim(), parts[1].trim(),
                                               parts[2].trim(), price);
                    addToArray(item);

                    // keep ID counter ahead of the highest stored ID
                    int num = Integer.parseInt(item.id.substring(1));
                    if (num >= nextIdNumber) {
                        nextIdNumber = num + 1;
                    }
                } catch (NumberFormatException e) {
                    // skip corrupted line
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                Listing l = listings[i];
                pw.println(l.id + "," + l.product + "," + l.store + "," + formatPrice(l.price));
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Prints 1499.0 as 1499, keeps decimals like 1299.5
    static String formatPrice(double p) {
        if (p == Math.floor(p)) {
            return String.valueOf((long) p);
        }
        return String.valueOf(p);
    }
}