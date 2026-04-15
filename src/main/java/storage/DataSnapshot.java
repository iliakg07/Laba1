package storage;

import java.util.ArrayList;
import java.util.List;

// Создаем 3 списка для содержимого файла JSON
public class DataSnapshot {
    public List<ExperimentRecord> experiments = new ArrayList<>();
    public List<RunRecord> runs = new ArrayList<>();
    public List<RunResultRecord> results = new ArrayList<>();

}
