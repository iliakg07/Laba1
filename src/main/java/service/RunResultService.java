package service;

import domain.MeasurementParam;
import domain.RunResult;
import validation.ValidationException;

import java.util.*;

public class RunResultService {

    private final TreeMap<Long, RunResult> results = new TreeMap<>();
    //    Нужно для проверки, что результат создаётся только для существующего Run
    private final RunService runService;
    //     Локальный счётчик ID, генерируется в сервисе
    private long nextId = 1;

    public RunResultService(RunService runService) {
        this.runService = runService;
    }

    private long generateNextId() {
        return nextId++;
    }

    public RunResult add(long runId, MeasurementParam param, double value, String unit, String comment) {
//        Проверка существования "родительского" Run перед добавлением его результата
        runService.getById(runId);

        long id = generateNextId();
        RunResult result = new RunResult(id, runId, param, value, unit, comment);
        results.put(id, result);
        return result;
    }

    public void remove(long id) {
        if (!results.containsKey(id)) {
            throw new ValidationException("RunResult with id " + id + " not found");
        }
        results.remove(id);
    }

    public RunResult update(long id, MeasurementParam param, double value, String unit, String comment) {
//        Сервис находит нужный объект по ID, обновление реализуется доменным объектом
        RunResult result = getById(id);
        result.update(param, value, unit, comment);
        return result;
    }

    public RunResult getById(long id) {
        RunResult result = results.get(id);

        if (result == null) {
            throw new ValidationException("RunResult with id " + id + " not found");
        }
        return result;
    }

    public Collection<RunResult> list() {
        return List.copyOf(results.values());
    }

    public Collection<RunResult> listByRunId(long runId) {
//        Сначала убеждаемся, что такой Run существует
        runService.getById(runId);
//        Фильтруем все результаты и оставляем рез-ты только указанного прогона
        return results.values().stream()
                .filter(result -> result.getRunId() == runId)
                .toList();
    }
    // ===== 3 ЭТАП: JSON =====
    public List<RunResult> snapshot() {
        return new ArrayList<>(results.values());
    }

    // ===== 3 ЭТАП: JSON =====
    public void loadRestored(List<RunResult> restoredResults) {
        Map<Long, RunResult> loadedResults = new TreeMap<>();
        long maxId = 0;

        for (RunResult result : restoredResults) {
            runService.getById(result.getRunId());

            if (loadedResults.put(result.getId(), result) != null) {
                throw new ValidationException("Duplicate run result id: " + result.getId());
            }

            maxId = Math.max(maxId, result.getId());
        }

        results.clear();
        results.putAll(loadedResults);
        nextId = maxId + 1;
    }
}

