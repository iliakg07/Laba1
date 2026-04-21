package storage;

import domain.MeasurementParam;
import validation.ValidationException;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ===== 3 ЭТАП: JSON =====
// Проверяет данные из JSON до загрузки в сервисы.
public class SnapshotValidator {

    public void validate(DataSnapshot snapshot) {
        if (snapshot == null) {
            throw new ValidationException("Snapshot can't be null");
        }

        List<ExperimentData> experiments = requireSection(snapshot.getExperiments(), "experiments");
        List<RunData> runs = requireSection(snapshot.getRuns(), "runs");
        List<RunResultData> results = requireSection(snapshot.getRunResults(), "runResults");

        validateExperiments(experiments);
        validateRuns(runs);
        validateResults(results);
        validateLinks(experiments, runs, results);
    }

    private <T> List<T> requireSection(List<T> section, String name) {
        if (section == null) {
            throw new ValidationException("File is missing '" + name + "' section");
        }
        return section;
    }

    private void validateExperiments(List<ExperimentData> experiments) {
        Set<Long> ids = new HashSet<>();

        for (ExperimentData experiment : experiments) {
            requireNotNull(experiment, "experiments");

            long id = requirePositive(experiment.getId(), "Experiment.id");
            requireUnique(ids, id, "Duplicate experiment id: ");

            requireNonBlank(experiment.getName(), "Experiment.name can't be empty");
            requireMaxLength(experiment.getName(), 128, "Experiment.name too long");
            requireMaxLength(experiment.getDescription(), 512, "Experiment.description too long");

            requireNonBlank(experiment.getOwnerUsername(), "Experiment.ownerUsername can't be empty");
            requireMaxLength(experiment.getOwnerUsername(), 128, "Experiment.ownerUsername too long");

            validateDates(experiment.getCreatedAt(), experiment.getUpdatedAt(), "Experiment id=" + id);
        }
    }

    private void validateRuns(List<RunData> runs) {
        Set<Long> ids = new HashSet<>();

        for (RunData run : runs) {
            requireNotNull(run, "runs");

            long id = requirePositive(run.getId(), "Run.id");
            requireUnique(ids, id, "Duplicate run id: ");

            requirePositive(run.getExperimentId(), "Run.experimentId");

            requireNonBlank(run.getName(), "Run.name can't be empty");
            requireMaxLength(run.getName(), 128, "Run.name too long");

            requireNonBlank(run.getOperatorName(), "Run.operatorName can't be empty");
            requireMaxLength(run.getOperatorName(), 64, "Run.operatorName too long");

            validateDates(run.getCreatedAt(), run.getUpdatedAt(), "Run id=" + id);
        }
    }

    private void validateResults(List<RunResultData> results) {
        Set<Long> ids = new HashSet<>();

        for (RunResultData result : results) {
            requireNotNull(result, "runResults");

            long id = requirePositive(result.getId(), "RunResult.id");
            requireUnique(ids, id, "Duplicate run result id: ");

            requirePositive(result.getRunId(), "RunResult.runId");

            MeasurementParam param = parseParam(result.getParam());

            if (result.getValue() == null) {
                throw new ValidationException("RunResult.value is required");
            }

            validateValue(param, result.getValue());

            requireNonBlank(result.getUnit(), "RunResult.unit can't be empty");
            requireMaxLength(result.getUnit(), 16, "RunResult.unit too long");
            requireMaxLength(result.getComment(), 128, "RunResult.comment too long");

            validateDates(result.getCreatedAt(), result.getUpdatedAt(), "RunResult id=" + id);
        }
    }

    private void validateLinks(List<ExperimentData> experiments,
                               List<RunData> runs,
                               List<RunResultData> results) {
        Set<Long> experimentIds = new HashSet<>();
        for (ExperimentData experiment : experiments) {
            experimentIds.add(experiment.getId());
        }

        Set<Long> runIds = new HashSet<>();
        for (RunData run : runs) {
            if (!experimentIds.contains(run.getExperimentId())) {
                throw new ValidationException("Run id=" + run.getId()
                        + " references missing experiment id=" + run.getExperimentId());
            }
            runIds.add(run.getId());
        }

        for (RunResultData result : results) {
            if (!runIds.contains(result.getRunId())) {
                throw new ValidationException("RunResult id=" + result.getId()
                        + " references missing run id=" + result.getRunId());
            }
        }
    }

    private void requireNotNull(Object object, String section) {
        if (object == null) {
            throw new ValidationException("Section '" + section + "' contains null item");
        }
    }

    private long requirePositive(Long value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " is required");
        }
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
        return value;
    }

    private void requireUnique(Set<Long> ids, long id, String message) {
        if (!ids.add(id)) {
            throw new ValidationException(message + id);
        }
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(message);
        }
    }

    private void requireMaxLength(String value, int max, String message) {
        if (value != null && value.length() > max) {
            throw new ValidationException(message);
        }
    }

    private MeasurementParam parseParam(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("RunResult.param is required");
        }

        try {
            return MeasurementParam.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown MeasurementParam: " + value);
        }
    }

    private void validateValue(MeasurementParam param, double value) {
        switch (param) {
            case pH -> {
                if (value < 0 || value > 14) {
                    throw new ValidationException("pH must be between 0 and 14");
                }
            }
            case Concentration -> {
                if (value < 0) {
                    throw new ValidationException("Concentration can't be negative");
                }
            }
            case Temperature -> {
            }
        }
    }

    private void validateDates(String createdAt, String updatedAt, String label) {
        Instant created = parseInstant(createdAt, label + " has invalid createdAt");
        Instant updated = parseInstant(updatedAt, label + " has invalid updatedAt");

        if (updated.isBefore(created)) {
            throw new ValidationException(label + " has updatedAt before createdAt");
        }
    }

    private Instant parseInstant(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(message);
        }

        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            throw new ValidationException(message);
        }
    }
}
