package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.model.User;
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
                    context.println("Данный адрес уже имеет короткую ссылку: %s", context.service.config().getSiteName() + link.getValue().getShortLink());
                    return;
                }
            }
        }

        Number count = context.selectNumberDefault("Введите количество переходов", context.service.config().getMaxClicks());
        Number minutes = context.selectNumberDefault("Введите время жизни в минутах", context.service.config().getMaxMinutes());

        // Получаем аутентифицированного пользователя
        User user = context.authorized().map(Authentication.Session::user).orElse(null);

        // Создаём ссылку и пользователя, если нет аутентификации
        LinkResponse link = context.service.createLink(fqdn, user, count.intValue(), LocalDateTime.now().plusMinutes(minutes.intValue()));

        // Если аутентификации нет, то будем аутентифицировать только что созданного пользователя
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
        context.println("Создана короткая ссылка: %s", context.service.config().getSiteName() + link.getShortURL());
        context.service.storeRepository();
    }
}
