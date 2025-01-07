package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.entity.security.Authentication;

import java.util.Optional;

public final class AuthenticationByLogin extends AbstractMenu {
    private final Authentication authentication;

    public AuthenticationByLogin(Authentication authentication) {
        super("Аутентификация по логину/паролю");
        this.authentication = authentication;
    }

    @Override
    public void accept(Context context) {
        String login = context.selectString("Введите логин");
        String password = context.selectString("Введите пароль");
        Optional<Authentication.Session> session = this.authentication.authenticate(login, password);
        session.ifPresent(context::putSession);
        if (session.isEmpty()) {
            context.clearSession();
            context.errorln("Не удалось авторизоваться под пользователем '%s'", login);
        }
    }
}
