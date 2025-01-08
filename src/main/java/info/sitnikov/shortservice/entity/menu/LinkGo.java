package info.sitnikov.shortservice.entity.menu;

public final class LinkGo extends AbstractMenu {
    public LinkGo() {
        super("Переход по короткой ссылке");
    }

    @Override
    public void accept(Context context) {
        String shortLink = context.selectString("Введите короткую ссылку для перехода");
        if (!shortLink.startsWith(context.service.config().getSiteName())) {
            context.errorln("Некорректная ссылка: %s", shortLink);
            return;
        }
        shortLink = shortLink.substring(context.service.config().getSiteName().length());
        context.service.openWebpage(shortLink);
    }
}
