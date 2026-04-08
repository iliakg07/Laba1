package service;

import domain.Experiment;
import domain.MeasurementParam;
import domain.Run;
import domain.RunResult;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class RunResultTest {

    @Test
//    Проверяем что объект класса RunResult создаётся корректно
    void shouldCreateRunResultWithValidData() {
        var exp = new Experiment("exp_name", "exp_desc", "exp_owner");
        var run = new Run(exp.getId(), "run_name", "run_operator");
        var rr = new RunResult(run.getId(), MeasurementParam.pH, 7.0, "pH", "comment");

        assertEquals(run.getId(), rr.getRunId());
        assertEquals(MeasurementParam.pH, rr.getParam());
        assertEquals(7.0, rr.getValue());
        assertEquals("pH", rr.getUnit());
        assertEquals("comment", rr.getComment());
    }

    @Test
//    Проверяем корректность получаемого конструктором runId
    void shouldThrowWhenRunIdIsNegative() {
        assertThrows(ValidationException.class, () -> {
           new RunResult(-1, MeasurementParam.pH, 1, "pH", "");
        });
    }

    @Test
//    Проверяем валидацию единиц измерения
    void shouldThrowWhenUnitIsNull() {
        assertThrows(ValidationException.class, () -> {
            new RunResult(1, MeasurementParam.pH, 1, "", "");
        });
    }

    @Test
//    Проверяем валидацию длинного комментария
    void shouldThrowWhenCommentTooLong() {
        assertThrows(ValidationException.class, () -> {
           new RunResult(1, MeasurementParam.pH, 1, "pH", "a".repeat(129));
        });
    }

    @Test
//    Проверяем валидацию установки единиц измерения через сеттер
    void shouldThrowWhenSetUnitIsEmpty() {
        var rr = new RunResult(1, MeasurementParam.pH, 1, "pH", "comm");
        assertThrows(ValidationException.class, () ->
                rr.setUnit(""));
    }

    @Test
//    Проверяем валидацию установки длинного комментария через сеттер
    void shouldThrowWhenSetCommentTooLong() {
        var rr = new RunResult(1, MeasurementParam.pH, 1, "pH", "");
        assertThrows(ValidationException.class, () ->
                rr.setComment("a".repeat(129)));
    }
}
