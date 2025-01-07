package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.controller.Output;
import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.model.Link;
import info.sitnikov.shortservice.model.User;
import info.sitnikov.shortservice.service.Config;

import java.time.LocalDateTime;
import java.util.List;

public final class LinkEdit extends AbstractMenu {
    public LinkEdit() {
        super("Редактирование ссылок");
    }

    private static void printLine(Output output) {
        output.println("---------------------");
    }

    @Override
    public void accept(Context context) {
        User user = context.authorized().map(Authentication.Session::user).orElse(null);
        if (user == null) {
            context.errorln("Для редактирования ссылок необходимо аутентифицироваться");
            return;
        }

        List<Link> links = context.service.getLinkListByUserId(user.getUserId());
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            context.println("%4d: [%s] %s Осталось переходов %d, минут %d", i + 1, Config.SITE_NAME + link.getShortLink(), link.getFqdn(), link.getClicksLeft(),
                    link.durationPrint());
        }
        context.println("%4d: %s", links.size() + 1, "Назад");
        printLine(context);
        context.print("> ");

        String in = context.inputString();
        Link link;
        if (in.equals(String.valueOf(links.size() + 1))) {
            return;
        }
        try {
            int index = Integer.parseInt(in) - 1;
            if (index >= 0 && index < links.size()) {
                link = links.get(index);
            } else {
                context.errorln("Не верный номер. %s", in);
                return;
            }
        } catch (Exception ex) {
            context.errorln("Ошибка ввода. %s", in);
            return;
        }

        String fqdn = context.selectStringDefault("Введите новую ссылку", link.getFqdn());
        Number count = context.selectNumberDefault("Введите новое количество переходов", link.getClicksLeft());
        Number minutes = context.selectNumberDefault("Введите новое время жизни в минутах", link.durationPrint());
        link.setFqdn(fqdn);
        link.setClicksLeft(count.intValue());
        link.setExpirationDate(LocalDateTime.now().plusMinutes(minutes.intValue()));
        context.service.storeRepository();
    }
}
