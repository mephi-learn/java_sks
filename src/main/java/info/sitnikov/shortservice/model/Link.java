package info.sitnikov.shortservice.model;

import info.sitnikov.shortservice.service.Config;
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

    // Расчёт времени до протухания ссылки. Вернёт отрицательные значения, если ссылка протухнет
    private long duration() {
        return Duration.between(LocalDateTime.now(), this.expirationDate).toMinutes();
    }

    // Ссылку необходимо удалить
    public boolean needDelete() {
        if (Config.DELETE_EXPIRED_AFTER_MINUTES == 0) return false;
        return -1 * this.duration() > Config.DELETE_EXPIRED_AFTER_MINUTES;
    }

    // Оставшееся количество минут для печати. Не может отдавать отрицательным значением
    public long durationPrint() {
        long minutes = this.duration();
        return minutes < 0 ? 0 : minutes;
    }
}
