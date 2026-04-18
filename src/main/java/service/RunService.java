package service;

import domain.Experiment;
import domain.Run;
import validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public class RunService {
//    Ключ - id прогона
    private final TreeMap<Long, Run> runs = new TreeMap<>();
//    Создаём для проверки того, что прогон создается только для существующего эксперимента
    private final ExperimentService experimentService;
//    Локальный счётчик ID, генерация происходит в сервисе
    private long nextId = 1;

    public RunService(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    private long generateNextId() {
        return nextId++;
    }

    public Run add(long experimentId, String name, String operatorName) {
//        Перед добавлением проверяем, что "родительский" Experiment существует
        experimentService.getById(experimentId);

        long id = generateNextId();
        Run run = new Run(id, experimentId, name, operatorName);
        runs.put(id, run);
        return run;
    }

    public void remove(long id) {
        if (!runs.containsKey(id)) {
            throw new ValidationException("Run with id " + id + " doesn't exist");
        }
//        если эксперимента с таким номером НЕТ - кидаем исключение
        runs.remove(id);
    }

    public Run update(long id, String name, String operatorName) {
//        Обновление реализуется доменным объектом
        Run run = getById(id);
        run.update(name, operatorName);
        return run;
    }

    public Run getById(long id) {
        Run run = runs.get(id);

        if (run == null) {
            throw new ValidationException("Run with id " + id + " doesn't exist");
        }
        return run;
    }

    public Collection<Run> list() {
        return List.copyOf(runs.values()); // возвращаем копию, чтобы никак нельзя было извне поменять оригинал
    }

    public Collection<Run> listByExpId(long experimentId) {
//        Если ExperimentId не существует, будем считать это ошибкой, а не пустым результатом
        experimentService.getById(experimentId);

//        Берём все Run и оставляем только те, что относятся к нужному нам Experiment
        return runs.values().stream()
                .filter(run -> run.getExperimentId() == experimentId)
                .toList();
    }

    public void loadFromList(Collection<Run> runs){
        this.runs.clear(); //Очистка текущей колекции
        for (Run run : runs){ //Заполнение колекции новыми объектами
            this.runs.put(run.getId(), run);
        }
        // Обновляем nextId, для этого создаем поток из колекции где из значений ID находим max, возвращается объект, если колекция пустая 0L  или настоящий максимум
        long maxId = runs.stream().mapToLong(Run::getId).max().orElse(0L);
        this.nextId = maxId + 1; //Новый счетчик для генерации ID
    }


    public void replaceData (RunService other){
        //Чистим содержимое TreeMap
        this.runs.clear();
        // Копируем данные из переданного файла и заполняем (копируя) TreeMap временного сервиса
        this.runs.putAll(other.runs);
        //Копируем счетчик nextId, чтобы не было конфликтов при добавлении новых результатов
        this.nextId = other.nextId;
    }
}