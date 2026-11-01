package studymate.service;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import studymate.database.ResourceDAO;

public class ResourceService {

    private final ResourceDAO resourceDAO = new ResourceDAO();

    public boolean addResource(
            int userId,
            String title,
            String url,
            String description
    ) throws SQLException {

        validateUserId(userId);

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Resource title cannot be empty."
            );
        }

        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Resource URL cannot be empty."
            );
        }

        String cleanTitle = title.trim();
        String cleanUrl = url.trim();
        String cleanDescription =
                description == null ? "" : description.trim();

        if (cleanTitle.length() > 255) {
            throw new IllegalArgumentException(
                    "Title cannot exceed 255 characters."
            );
        }

        try {
            URI uri = URI.create(cleanUrl);
            String scheme = uri.getScheme();

            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null
                    || uri.getHost().isBlank()) {

                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Enter a valid URL beginning with "
                    + "http:// or https://."
            );
        }

        return resourceDAO.addResource(
                userId,
                cleanTitle,
                cleanUrl,
                cleanDescription
        );
    }

    public List<Object[]> getResources(int userId)
            throws SQLException {

        validateUserId(userId);

        return resourceDAO.getResources(userId);
    }

    public boolean deleteResource(int userId, int resourceId)
            throws SQLException {

        validateUserId(userId);

        if (resourceId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid resource ID."
            );
        }

        return resourceDAO.deleteResource(userId, resourceId);
    }

    private void validateUserId(int userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }
    }
}