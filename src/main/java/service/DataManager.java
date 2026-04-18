package service;

import storage.DataContainer;
import storage.JsonDataValidator;
import storage.JsonFileStorage;
import validation.ValidationException;

import java.io.IOException;
import java.util.ArrayList;

public class DataManager {
    private final ExperimentService experimentService;
    private final RunService runService;
    private final RunResultService runResultService;

    private final JsonFileStorage fileStorage;
    private final JsonDataValidator dataValidator;

    public DataManager(ExperimentService experimentService, RunService runService, RunResultService runResultService){
        this.experimentService = experimentService;
        this.runService = runService;
        this.runResultService = runResultService;
        this.fileStorage = new JsonFileStorage();
        this.dataValidator = new JsonDataValidator();
    }
//Создем метод который сохраняет данные в файл по указанному пути, выбрасывает исключения IOException
    public void saveToFile(String path)
        throws IOException{

        //Берем эксперемент из сервиса возвращаем лист, чтобы получить копию,устанавливаем
        DataContainer container = new DataContainer();
        container.setExperiments(new ArrayList<>(experimentService.list()));
        container.setRuns(new ArrayList<>(runService.list()));
        container.setRunResults(new ArrayList<>(runResultService.list()));

        //Превращаем контейнер в JSON и записываем в файл
        fileStorage.save(container, path);
    }

    //Создаем метод который загружает данные из файла, также выбрасывает IOException, ValidationException
    public void loadFromFile(String path)
        throws IOException, ValidationException{

        //Загружаем контейнер из файла
        DataContainer container = fileStorage.load(path);

        //Проверяем целостность данных, если что то не так выбросит ошибку
        dataValidator.validate(container);

        //Создаем временные сервисы и наполняем их загруженными данными
        ExperimentService tempExpService = new ExperimentService();
        RunService tempRunService = new RunService(tempExpService);
        RunResultService tempResultService = new RunResultService(tempRunService);

        //Загружаем данные напрямую через публичные методы сервисов
        tempExpService.loadFromList(container.getExperiments());
        tempRunService.loadFromList(container.getRuns());
        tempResultService.loadFromList(container.getRunResults());

        //Если все успешно заменяем оригинальные коллекции
         experimentService.replaceData(tempExpService);
         runService.replaceData(tempRunService);
         runResultService.replaceData(tempResultService);
    }

}
