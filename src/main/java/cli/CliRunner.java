package cli;

import domain.Experiment;
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

    void start() {
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
        ParsedCommand parsedCommand = parseCommand(line);

        try {
            switch (parsedCommand.name()) {
//                Перебираем команды и вызываем соответствующий метод
                case "help" -> printHelp();
                case "exit" -> handleExit();
                case "exp_add" -> handleExperimentAdd(parsedCommand);
                default -> out.println("Unknown command: " + line + ". Type 'help' to see available commands.");
            }
        } catch (ValidationException e) {
            out.println("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error: " + e.getMessage());
        }
    }

//    Реализация того, что CLI будет отличать ввод самой команды от передаваемых ей аргументов
    private ParsedCommand parseCommand(String line) {

//        Строка, введённая пользователем (line) разделится по любому пробельному символу
//        Максимум строка будет разделена на 2 части - имя команды + хвост из аргументов
        String[] parts = line.split("\\s+", 2);

//        Приводит всю строку к нижнему регистру.
//        Обеспечивает одинаковый формат данных независимо от ОС или страны пользователя
        String name = parts[0].toLowerCase(Locale.ROOT);

//        Если длина массива строк, полученного ранее, > 1, то применяем .trim() к "хвосту"
//        Иначе - вернуть пустую строку
        String arguments = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedCommand(name, arguments);
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
        out.println("exp_add - create a new experiment");
        out.println("exit - stop the program");
    }

    private void handleExit() {
//        Останавливает цикл while
        running = false;
        out.println("CLI stopped.");
    }

//    Команда exp_add
    private void handleExperimentAdd(ParsedCommand parsedCommand) {
        guaranteeNoArguments(parsedCommand, "exp_add");

        out.println("Creating a new experiment.");
        String name = readRequiredValue("Name");
        String description = readOptionalValue("Description");
        String ownerUsername = readRequiredValue("Owner username");

        Experiment experiment = experimentService.add(name, description, ownerUsername);
        out.println("Experiment created with id " + experiment.getId());
    }

//    Обеспечиваем вывод команды без ошибок, так как ее запуск происходит без ввода аргументов.
//    Кидает исключение, если введём команду с каким-то аргументом (типа exp_add test)
    private void guaranteeNoArguments(ParsedCommand parsedCommand, String commandName) {
        if (!parsedCommand.arguments().isEmpty()) {
            throw new ValidationException(commandName + " does not accept arguments");
        }
    }

    private String readRequiredValue(String label) {
//        Здесь required = поля, которые не могут быть пустыми
        while (true) {
            out.print(label + ": ");
//            label = какой текст показать пользователю перед вводом (name/description/...)

            if (!scanner.hasNextLine()) {
                running = false;
                throw new ValidationException("Input stream was closed");
            }

            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
//            Если пользователь оставил строку пустой, просит ввести значение заново
            out.println(label + " can't be empty.");
        }
    }

    private String readOptionalValue(String label) {
//        Здесь optional = поля, которые могут быть пустыми
        out.print(label + ": ");

        if (!scanner.hasNextLine()) {
            running = false;
            throw new ValidationException("Input stream was closed");
        }

        String value = scanner.nextLine().trim();

//        Если value пустая, вернуть null; иначе - вернуть value (= if-else)
        return value.isBlank() ? null : value;
    }

    private record ParsedCommand(String name, String arguments) {
    }
}

