package domain;
import java.time.Instant;

public final class RunResult {
    // Уникальный номер результата. Программа назначает сама.
    private long id;
    // К какому запуску относится (id запуска).
    // Должен ссылаться на реально существующий Run.
    public long runId;
    // Что измеряли (PH/CONDUCTIVITY/NITRATE...). Выбирается из списка MeasurementParam.
    public MeasurementParam param;
    // Числовое значение результата.
    private double value;
    // Единицы (например "mg/L"). Нельзя пустое. До 16 символов.
    public String unit;
    // Комментарий (например “after 60 min”). Можно пусто. До 128 символов.
    public String comment;
    // Когда добавили результат. Программа ставит автоматически.
    private Instant createdAt;
}
