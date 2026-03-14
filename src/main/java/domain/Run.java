package domain;

import java.time.Instant;

public final class Run {
    // Уникальный номер запуска. Программа назначает сама.
    private long id;
    // К какому эксперименту относится (id эксперимента).
    // Должен ссылаться на реально существующий Experiment.
    public long experimentId;
    // Название запуска reminder: “Run-2026-02-03-A”. Нельзя пустое. До 128 символов.
    public String name;
    // Кто выполнял запуск (логин или имя). Нельзя пустое. До 64 символов.
    public String operatorName;
    // Когда запуск зарегистрирован. Программа ставит автоматически.
    private Instant createdAt;
}

