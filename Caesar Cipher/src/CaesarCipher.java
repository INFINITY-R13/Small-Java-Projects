import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the classic Caesar Cipher algorithm in Java.
 *
 * <p>The Caesar Cipher is a type of substitution cipher in which each letter in the
 * plaintext is replaced by a letter some fixed number of positions down the alphabet.
 *
 * <p>This implementation:
 * <ul>
 *   <li>Preserves uppercase and lowercase casing.</li>
 *   <li>Preserves non-alphabetic characters (digits, spaces, punctuation, symbols).</li>
 *   <li>Handles arbitrary positive, negative, and large integer keys via modulo arithmetic.</li>
 *   <li>Supports both static utility calls and object-oriented instance usage.</li>
 *   <li>Provides a brute-force decryption feature exploring all 25 possible shifts.</li>
 * </ul>
 */
public class CaesarCipher {

    public static final int ALPHABET_SIZE = 26;
    private final int shift;

    /**
     * Constructs a CaesarCipher instance configured with a specific shift key.
     *
     * @param shift the number of positions each letter will be shifted
     */
    public CaesarCipher(int shift) {
        this.shift = normalizeShift(shift);
    }

    /**
     * Gets the normalized shift key (between 0 and 25 inclusive) for this cipher instance.
     *
     * @return the normalized shift
     */
    public int getShift() {
        return shift;
    }

    /**
     * Encrypts the provided plaintext using this instance's configured shift.
     *
     * @param plainText the string to encrypt
     * @return the encrypted ciphertext
     */
    public String encrypt(String plainText) {
        return encrypt(plainText, this.shift);
    }

    /**
     * Decrypts the provided ciphertext using this instance's configured shift.
     *
     * @param cipherText the string to decrypt
     * @return the decrypted plaintext
     */
    public String decrypt(String cipherText) {
        return decrypt(cipherText, this.shift);
    }

    /**
     * Encrypts a string using the Caesar cipher with a specified shift key.
     *
     * @param plainText the string to encrypt
     * @param shift the number of positions to shift characters
     * @return the encrypted ciphertext, or null if plainText is null
     */
    public static String encrypt(String plainText, int shift) {
        if (plainText == null) {
            return null;
        }

        int normalizedShift = normalizeShift(shift);
        if (normalizedShift == 0) {
            return plainText;
        }

        StringBuilder result = new StringBuilder(plainText.length());

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);

            if (Character.isUpperCase(c)) {
                char shifted = (char) ('A' + (c - 'A' + normalizedShift) % ALPHABET_SIZE);
                result.append(shifted);
            } else if (Character.isLowerCase(c)) {
                char shifted = (char) ('a' + (c - 'a' + normalizedShift) % ALPHABET_SIZE);
                result.append(shifted);
            } else {
                // Non-alphabetic characters remain untouched
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Decrypts a string using the Caesar cipher with a specified shift key.
     *
     * @param cipherText the string to decrypt
     * @param shift the original key used to encrypt the text
     * @return the decrypted plaintext, or null if cipherText is null
     */
    public static String decrypt(String cipherText, int shift) {
        if (cipherText == null) {
            return null;
        }
        // Decryption is simply encryption with the negative shift
        return encrypt(cipherText, -shift);
    }

    /**
     * Performs a brute-force decryption on ciphertext by testing all 25 possible shifts.
     *
     * @param cipherText the encrypted string to crack
     * @return a map where keys are shift values (1 to 25) and values are the corresponding decryptions
     */
    public static Map<Integer, String> bruteForce(String cipherText) {
        if (cipherText == null) {
            return Collections.emptyMap();
        }

        Map<Integer, String> results = new LinkedHashMap<>();
        for (int key = 1; key < ALPHABET_SIZE; key++) {
            results.put(key, decrypt(cipherText, key));
        }
        return results;
    }

    /**
     * Normalizes an arbitrary shift integer to a valid value in the range [0, 25].
     *
     * @param shift the raw shift value (can be negative or greater than 25)
     * @return the normalized non-negative shift value
     */
    public static int normalizeShift(int shift) {
        return ((shift % ALPHABET_SIZE) + ALPHABET_SIZE) % ALPHABET_SIZE;
    }
}
