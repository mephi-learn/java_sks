package info.sitnikov.shortservice.repository;

import info.sitnikov.shortservice.adapter.Storage;
import info.sitnikov.shortservice.model.Link;
import info.sitnikov.shortservice.model.User;

import java.util.*;

public interface Repository {
    Optional<User> storeUser(User user);

    Optional<User> findUserByUserName(String username);

    Optional<User> findUserByUserId(String userId);
//    void deleteUserById(String userId);

    void updateLink(Link link);

    Optional<Link> getLinkByShortLink(String shortLink);

    List<Link> getLinkListByUserId(String userId);

    Optional<Map<String, Link>> getLinkMapByUserId(String userId);

    void store();

    void load();

    public class Memory implements Repository {
        private final Storage storage;
        private final Map<String, User> users = new HashMap<>();

        public Memory(Storage storage) {
            this.storage = storage;
        }

        @Override
        public Optional<User> storeUser(User user) {
            this.users.put(user.getUserId(), user);
            this.store();
            return Optional.of(user);
        }

        @Override
        public Optional<User> findUserByUserName(String username) {
            for (User user : this.users.values()) {
                if (user.getUsername().equals(username)) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<User> findUserByUserId(String userId) {
            User user = this.users.get(userId);
            return Optional.ofNullable(user);
        }

        @Override
        public void updateLink(Link link) {
            User user = this.users.get(link.getUserId());
            user.addLink(link);
        }

        @Override
        public Optional<Link> getLinkByShortLink(String shortLink) {
            for (User user : this.users.values()) {
                Map<String, Link> links = user.getLinks();
                if (links.containsKey(shortLink)) {
                    if (links.get(shortLink).needDelete()) {
                        user.getLinks().remove(shortLink);
                        return Optional.empty();
                    }
                    return Optional.ofNullable(links.get(shortLink));
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Link> getLinkListByUserId(String userId) {
            User user = this.users.get(userId);
            List<Link> links = new ArrayList<>();
            for (Link link : user.getLinks().values()) {
                if (link.needDelete()) {
                    user.getLinks().remove(link.getShortLink());
                    continue;
                }
                links.add(link);
            }
            return links;
        }

        @Override
        public Optional<Map<String, Link>> getLinkMapByUserId(String userId) {
            User user = this.users.get(userId);
            if (user == null) {
                return Optional.empty();
            }
            for (Link link : user.getLinks().values()) {
                if (link.needDelete()) {
                    user.getLinks().remove(link.getShortLink());
                }
            }
            return Optional.of(new HashMap<>(user.getLinks()));
        }

        @Override
        public void store() {
            try {
                storage.store(this.users);
            } catch (Exception ignored) {
            }
        }

        @Override
        public void load() {
            try {
                this.users.putAll(this.storage.load());
            } catch (Exception e) {
                this.users.clear();
                this.store();
            }
        }
    }
}
