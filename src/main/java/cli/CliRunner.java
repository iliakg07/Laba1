package cli;

import domain.Experiment;
import domain.Run;
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
                case "exp_list" -> handleExperimentList(parsedCommand);
                case "exp_show" -> handleExperimentShow(parsedCommand);
                case "exp_update" -> handleExperimentUpdate(parsedCommand);
                case "run_add" -> handleRunAdd(parsedCommand);

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
        out.println("exp_list - show all experiments");
        out.println("exp_show <id> - show one experiment");
        out.println("exp_update <id> field=value ... - update experiment");
        out.println("run_add <experimentId> - create a run for experiment");

        out.println("exit - stop the program");
    }

    private void handleExit() {
//        Останавливает цикл while
        running = false;
        out.println("CLI stopped.");
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

//    Команда exp_add - добавить эксперимент
    private void handleExperimentAdd(ParsedCommand parsedCommand) {

//        Проверяем что команда вызывается без аргументов
        guaranteeNoArguments(parsedCommand, "exp_add");

        out.println("Creating a new experiment.");

//        По очереди спрашивает name, description, owner username
        String name = readRequiredValue("Name");
        String description = readOptionalValue("Description");
        String ownerUsername = readRequiredValue("Owner username");

//        Передаёт собранные данные в сервис, и выводит ID созданного эксперимента
        Experiment experiment = experimentService.add(name, description, ownerUsername);
        out.println("Experiment created with id " + experiment.getId());
    }

    private String formatExperimentLine(Experiment experiment) {

//        Метод получает объект эксперимента, и выводит текстовую строку из полей объекта
//        Если описание пустое, то выведет "-"
        String description = experiment.getDescription() == null ? "-" : experiment.getDescription();
        return experiment.getId()
                + " | "
                + " | name - " + experiment.getName()
                + " | owner - " + experiment.getOwnerUsername()
                + " | description - " + description;
    }

//    Команда exp_list - показать список добавленных экспериментов
    private void handleExperimentList(ParsedCommand parsedCommand) {

//        Проверяем, что команда вызвана без аргументов
        guaranteeNoArguments(parsedCommand, "exp_list");

        var experiments = experimentService.list();
        if (experiments.isEmpty()) {
            out.println("No experiments found.");
            return;
        }

//        Если список не пустой, то форматированно выводит каждый эксперимент
        out.println("Experiments:");
        for (Experiment experiment : experiments) {
            out.println(formatExperimentLine(experiment));
        }
    }

    private long parseRequiredLongArgument(ParsedCommand parsedCommand, String commandName, String argumentLabel) {

//        Берёт строку аргументов
        String arguments = parsedCommand.arguments();
        if (arguments.isEmpty()) {
            throw new ValidationException(commandName + " requires " + argumentLabel);
        }

//        Делит эту строку по пробелам, если аргументов > 1 - исключение
        String[] parts = arguments.split("\\s+");
        if (parts.length != 1) {
            throw new ValidationException(commandName + " accepts exactly one argument: " + argumentLabel);
        }

//        Преобразование аргумента в long
        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
//            Ловим ввод типа "abc"
            throw new ValidationException(argumentLabel + " must be a number");
        }
    }

    private String formatNullableValue(String value) {
//        Если строковый value == null, то возвращает "-". Если нет - то само value
        return value == null ? "-" : value;
    }

//    Команда exp_show - показать информацию по одному эксперименту
    private void handleExperimentShow(ParsedCommand parsedCommand) {

//        Вызывает метод parse..(), чтобы достать и валидировать ID
        long experimentId = parseRequiredLongArgument(parsedCommand, "exp_show", "experiment id");
        Experiment experiment = experimentService.getById(experimentId);

        out.println("Experiment details:");
        out.println("Id: " + experiment.getId());
        out.println("Name: " + experiment.getName());

//        Используем метод format..(), чтобы в случае пустого описания вывести "-"
        out.println("Description: " + formatNullableValue(experiment.getDescription()));
        out.println("Owner username: " + experiment.getOwnerUsername());
        out.println("Created at: " + experiment.getCreatedAt());
        out.println("Updated at: " + experiment.getUpdatedAt());
    }

    private ExperimentUpdateRequest parseExperimentUpdateRequest(ParsedCommand parsedCommand) {

//        Берёт всю строку аргументов после команды exp_update
        String arguments = parsedCommand.arguments();
        if (arguments.isEmpty()) {
            throw new ValidationException("exp_update requires experiment id and one field=value");
        }

//        Делит строку по пробелам на две части - id + field=value
        String[] parts = arguments.split("\\s+");
        long experimentId;
        try {
            experimentId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new ValidationException("experiment id must be a number");
        }

        if (parts.length != 2) {
            throw new ValidationException("exp_update accepts exactly one field=value");
        }

//        Вторую часть делит на поле и значение
        String[] fieldAndValue = parts[1].split("=", 2);
        if (fieldAndValue.length != 2) {
            throw new ValidationException("Invalid update argument: " + parts[1]);
        }

        return new ExperimentUpdateRequest(experimentId, fieldAndValue[0], fieldAndValue[1]);
    }

    private void handleExperimentUpdate(ParsedCommand parsedCommand) {

//        Получает из парсера id, field, value, находит эксперимент по id
        ExperimentUpdateRequest request = parseExperimentUpdateRequest(parsedCommand);
        Experiment experiment = experimentService.getById(request.id());

        String updatedName = experiment.getName();
        String updatedDescription = experiment.getDescription();
        String updatedOwnerUsername = experiment.getOwnerUsername();

        switch (request.field()) {
//            За один цикл команды обновляем только одно поле
            case "name" -> updatedName = request.value();
            case "description" -> updatedDescription = request.value();
            case "ownerUsername" -> updatedOwnerUsername = request.value();
            default -> throw new ValidationException("Unknown experiment field: " + request.field());
        }

        experimentService.update(experiment.getId(), updatedName, updatedDescription, updatedOwnerUsername);
        out.println("Experiment updated.");
    }

//    Команда run_add - добавить прогон эксперимента (интерактивно)
    private void handleRunAdd(ParsedCommand parsedCommand) {
//         Получение и проверка experimentId через парсер
        long experimentId = parseRequiredLongArgument(parsedCommand, "run_add", "experiment id");

        out.println("Creating a new run.");
//        Пользователь вводит параметры прогона
        String runName = readRequiredValue("Run name");
        String operatorName = readRequiredValue("Operator");

//        Передаёт в сервис полученные данные и выводит ID созданного run
        Run run = runService.add(experimentId, runName, operatorName);
        out.println("Run created with id " + run.getId());
    }

    private record ExperimentUpdateRequest(long id, String field, String value) {
    }

    private record ParsedCommand(String name, String arguments) {
    }
}

