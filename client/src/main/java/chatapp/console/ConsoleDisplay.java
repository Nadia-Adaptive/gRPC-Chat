package chatapp.console;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleDisplay implements ApplicationDisplay, AutoCloseable {
    PrintStream out;
    Scanner scanner;

    public ConsoleDisplay() {
        out = System.out;
        scanner = new Scanner(System.in);
    }

    @Override
    public void print(final String content) {
        out.println(content);
    }

    @Override
    public String readString() {
        return scanner.nextLine();
    }

    @Override
    public void clearDisplay() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
