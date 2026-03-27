package validation;

import domain.RunResult;

public class RunResultValidator {
    public static void validate(RunResult result) {
        if (result.getRunId() <= 0) {
            throw new ValidationException("Invalid runId");
        }

        if (result.getValue() < 0) {
            throw new ValidationException("Value can't be less than 0");
        }

        if (result.getParam() == null) {
            throw new ValidationException("Measurement param is required");
        }

        if (result.getUnit() == null || result.getUnit().isBlank()) {
            throw new ValidationException("Unit cannot be empty");
        }

        if (result.getUnit().length() > 16) {
            throw new ValidationException("Unit too long");
        }

        if (result.getComment() != null && result.getComment().length() > 128) {
            throw new ValidationException("Comment too long");
        }
    }
}
