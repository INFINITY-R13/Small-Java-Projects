import java.util.Map;
import java.util.Scanner;

/**
 * Interactive command-line interface for the Caesar Cipher program.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHeader();

        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleEncrypt(scanner);
                    break;
                case "2":
                    handleDecrypt(scanner);
                    break;
                case "3":
                    handleBruteForce(scanner);
                    break;
                case "4":
                    runQuickDemo();
                    break;
                case "5":
                    System.out.println("\nThank you for using Caesar Cipher. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid option. Please enter a number between 1 and 5.\n");
            }

            if (running) {
                System.out.println("\n" + "=".repeat(50) + "\n");
            }
        }

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("==================================================");
        System.out.println("            CAESAR CIPHER TOOL (JAVA)             ");
        System.out.println("==================================================");
        System.out.println("A classic substitution cipher supporting arbitrary");
        System.out.println("shift keys, case preservation, and brute-force cracking.");
        System.out.println("==================================================\n");
    }

    private static void printMenu() {
        System.out.println("Select an operation:");
        System.out.println("  [1] Encrypt a message");
        System.out.println("  [2] Decrypt a message");
        System.out.println("  [3] Brute-force crack (show all 25 shifts)");
        System.out.println("  [4] Run quick demonstration");
        System.out.println("  [5] Exit");
    }

    private static void handleEncrypt(Scanner scanner) {
        System.out.println("\n--- Encrypt Message ---");
        System.out.print("Enter the plaintext message: ");
        String text = scanner.nextLine();

        int shift = readShiftKey(scanner);
        String encrypted = CaesarCipher.encrypt(text, shift);

        int normalized = CaesarCipher.normalizeShift(shift);
        System.out.println("\n[+] Shift Key: " + shift + " (Normalized: " + normalized + ")");
        System.out.println("[+] Ciphertext: " + encrypted);
    }

    private static void handleDecrypt(Scanner scanner) {
        System.out.println("\n--- Decrypt Message ---");
        System.out.print("Enter the ciphertext message: ");
        String text = scanner.nextLine();

        int shift = readShiftKey(scanner);
        String decrypted = CaesarCipher.decrypt(text, shift);

        int normalized = CaesarCipher.normalizeShift(shift);
        System.out.println("\n[+] Shift Key: " + shift + " (Normalized: " + normalized + ")");
        System.out.println("[+] Plaintext:  " + decrypted);
    }

    private static void handleBruteForce(Scanner scanner) {
        System.out.println("\n--- Brute-Force Crack ---");
        System.out.print("Enter the ciphertext to crack: ");
        String text = scanner.nextLine();

        if (text.trim().isEmpty()) {
            System.out.println("[!] Provided text is empty.");
            return;
        }

        System.out.println("\nAttempting all 25 possible shifts:\n");
        System.out.printf("%-10s | %s%n", "Shift Key", "Decrypted Text");
        System.out.println("-".repeat(50));

        Map<Integer, String> results = CaesarCipher.bruteForce(text);
        for (Map.Entry<Integer, String> entry : results.entrySet()) {
            System.out.printf("Shift %-4d | %s%n", entry.getKey(), entry.getValue());
        }
    }

    private static void runQuickDemo() {
        System.out.println("\n--- Quick Demonstration ---");
        String sample = "Hello, World! Caesar Cipher 2026.";
        int key = 3;

        System.out.println("Original text  : " + sample);
        String encrypted = CaesarCipher.encrypt(sample, key);
        System.out.println("Encrypted (k=3): " + encrypted);
        String decrypted = CaesarCipher.decrypt(encrypted, key);
        System.out.println("Decrypted (k=3): " + decrypted);

        // Negative shift demo
        int negKey = -3;
        String negEncrypted = CaesarCipher.encrypt(sample, negKey);
        System.out.println("Encrypted (k=-3): " + negEncrypted);
        String negDecrypted = CaesarCipher.decrypt(negEncrypted, negKey);
        System.out.println("Decrypted (k=-3): " + negDecrypted);

        // Verification
        boolean match = sample.equals(decrypted) && sample.equals(negDecrypted);
        System.out.println("Integrity Check : " + (match ? "PASSED (Decrypted text matches original)" : "FAILED"));
    }

    private static int readShiftKey(Scanner scanner) {
        while (true) {
            System.out.print("Enter shift key (integer, e.g., 3, -5, 29): ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid integer.");
            }
        }
    }
}
