package info.sitnikov.shortservice.entity.menu;

import java.util.function.Consumer;

public abstract class AbstractMenu implements Consumer<Context> {
    protected final String name;

    protected AbstractMenu(String name) {
        this.name = name;
    }

    public final Menu register(Menu menu) {
        return menu.addSub(name, this);
    }
}
