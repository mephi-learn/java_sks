package info.sitnikov.shortservice.model;

import info.sitnikov.shortservice.service.dto.LinkResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
final public class Link {
    private String userId;
    private String fqdn;
    private String shortLink;
    private int clicksLeft;
    private LocalDateTime expirationDate;

    public void use() {
        clicksLeft--;
    }

    public LinkResponse response() {
        return new LinkResponse(shortLink, this.userId);
    }

    // Ссылка протухла
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate) || clicksLeft <= 0;
    }

    public long duration() {
        long minutes = Duration.between(LocalDateTime.now(), this.expirationDate).toMinutes();
        return minutes < 0 ? 0 : minutes;
    }
}
