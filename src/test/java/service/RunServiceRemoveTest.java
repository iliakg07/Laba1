package service;

import org.junit.jupiter.api.Test;
import validation.ValidationException;
import static org.junit.jupiter.api.Assertions.*;

class RunServiceRemoveTest {

    @Test
    void shouldRemoveRun() {
        RunService service = new RunService();

        var run = service.add(1, "run1", "operator");
        service.remove(run.getId());

        assertThrows(ValidationException.class, () -> {
           service.getById(run.getId());
        });
    }

    @Test
    void shouldThrowWhenRemovingIdDoesNotExist() {

        RunService service = new RunService();

        try {
            service.remove(999);
            fail("Expected exception was not thrown");
        } catch (ValidationException e) {
            // ок
        }
    }
}
