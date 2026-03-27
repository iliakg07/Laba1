package service;

import domain.MeasurementParam;
import domain.RunResult;
import util.IdGenerator;
import validation.RunResultValidator;

import java.time.Instant;
import java.util.TreeMap;

public class RunResultService {

    private final TreeMap<Long, RunResult> results = new TreeMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public RunResult add(long runId,
                         MeasurementParam param,
                         double value,
                         String unit,
                         String comment
                         ) {

        long id = idGenerator.next();

        RunResult result = new RunResult(
                id,
                runId,
                param,
                value,
                unit,
                comment,
                Instant.now()
        );

        RunResultValidator.validate(result);
        results.put(id, result);
        return result;

    }
}
