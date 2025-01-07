package info.sitnikov.shortservice.service.dto;

import lombok.Data;

@Data
public class UserResponse {
    String userId;
    String username;
    String password;

    public UserResponse(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
}
