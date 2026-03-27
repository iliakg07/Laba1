package service;

import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ExpServiceRemoveTest {
//  Проверяем, удаляет ли существующий эксперимент
    @Test
    void shouldRemoveExperiment() {
        ExperimentService service = new ExperimentService();

        var exp = service.add("exp", "desc", "user");
        service.remove(exp.getId());

//  Проверяем, что данные реально удаляются. Через лямбда функции
        assertThrows(ValidationException.class, () -> {
            service.getById(exp.getId());
        });
    }

    @Test
    void shouldThrowWhenRemovingIdDoesNotExist() {
        ExperimentService service = new ExperimentService();

//  Создали пустую коллекцию, проверяем, что при удалении несуществующего элемента (999) будет ошибка
//  Через try-catch
        try {
            service.remove(999);
            fail("Expected exception was not thrown"); // если с 999 ошибки не произошло, валим тест "вручную"
        } catch (ValidationException e) {
            // всё ок
        }
        // fail выполняется только если до него не выбросило ошибку
    }
}
