package info.sitnikov.shortservice.controller;

import java.io.PrintStream;

public interface Output {
    public void print(String format, Object... args);

    public void println(String format, Object... args);

    public void error(String format, Object... args);

    public void errorln(String format, Object... args);

    final class Console implements Output {
        private final PrintStream output;
        private final PrintStream error;

        public Console() {
            output = System.out;
            error = System.err;
        }

        @Override
        public void print(String format, Object... args) {
            output.printf(format, args);
            output.flush();
        }

        @Override
        public void println(String format, Object... args) {
            print(format + "%n", args);
        }

        @Override
        public void error(String format, Object... args) {
            error.printf(format, args);
            error.flush();
        }

        @Override
        public void errorln(String format, Object... args) {
            error(format + "%n", args);
        }
    }
}
