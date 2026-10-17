import java.util.Map;

/**
 * Standalone automated test suite for the CaesarCipher class.
 * Runs without requiring external testing frameworks like JUnit.
 */
public class CaesarCipherTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running Caesar Cipher Test Suite...\n");

        testStandardShift();
        testDecryption();
        testCasePreservation();
        testNonAlphabeticCharacters();
        testLargeShifts();
        testNegativeShifts();
        testZeroShift();
        testEmptyAndNullInputs();
        testBruteForce();
        testInstanceMethods();

        System.out.println("\n--------------------------------------------------");
        System.out.println("Test Results: " + testsPassed + " passed, " + testsFailed + " failed.");
        System.out.println("--------------------------------------------------");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName + " | Expected: \"" + expected + "\", Actual: \"" + actual + "\"");
            testsFailed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName + " | Condition was false");
            testsFailed++;
        }
    }

    private static void testStandardShift() {
        assertEquals("Standard uppercase shift (k=3)",
                "KHOOR",
                CaesarCipher.encrypt("HELLO", 3));
        assertEquals("Standard lowercase shift (k=3)",
                "khoor",
                CaesarCipher.encrypt("hello", 3));
    }

    private static void testDecryption() {
        String original = "ATTACK AT DAWN";
        String encrypted = CaesarCipher.encrypt(original, 5);
        assertEquals("Decryption reverses encryption",
                original,
                CaesarCipher.decrypt(encrypted, 5));
    }

    private static void testCasePreservation() {
        String original = "The Quick Brown Fox Jumps Over The Lazy Dog";
        String encrypted = CaesarCipher.encrypt(original, 4);
        assertEquals("Encrypt with mixed case",
                "Xli Uymgo Fvsar Jsb Nyqtw Sziv Xli Pedc Hsk",
                encrypted);
        assertEquals("Decrypt with mixed case",
                original,
                CaesarCipher.decrypt(encrypted, 4));
    }

    private static void testNonAlphabeticCharacters() {
        String original = "Pin: 1234! User: admin@site.org #2026";
        String encrypted = CaesarCipher.encrypt(original, 7);
        assertEquals("Decrypt text preserving symbols/digits",
                original,
                CaesarCipher.decrypt(encrypted, 7));
    }

    private static void testLargeShifts() {
        String original = "Java Programming";
        // Shift 29 is equivalent to shift 3 (29 % 26 = 3)
        assertEquals("Large positive shift (k=29 equals k=3)",
                CaesarCipher.encrypt(original, 3),
                CaesarCipher.encrypt(original, 29));

        // Shift 52 is equivalent to shift 0 (52 % 26 = 0)
        assertEquals("Full 2-rotation shift (k=52 equals k=0)",
                original,
                CaesarCipher.encrypt(original, 52));
    }

    private static void testNegativeShifts() {
        String original = "Cryptography";
        // Shift -3 is equivalent to shift 23
        assertEquals("Negative shift (k=-3 equals k=23)",
                CaesarCipher.encrypt(original, 23),
                CaesarCipher.encrypt(original, -3));
        assertEquals("Decrypt with negative shift reverses correctly",
                original,
                CaesarCipher.decrypt(CaesarCipher.encrypt(original, -3), -3));
    }

    private static void testZeroShift() {
        String original = "No change should happen here.";
        assertEquals("Shift of 0 returns identical string",
                original,
                CaesarCipher.encrypt(original, 0));
        assertEquals("Decrypt with shift 0 returns identical string",
                original,
                CaesarCipher.decrypt(original, 0));
    }

    private static void testEmptyAndNullInputs() {
        assertEquals("Empty string encryption", "", CaesarCipher.encrypt("", 5));
        assertEquals("Empty string decryption", "", CaesarCipher.decrypt("", 5));
        assertEquals("Null input encryption", null, CaesarCipher.encrypt(null, 5));
        assertEquals("Null input decryption", null, CaesarCipher.decrypt(null, 5));
    }

    private static void testBruteForce() {
        String original = "SECRET MESSAGE";
        String encrypted = CaesarCipher.encrypt(original, 11);
        Map<Integer, String> results = CaesarCipher.bruteForce(encrypted);

        assertEquals("Brute-force returns exactly 25 combinations", 25, results.size());
        assertTrue("Brute-force contains original text at key 11", results.containsKey(11) && original.equals(results.get(11)));
    }

    private static void testInstanceMethods() {
        CaesarCipher cipher = new CaesarCipher(7);
        String text = "Instance vs Static";
        assertEquals("Instance encrypt matches static encrypt",
                CaesarCipher.encrypt(text, 7),
                cipher.encrypt(text));
        assertEquals("Instance decrypt matches static decrypt",
                CaesarCipher.decrypt(cipher.encrypt(text), 7),
                cipher.decrypt(cipher.encrypt(text)));
        assertEquals("Instance getShift returns normalized shift", 7, cipher.getShift());
    }
}
