package info.sitnikov.shortservice.entity.menu;

import info.sitnikov.shortservice.controller.Input;
import info.sitnikov.shortservice.controller.Output;
import info.sitnikov.shortservice.entity.security.Authentication;
import info.sitnikov.shortservice.service.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class Context implements Output, Input {
    final Service service;
    private final Output output;
    private final Input input;
    private final AtomicReference<Authentication.Session> session;

    public Context(Service service, Input input, Output output) {
        this.service = service;
        this.output = output;
        this.input = input;
        this.session = new AtomicReference<>();
    }

    public Context(Service service) {
        this(service, new Keyboard(), new Console());
    }

    @Override
    public void close() throws Exception {
        input.close();
    }

    @Override
    public String inputString() {
        return input.inputString();
    }

    @Override
    public Optional<Number> inputNumber() {
        return input.inputNumber();
    }

    @Override
    public Optional<String> inputStringDefault(String defaultValue) {
        return input.inputStringDefault(defaultValue);
    }

    @Override
    public Optional<Number> inputNumberDefault(String defaultValue) {
        return input.inputNumberDefault(defaultValue);
    }

    @Override
    public void print(String format, Object... args) {
        output.print(format, args);
    }

    @Override
    public void println(String format, Object... args) {
        output.println(format, args);
    }

    public void print() {
        String authenticated = "не авторизован";
        Authentication.Session session = this.session.get();
        if (session != null) {
            authenticated = session.user().getUsername();
        }
        output.println("Auth: %s", authenticated);
    }

    @Override
    public void error(String format, Object... args) {
        output.error(format, args);
    }

    @Override
    public void errorln(String format, Object... args) {
        output.errorln(format, args);
    }

    public String selectString(String text) {
        output.print(text);
        output.print(" > ");
        return input.inputString();
    }

    public String selectStringDefault(String text, String defaultValue) {
        output.print(text + " [%s]", defaultValue);
        output.print(" > ");
        return input.inputStringDefault(defaultValue).orElse("");
    }

    public Number selectNumber(String text) {
        Optional<Number> number;
        while (true) {
            output.print(text);
            output.print(" > ");
            number = input.inputNumber();
            if (number.isPresent()) {
                break;
            }
            output.println("Неправильно введён номер");
        }
        return number.get();
    }

    public Number selectNumberDefault(String text, long defaultValue) {
        Optional<Number> number;
        while (true) {
            output.print(text + " [%d]", defaultValue);
            output.print(" > ");
            number = input.inputNumberDefault(String.valueOf(defaultValue));
            if (number.isPresent()) {
                break;
            }
            output.println("Неправильно введён номер");
        }
        return number.get();
    }

    public void putSession(Authentication.Session session) {
        this.session.setRelease(session);
//        this.session.compareAndExchange(null, session);
    }

    public void clearSession() {
        session.setRelease(null);
    }

    public Optional<Authentication.Session> authorized() {
        return Optional.ofNullable(session.get());
    }
}
