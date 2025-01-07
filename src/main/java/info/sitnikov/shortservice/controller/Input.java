package info.sitnikov.shortservice.controller;

import java.util.Optional;
import java.util.Scanner;

public interface Input extends AutoCloseable {
    String inputString();

    Optional<Number> inputNumber();

    Optional<String> inputStringDefault(String defaultValue);

    Optional<Number> inputNumberDefault(String defaultValue);

    final class Keyboard implements Input {
        private final Scanner scanner;

        public Keyboard() {
            scanner = new Scanner(System.in);
        }

        @Override
        public void close() {
            scanner.close();
        }

        @Override
        public String inputString() {
            return scanner.nextLine();
        }

        @Override
        public Optional<String> inputStringDefault(String defaultValue) {
            String input = inputString();
            if (input.isEmpty()) {
                input = defaultValue;
            }
            return Optional.of(input);
        }

        @Override
        public Optional<Number> inputNumber() {
            String input = inputString();
            try {
                return Optional.of(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        @Override
        public Optional<Number> inputNumberDefault(String defaultValue) {
            String input = inputString();
            if (input.isEmpty()) {
                input = defaultValue;
            }
            try {
                return Optional.of(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }
}
