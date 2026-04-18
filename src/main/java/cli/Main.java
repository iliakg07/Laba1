package cli;

public class Main {
    public static void main(String[] args) {
        String initialFilePath = null; //Изначально путь к ссылке не задан null
        if (args.length > 0){ // Проверяем передан ли программе аргументы командной строки
            initialFilePath = args[0];
        }
        // Используем конструктор с возможностью передачи начального файла
        new CliRunner(System.in, System.out, initialFilePath).start();
    }
}
