package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.model.User;
import info.sitnikov.shortservice.repository.Repository;

import java.util.Optional;

public final class Registration extends AbstractMenu {
    private final Repository repository;

    public Registration(Repository repository) {
        super("Регистрация");
        this.repository = repository;
    }

    @Override
    public void accept(Context context) {
        String login = context.selectString("Введите логин");
        Optional<User> search = repository.findUserByUserName(login);
        if (search.isEmpty()) {
            String password = context.selectString("Введите пароль");
            String confirm = context.selectString("Подтвердите пароль");
            if (password.equals(confirm)) {
                context.clearSession();
                User user = context.service.createUser(login, password);
//                repository.saveUser(user);
                context.putSession(Authentication.Session.of(user));
            } else {
                context.errorln("Пароль не совпадает");
            }
        } else {
            context.errorln("Пользователь с именем '%s' уже существует", login);
        }
    }
}
