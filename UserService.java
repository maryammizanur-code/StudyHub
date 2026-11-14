package studymate.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;
import java.sql.SQLException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import studymate.database.UserDAO;

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
            throw new IllegalArgumentException( "Full name cannot be empty.");
        }
        if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
        {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        String passwordHash = hashPassword(password);
        return userDAO.registerUser(fullName.trim(),email.trim(),passwordHash);
    }
    private String hashPassword(char[] password)
            throws GeneralSecurityException {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
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
        if (email == null || email.trim().isEmpty()|| password == null || password.length == 0) {
            return false;
        }
        String storedHash =userDAO.getPasswordHashByEmail(email.trim());
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
            if (salt.length != 16 || expectedHash.length != 32){
                return false;
            }
            PBEKeySpec spec = new PBEKeySpec(password,salt,iterations,expectedHash.length*8);
            try {
                SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");                
                byte[] actualHash=factory.generateSecret(spec).getEncoded();
                return MessageDigest.isEqual(expectedHash, actualHash);
            }finally{
                spec.clearPassword();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public int getUserIdByEmail(String email) throws SQLException{
        if (email == null || email.trim().isEmpty()) {
            return -1;
        }
        return userDAO.getUserIdByEmail(email.trim());
    }
    public UserDAO.UserProfile getProfile(int userId)
            throws SQLException{
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        UserDAO.UserProfile profile =userDAO.getProfile(userId);
        if (profile == null) {
            throw new IllegalArgumentException("User profile not found.");
        }
        return profile;
    }
    public void updateProfile(
            int userId,
            String fullName,
            String email) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException( "Invalid user ID.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
        {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        boolean updated = userDAO.updateProfile(userId,fullName.trim(),email.trim());
        if (!updated) {
            throw new IllegalArgumentException("Profile update failed.");
        }
    }
    public void changePassword(
            int userId,
            char[] currentPassword,
            char[] newPassword,
            char[] confirmPassword) throws Exception{
        if (userId <= 0){
            throw new IllegalArgumentException("Invalid user ID.");
        }
        if (currentPassword == null || currentPassword.length == 0)
        {
            throw new IllegalArgumentException("Enter your current password.");
        }
        if (newPassword == null || newPassword.length < 8) {
            throw new IllegalArgumentException( "New password must contain at least 8 characters.");
        }
        if (confirmPassword == null || !Arrays.equals(newPassword, confirmPassword))
        {
            throw new IllegalArgumentException("New passwords do not match.");
        }
        if (Arrays.equals(currentPassword, newPassword)){
            throw new IllegalArgumentException("New password must be different from the current password.");
        }
        String storedHash =userDAO.getPasswordHashByUserId(userId);
        if (storedHash == null || !verifyPassword(currentPassword, storedHash))
        {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        String newPasswordHash = hashPassword(newPassword);
        boolean updated = userDAO.updatePasswordHash(userId,newPasswordHash);
        if (!updated) {
            throw new IllegalArgumentException( "Password update failed.");
        }
    }
}
