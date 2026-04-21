package service;

import domain.Experiment;
import validation.ValidationException;

import java.util.*;

public class ExperimentService {

    //    Хранилище экспериментов - ключ = id, значение = сам объект
    private final TreeMap<Long, Experiment> experiments = new TreeMap<>();
    //    Счётчик id - ответственность сервиса, хранится в нём
    private long nextId = 1;

    private long generateNextId() {
        return nextId++;
    }

    public Experiment add(String name, String description, String ownerUsername) {
        long id = generateNextId();

        Experiment exp = new Experiment(id, name, description, ownerUsername);
        experiments.put(id, exp);
        return exp;
    }

    public void remove(long id) {
        if (!experiments.containsKey(id)) {
            throw new ValidationException("Experiment with id - " + id + " doesn't exist");
        }
//        если эксперимента с таким номером НЕТ - кидаем исключение
        experiments.remove(id);
    }

    public Experiment update(long id, String name, String description, String ownerUsername) {
//        Сервис находит нужный объект по id, само изменение выполняет доменный объект
        Experiment experiment = getById(id);
        experiment.update(name, description, ownerUsername);
        return experiment;
    }

    public Experiment getById(long id) {
        Experiment exp = experiments.get(id);

        if (exp == null) {
            throw new ValidationException("Experiment not found with id - " + id);
        }
        return exp;
    }
    // ===== 3 ЭТАП: JSON =====
    // Отдаёт копию коллекции для сохранения.
    public List<Experiment> snapshot() {
        return new ArrayList<>(experiments.values());
    }
    // ===== 3 ЭТАП: JSON =====
    // Загружает восстановленные объекты и обновляет nextId.
    public void loadRestored(List<Experiment> restoredExperiments) {
        Map<Long, Experiment> loadedExperiments = new TreeMap<>();
        long maxId = 0;

        for (Experiment experiment : restoredExperiments) {
            if (loadedExperiments.put(experiment.getId(), experiment) != null) {
                throw new ValidationException("Duplicate experiment id: " + experiment.getId());
            }
            maxId = Math.max(maxId, experiment.getId());
        }

        experiments.clear();
        experiments.putAll(loadedExperiments);
        nextId = maxId + 1;
    }

    public Collection<Experiment> list() {
        return List.copyOf(experiments.values());
    }

}



