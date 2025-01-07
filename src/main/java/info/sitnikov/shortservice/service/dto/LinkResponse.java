package info.sitnikov.shortservice.service.dto;

import lombok.Data;

@Data
public class LinkResponse {
    String shortURL;
    String userId;

    public LinkResponse(String shortURL, String userId) {
        this.shortURL = shortURL;
        this.userId = userId;
    }
}
