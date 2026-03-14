package validation;

import domain.RunResult;

public class RunResultValidator {
    public static void validate(RunResult result) {
        if (result.runId <= 0) {
            throw new ValidationException("Invalid runId");
        }

        if (result.param == null) {
            throw new ValidationException("Measurement param is required");
        }

        if (result.unit == null) {
            throw new ValidationException("Unit cannot be empty");
        }

        if (result.unit.length() > 16) {
            throw new ValidationException("Unit too long");
        }

        if (result.comment != null && result.comment.length() > 128) {
            throw new ValidationException("Comment too long");
        }

    }
}
