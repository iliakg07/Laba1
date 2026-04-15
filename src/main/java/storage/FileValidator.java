package storage;

import validation.ValidationException;

import java.util.HashSet;
import java.util.Set;

public class FileValidator {

   //Проверка DataSnapshot на то, что не пустой и не сломанный
    public void validate(DataSnapshot snapshot) {
        if (snapshot == null) {
            throw new ValidationException("Download error: The file is empty or corrupted.");
        }    // Проверка, что все 3 списка существуют
        if (snapshot.experiments == null || snapshot.runs == null || snapshot.results == null) {
            throw new ValidationException("Load error: One of the sections experiments/runs/results is missing");
        }
        //Проверяем, что ID не повторяются
        Set<Long> experimentIds = new HashSet<>();
        for (ExperimentRecord experiment : snapshot.experiments) {
            if (experiment == null) { //Проверям что не null
                throw new ValidationException("Load error: empty Experiment found");
            }
            if (experiment.id <= 0) { //Проверяем что больше 0
                throw new ValidationException("Load error: Experiment id must be positive");
            }
            if (!experimentIds.add(experiment.id)){ //Проверям что не повторяется
                throw new ValidationException("Load error: repeating Experiment id=" + experiment.id);
            }
        }
    }
}
