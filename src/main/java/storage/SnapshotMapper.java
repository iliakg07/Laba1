package storage;

import domain.Experiment;
import domain.MeasurementParam;
import domain.Run;
import domain.RunResult;

import java.time.Instant;
import java.util.List;

// ===== 3 ЭТАП: JSON =====
// Переводит доменные объекты в простые JSON-объекты и обратно.
public class SnapshotMapper {

    public DataSnapshot toSnapshot(List<Experiment> experiments,
                                   List<Run> runs,
                                   List<RunResult> results) {
        return new DataSnapshot(
                experiments.stream().map(this::toExperimentData).toList(),
                runs.stream().map(this::toRunData).toList(),
                results.stream().map(this::toRunResultData).toList()
        );
    }

    public List<Experiment> toExperiments(DataSnapshot snapshot) {
        return snapshot.getExperiments().stream()
                .map(this::toExperiment)
                .toList();
    }

    public List<Run> toRuns(DataSnapshot snapshot) {
        return snapshot.getRuns().stream()
                .map(this::toRun)
                .toList();
    }

    public List<RunResult> toRunResults(DataSnapshot snapshot) {
        return snapshot.getRunResults().stream()
                .map(this::toRunResult)
                .toList();
    }

    private ExperimentData toExperimentData(Experiment experiment) {
        return new ExperimentData(
                experiment.getId(),
                experiment.getName(),
                experiment.getDescription(),
                experiment.getOwnerUsername(),
                experiment.getCreatedAt().toString(),
                experiment.getUpdatedAt().toString()
        );
    }

    private RunData toRunData(Run run) {
        return new RunData(
                run.getId(),
                run.getExperimentId(),
                run.getName(),
                run.getOperatorName(),
                run.getCreatedAt().toString(),
                run.getUpdatedAt().toString()
        );
    }

    private RunResultData toRunResultData(RunResult result) {
        return new RunResultData(
                result.getId(),
                result.getRunId(),
                result.getParam().name(),
                result.getValue(),
                result.getUnit(),
                result.getComment(),
                result.getCreatedAt().toString(),
                result.getUpdatedAt().toString()
        );
    }

    private Experiment toExperiment(ExperimentData data) {
        return Experiment.restore(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getOwnerUsername(),
                Instant.parse(data.getCreatedAt()),
                Instant.parse(data.getUpdatedAt())
        );
    }

    private Run toRun(RunData data) {
        return Run.restore(
                data.getId(),
                data.getExperimentId(),
                data.getName(),
                data.getOperatorName(),
                Instant.parse(data.getCreatedAt()),
                Instant.parse(data.getUpdatedAt())
        );
    }

    private RunResult toRunResult(RunResultData data) {
        return RunResult.restore(
                data.getId(),
                data.getRunId(),
                MeasurementParam.valueOf(data.getParam()),
                data.getValue(),
                data.getUnit(),
                data.getComment(),
                Instant.parse(data.getCreatedAt()),
                Instant.parse(data.getUpdatedAt())
        );
    }
}

