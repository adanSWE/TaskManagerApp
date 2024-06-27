package backendPackage;

import java.nio.charset.StandardCharsets; // imported for converting the password into bytes
import java.security.MessageDigest; // imported to find and use a certain algorithim for encoding (SHA-256)
import java.security.NoSuchAlgorithmException; // JUST TO HANDLE THE CASE WHERE THE REQUESTED ALGORITHIM IS NOT AVAILABLE
import java.util.Base64; // Used to make the encoding of hashed bytes to string easier

public class SimpleBcrypt {

    /**
     * Hashes the input string using a simple iteration of SHA-256. (Secure Hash Algorithm 256-bit)
     *
     * @param password The password to hash.
     * @return The hashed password.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // locating tool named SHA-256
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8)); // this converts the password into UTF-8 encoding (into bytes)
            return Base64.getEncoder().encodeToString(encodedhash); // the bytes are then hashed using SHA-256 encoding and returned as a String
        } catch (NoSuchAlgorithmException e) { // if algorithim is not found (referring to SHA-256)
            throw new RuntimeException("Algorithm not found", e); // display error
        }
    }

    /**
     * Compares the plaintext password with the hashed password.
     *
     * @param password The plaintext password.
     * @param hashedPassword The hashed password to compare against.
     * @return True if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password); // Storing the hashed version of the user inputted password in the variable hashedInput
        return hashedInput.equals(hashedPassword); // Checking if the strings have the same values (if the hashed password and the inputted hashed passwords are same
    }
    
    
}

