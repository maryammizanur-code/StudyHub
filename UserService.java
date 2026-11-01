
package studymate.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import studymate.database.UserDAO;
import java.security.MessageDigest;
import java.util.Arrays;
import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    private static final int ITERATIONS = 600000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom random = new SecureRandom();

    public boolean registerUser(
            String fullName,
            String email,
            char[] password) throws Exception {

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Full name cannot be empty.");
        }

        if (email == null ||
                !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (password == null || password.length < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        String passwordHash = hashPassword(password);

        return userDAO.registerUser(
                fullName.trim(),
                email.trim(),
                passwordHash);
    }

    private String hashPassword(char[] password)
            throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return "pbkdf2$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }
    
public boolean loginUser(String email, char[] password)
        throws Exception {

    if (email == null || email.trim().isEmpty()
            || password == null || password.length == 0) {
        return false;
    }

    String storedHash =
            userDAO.getPasswordHashByEmail(email.trim());

    if (storedHash == null) {
        return false;
    }

    return verifyPassword(password, storedHash);
}

private boolean verifyPassword(
        char[] password, String storedHash)
        throws GeneralSecurityException {

    String[] parts = storedHash.split("\\$", -1);

    if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
        return false;
    }

    try {
        int iterations = Integer.parseInt(parts[1]);

        if (iterations <= 0 || iterations > 2000000) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

        if (salt.length != 16 || expectedHash.length != 32) {
            return false;
        }

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                expectedHash.length * 8
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256");

            byte[] actualHash =
                    factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(
                    expectedHash, actualHash);

        } finally {
            spec.clearPassword();
        }

    } catch (IllegalArgumentException e) {
        return false;
    }
}
public int getUserIdByEmail(String email) throws SQLException {

    if (email == null || email.trim().isEmpty()) {
        return -1;
    }

    return userDAO.getUserIdByEmail(email.trim());
}


}

