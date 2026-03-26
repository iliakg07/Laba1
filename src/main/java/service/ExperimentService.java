package service;

import domain.Experiment;
import util.IdGenerator;

import java.util.TreeMap;

public class ExperimentService {

    private final TreeMap<Long, Experiment> experiments = new TreeMap<>(); // коллекция экспериментов
    private final IdGenerator idGenerator = new IdGenerator();

    public Experiment add(String name, String description, String ownerUsername) {

        long id = idGenerator.next();

        Experiment exp = new Experiment(
                id,
                name,
                description,
                ownerUsername,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        experiments.put(id, exp);
        return exp;
    }
}