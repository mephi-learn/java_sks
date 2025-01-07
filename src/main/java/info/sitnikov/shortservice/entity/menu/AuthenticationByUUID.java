package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.entity.security.Authentication;

import java.util.Optional;

public final class AuthenticationByUUID extends AbstractMenu {
    private final Authentication authentication;

    public AuthenticationByUUID(Authentication authentication) {
        super("Аутентификация по UUID");
        this.authentication = authentication;
    }

    @Override
    public void accept(Context context) {
        String uuid = context.selectString("Введите UUID");
        Optional<Authentication.Session> session = this.authentication.authenticateByUserId(uuid);
        session.ifPresent(context::putSession);
        if (session.isEmpty()) {
            context.clearSession();
            context.errorln("Не удалось авторизоваться под пользователем '%s'", uuid);
        }
    }
}
