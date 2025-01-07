package info.sitnikov.shortservice.entity.security;

import info.sitnikov.shortservice.model.User;
import info.sitnikov.shortservice.repository.Repository;

import java.util.Optional;

public interface Authentication {

    static Authentication create(Repository repository) {
        return new Default(repository);
    }

    Optional<Session> authenticate(String username, String password);

    Optional<Session> authenticateByUserId(String userId);

    interface Session {

        static Session of(User user) {
            return new SimpleSession(user);
        }

        User user();
    }

    record SimpleSession(User user) implements Session {
    }

    final class Default implements Authentication {
        private final Repository repository;

        private Default(Repository repository) {
            this.repository = repository;
        }

        @Override
        public Optional<Session> authenticate(String username, String password) {
            Optional<User> user = repository.findUserByUserName(username);
            if (user.isPresent() && user.get().equalsSecret(password)) {
                return Optional.of(new SimpleSession(user.get()));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Session> authenticateByUserId(String userId) {
            return repository.findUserByUserId(userId).map(SimpleSession::new);
        }
    }
}
