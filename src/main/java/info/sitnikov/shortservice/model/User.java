package info.sitnikov.shortservice.model;

import info.sitnikov.shortservice.service.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Data
@Builder
public class User {
    final String userId;
    String username;
    String hashedPassword;
    final Map<String, Link> links;

    public UserResponse response() {
        return new UserResponse(userId, username, "");
    }

    public static String hash(String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Link addLink(Link link) {
        this.links.put(link.getShortLink(), link);
        return link;
    }

    public boolean equalsSecret(String password) {
        if (!hashedPassword.isEmpty()) {
            return hashedPassword.equals(hash(password));
        }
        return false;
    }
}
