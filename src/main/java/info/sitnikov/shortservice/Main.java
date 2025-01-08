package info.sitnikov.shortservice;

import info.sitnikov.shortservice.adapter.Storage;
import info.sitnikov.shortservice.entity.menu.*;
import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.repository.Repository;
import info.sitnikov.shortservice.service.Config;
import info.sitnikov.shortservice.service.Service;

public class Main {
    public static void main(String[] args) throws Exception {
        Config config = new Config("config.json");
        Storage storage = new Storage.FileStorage("storage.json");
        Repository repo = new Repository.Memory(config, storage);
        Authentication authentication = Authentication.create(repo);
        Service service = new Service.Default(config, repo, authentication);
        repo.load();

        if (args.length > 0) {
            for (String shortLink : args) {
                if (!shortLink.startsWith(config.getSiteName())) {
                    System.out.printf("Некорректная ссылка: %s%n", shortLink);
                }
                shortLink = shortLink.substring(config.getSiteName().length());
                service.openWebpage(shortLink);
            }
            repo.store();
            return;
        }

        // При завершении приложения будем сохранять данные
        Runtime.getRuntime().addShutdownHook(new Thread(repo::store));

//        Menu menu = createMenu(repo);
        Menu menu = createMenuOnlyUUID(authentication);

        try {
            Context context = new Context(service);
            menu.select(context);
            context.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        repo.store();
    }

    private static Menu createMenuOnlyUUID(Authentication authentication) {
        Menu root = Menu.root();

        new AuthenticationByUUID(authentication).register(root);
        new LinkCreate().register(root);
        new LinkGo().register(root);
        new LinkEdit().register(root);
        new LinkDelete().register(root);

        return root;
    }

    private static Menu createMenu(Repository repo) {
        Menu root = Menu.root();

        Menu userMenu = root.submenu("Управление пользователями");
        Authentication authentication = Authentication.create(repo);
        new AuthenticationByUUID(authentication).register(userMenu);
        new AuthenticationByLogin(authentication).register(userMenu);
        new Registration(repo).register(userMenu);

        Menu linkMenu = root.submenu("Управление ссылками");
        new LinkCreate().register(linkMenu);
        new LinkEdit().register(linkMenu);
        new LinkDelete().register(linkMenu);
        new LinkGo().register(linkMenu);

        return root;
    }
}
