package service;

import domain.Run;
import util.IdGenerator;
import validation.ValidationException;
import validation.RunValidator;

import java.time.Instant;
import java.util.TreeMap;

public class RunService {
    private final TreeMap<Long, Run> runs = new TreeMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public Run add(long experimentId, String name, String ownerUsername) {

        long id = idGenerator.next();

        Run run = new Run(
                id,
                experimentId,
                name,
                ownerUsername,
                Instant.now()
        );

        RunValidator.validate(run);
        runs.put(id, run);
        return run;
    }
}
