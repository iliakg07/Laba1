package service;

import domain.MeasurementParam;
import org.junit.jupiter.api.Test;
import validation.ValidationException;
import static org.junit.jupiter.api.Assertions.fail;

public class RunResServRemoveTest {
    @Test
    void shouldRemoveRunResult() {
        RunResultService service = new RunResultService();

        var result = service.add(1, MeasurementParam.pH, 7.0, "pH", null);

        service.remove(result.getId());

        try {
            service.getById(result.getId());
            fail("Expected exception was not thrown");
        } catch (ValidationException e) {
            // ок
        }
    }

    @Test
    void shouldThrowWhenRemovingIdDoesNotExist() {
        RunResultService service = new RunResultService();

        try {
            service.remove(999);
            fail("Expected exception was not thrown");
        } catch (ValidationException e) {
            // ок
        }
    }
}
