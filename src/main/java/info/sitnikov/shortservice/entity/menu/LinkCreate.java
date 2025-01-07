package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.model.User;
import info.sitnikov.shortservice.service.Config;
import info.sitnikov.shortservice.service.dto.LinkResponse;

import java.time.LocalDateTime;
import java.util.Optional;

public final class LinkCreate extends AbstractMenu {
    public LinkCreate() {
        super("Создание короткой ссылки");
    }

    @Override
    public void accept(Context context) {
        String fqdn = context.selectString("Введите ссылку для сокращения");
        if (!fqdn.startsWith("http://") && !fqdn.startsWith("https://")) {
            fqdn = "https://" + fqdn;
        }

        // Если у пользователя уже имеется сохранённая ссылка, то отдаём её сразу
        Optional<User> sessionUser = context.authorized().map(Authentication.Session::user);
        if (sessionUser.isPresent()) {
            User user = sessionUser.get();
            for (var link : user.getLinks().entrySet()) {
                if (link.getValue().getFqdn().equals(fqdn)) {
                    context.println("Данный адрес уже имеет короткую ссылку: %s", Config.SITE_NAME + link.getValue().getShortLink());
                    return;
                }
            }
        }

        Number count = context.selectNumberDefault("Введите количество переходов", Config.MAX_CLICKS);
        Number minutes = context.selectNumberDefault("Введите время жизни в минутах", Config.MAX_MINUTES);

        User user = context.authorized().map(Authentication.Session::user).orElse(null);

        LinkResponse link = context.service.createLink(fqdn, user, count.intValue(), LocalDateTime.now().plusMinutes(minutes.intValue()));
        if (context.authorized().isEmpty()) {
            Optional<Authentication.Session> session = context.service.getAuthentication().authenticateByUserId(link.getUserId());
            session.ifPresent(context::putSession);
            if (session.isEmpty()) {
                context.clearSession();
                context.errorln("Не удалось авторизоваться под пользователем '%s'", link.getUserId());
                return;
            }
            context.println("Был зарегистрирован пользователь с UUID: %s", link.getUserId());
        }
        context.println("Создана короткая ссылка: %s", Config.SITE_NAME + link.getShortURL());
    }
}
