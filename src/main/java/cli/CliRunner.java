package cli;

import service.ExperimentService;
import service.RunResultService;
import service.RunService;
import validation.ValidationException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public class CliRunner {
    private final Scanner scanner;
    private final PrintStream out;
    private final ExperimentService experimentService;
    private final RunService runService;
    private final RunResultService runResultService;
    private boolean running;

    public CliRunner() {
        this(System.in, System.out);
    }

    public CliRunner(InputStream inputStream, PrintStream out) {
        this.out = out;
        this.scanner = new Scanner(inputStream);
        this.experimentService = new ExperimentService();
        this.runService = new RunService(experimentService);
        this.runResultService = new RunResultService(runService);
    }

    public static void run() {
        new CliRunner().start();
    }

    private void start() {
        running = true;
        printWelcome();

//        hasNextLine() проверяет, не закончился ли ввод в консоль
        while (running && scanner.hasNextLine()) {
            out.print("> ");

//            .nextLine() читает след. строку до нажатия Enter
//            .trim() убирает пробелы в начале и конце строки - " help " -> "help"
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            handleCommand(line);
        }
    }

    private void handleCommand(String line) {
//        Приводит всю строку к нижнему регистру, чтобы выполнение команды не зависело от ввода
        String command = line.toLowerCase(Locale.ROOT);
//        Locale.ROOT - базовая локаль, не привязанная к региону или языку.
//        Обеспечивает предсказуемый формат данных независимо от ОС или региональных настроек пользователя

        try {
            switch (command) {
//                Перебираем команды и вызываем соответствующий метод
                case "help" -> printHelp();
                case "exit" -> handleExit();
                default -> out.println("Unknown command: " + line + ". Type 'help' to see available commands.");
            }
        } catch (ValidationException e) {
            out.println("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void printWelcome() {
//        Выводит приветственное сообщение
        out.println("Experiment CLI started.");
        out.println("Type 'help' to see available commands.");
    }

    private void printHelp() {
//        Выводит список доступных программ
        out.println("Available commands:");
        out.println("help - show available commands");
        out.println("exit - stop the program");
    }

    private void handleExit() {
//        Останавливает цикл while
        running = false;
        out.println("CLI stopped.");
    }
}

