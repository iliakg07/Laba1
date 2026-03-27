package service;

import domain.RunResult;
import util.IdGenerator;

import java.util.TreeMap;

public class RunResultService {

    private final TreeMap<Long, RunResult> results = new TreeMap<>();
    private final IdGenerator idGenerator = new IdGenerator();
}
