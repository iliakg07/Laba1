package validation;

import domain.Run;

public class RunValidator {
    public static void validate(Run run) {
        if (run.experimentId <= 0) {
            throw new ValidationException("Invalid experimentId");
        }

        if (run.name == null) {
            throw new ValidationException("Run name cannot be empty");
        }

        if (run.name.length() > 128) {
            throw new ValidationException("Run name too long");
        }

        if (run.operatorName == null) {
            throw new ValidationException("Operator name cannot be empty");
        }

        if (run.operatorName.length() > 64) {
            throw new ValidationException("Operator name too long");
        }

    }
}
