package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.service.Config;

public final class LinkGo extends AbstractMenu {
    public LinkGo() {
        super("Переход по короткой ссылке");
    }

    @Override
    public void accept(Context context) {
        String shortLink = context.selectString("Введите короткую ссылку для перехода");
        if (!shortLink.startsWith(Config.SITE_NAME)) {
            context.errorln("Некорректная ссылка: %s", shortLink);
            return;
        }
        shortLink = shortLink.substring(Config.SITE_NAME.length());
        context.service.openWebpage(shortLink);
    }
}
