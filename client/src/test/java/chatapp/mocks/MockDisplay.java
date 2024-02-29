package chatapp.mocks;

import chatapp.console.ApplicationDisplay;

import java.util.Scanner;

public class MockDisplay implements ApplicationDisplay, AutoCloseable {
    String out;
    Scanner scanner;

    public MockDisplay(final String scanner) {
        this.scanner = new Scanner(scanner);
        out = "";
    }

    @Override
    public void print(final String content) {
        out += content;
    }

    @Override
    public String readString() {
        return scanner.nextLine();
    }

    @Override
    public void clearDisplay() {
        out = "";
    }

    @Override
    public void close() {
        scanner.close();
    }

    public String getOutput() {
        return out;
    }
}
