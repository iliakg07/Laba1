package service;

import domain.MeasurementParam;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RunResServUpdateTest {
    @Test
    void shouldUpdateRunResult() {
        RunResultService service = new RunResultService();

        var result = service.add(1, MeasurementParam.pH, 7.0, "pH", null);

        var updated = service.update(
                result.getId(),
                MeasurementParam.Temperature,
                105,
                "Celsius",
                "updated"
        );

        assertEquals(MeasurementParam.Temperature, updated.getParam());
        assertEquals(105, updated.getValue());
        assertEquals("Celsius", updated.getUnit());
    }

    @Test
    void shouldThrowWhenUpdatingIdDoesNotExist() {
        RunResultService service = new RunResultService();

        try {
            service.update(999, MeasurementParam.pH, 7.0, "pH", null);
            fail("Expected exception was not thrown");
        } catch (ValidationException e) {
            // ок
        }
    }

    @Test
    void shouldNotUpdateWhenInvalidData() {
        RunResultService service = new RunResultService();

        var result = service.add(1, MeasurementParam.pH, 7.0, "pH", null);

        try {
            service.update(result.getId(), MeasurementParam.pH, -1, "", null);
            fail("Expected exception was not thrown");
        } catch (ValidationException e) {
            // ок
        }

        var same = service.getById(result.getId());

        assertEquals(7.0, same.getValue());
        assertEquals("pH", same.getUnit());
    }
}
