package info.sitnikov.shortservice.service;

import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.model.Link;
import info.sitnikov.shortservice.model.User;
import info.sitnikov.shortservice.repository.Repository;
import info.sitnikov.shortservice.service.dto.LinkResponse;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

public interface Service {
    Authentication getAuthentication();

    User createUser(String username, String password);

    LinkResponse createLink(String fqdn, User user, int limit, LocalDateTime expiration);

    List<Link> getLinkListByUserId(String userId);

    Config config();
    void storeRepository();

    boolean openWebpage(String shortLink);

    final class Default implements Service {
        private final Repository repository;
        private final Authentication authentication;

        private final Config config;

        public Default(Config config, Repository repository, Authentication auth) {
            this.config = config;
            this.repository = repository;
            this.authentication = auth;
        }

        @Override
        public Authentication getAuthentication() {
            return this.authentication;
        }

        @Override
        public User createUser(String username, String password) {

            // Если пользователь с таким username уже присутствует, то выдаём ошибку
            if (repository.findUserByUserName(username).isPresent()) {
                throw new IllegalArgumentException("Username " + username + " already exists");
            }

            // Ищём уникальный идентификатор пользователя
            String userId;
            do {
                userId = UUID.randomUUID().toString();
            } while (this.repository.findUserByUserId(userId).isPresent());


            // Создаём пользователя
            User user = User.builder().userId(userId).username(username).hashedPassword(User.hash(password)).links(new HashMap<>()).build();
            return repository.storeUser(user).orElseThrow();
        }

        private User createUserUUID() {

            // Ищём уникальный идентификатор пользователя
            String userId;
            do {
                // Если пользователь с таким username уже присутствует, то выдаём ошибку
                userId = UUID.randomUUID().toString();
                if (repository.findUserByUserId(userId).isEmpty()) {
                    break;
                }
            } while (this.repository.findUserByUserId(userId).isPresent());


            // Создаём пользователя
            User user = User.builder().userId(userId).username(userId).hashedPassword(User.hash(userId)).links(new HashMap<>()).build();
            return repository.storeUser(user).orElseThrow();
        }

        @Override
        public @NotNull LinkResponse createLink(String fqdn, User user, int limit, LocalDateTime expiration) {
            // Если пользователя не существует, создадим его, при этом мы должны проверить, что такого пользователя уже нет. При использовании UUID это
            // маловероятно, но всё же...
            if (user == null) {
                user = this.createUserUUID();
            }

            // Если пользователь уже сохранял этот fqdn, то ему должна вернуться такая же короткая ссылка, как и в первый раз
            Map<String, Link> links = user.getLinks();
            for (Link link : links.values()) {
                if (link.getFqdn().equals(fqdn)) {
                    return link.response();
                }
            }

            // Короткая ссылка должна быть уникальной для всех пользователей
            String shortLink;
            do {
                shortLink = generateShort(config.getShortLength());
            } while (this.repository.getLinkByShortLink(shortLink).isPresent());


            // Создаём ссылку и сохраняем её
            Link link = Link.builder()
                    .userId(user.getUserId())
                    .fqdn(fqdn)
                    .shortLink(shortLink)
                    .clicksLeft(limit)
                    .expirationDate(expiration)
                    .build();
            LinkResponse response = user.addLink(link).response();
            this.repository.store();
            return response;
        }

        @Override
        public List<Link> getLinkListByUserId(String userId) {
            return repository.getLinkListByUserId(userId);
        }

        @Override
        public Config config() {
            return config;
        }

        @Override
        public void storeRepository() {
            this.repository.store();
        }

        @Override
        public boolean openWebpage(String shortLink) {
            Optional<Link> linkByShortLink = repository.getLinkByShortLink(shortLink);
            if (linkByShortLink.isEmpty()) {
                System.out.printf("Некорректная ссылка: %s%n", config.getSiteName() + shortLink);
                return false;
            }
            if (linkByShortLink.get().isExpired()) {
                System.out.printf("Превышено количество переходов или достигнуто ограничение по времени: %s%n", config.getSiteName() + shortLink);
                return false;
            }
            Link link = linkByShortLink.get();
            URI uri = URI.create(link.getFqdn());
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                    this.useShortLink(shortLink);
                } catch (Exception ignored) {
                    return false;
                }
            }
            return true;
        }

        @NotNull
        public String generateShort(int limit) {
            char[] table = "012345789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            String shortName;
            do {
                shortName = generateRandomString(table, limit);
            } while (shortName.charAt(0) < 'a' || shortName.charAt(0) > 'z');
            return shortName;
        }

        @NotNull
        public String generateUsername() {
            char[] table = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            return generateRandomString(table, 10);
        }

        @NotNull
        public String generatePassword() {
            char[] table = "012345789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()".toCharArray();
            return generateRandomString(table, 10);
        }

        @NotNull
        public String generateRandomString(char[] table, int limit) {
            Random random = new Random(new Date().getTime());
            StringBuilder buffer = new StringBuilder(limit);
            for (int i = 0; i < limit; i++) {
                char value = table[random.nextInt(table.length)];
                buffer.append(value);
            }
            return buffer.toString();
        }

        private void useShortLink(String shortLink) {
            Optional<Link> linkByShortLink = this.repository.getLinkByShortLink(shortLink);
            if (linkByShortLink.isPresent()) {
                Link link = linkByShortLink.get();
                link.use();
                repository.updateLink(link);
            }
        }

    }
}
