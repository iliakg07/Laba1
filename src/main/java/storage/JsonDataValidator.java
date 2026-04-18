package storage;

import domain.Experiment;
import domain.Run;
import domain.RunResult;
import validation.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//Проверяем что списки не null
public class JsonDataValidator {
    public void validate(DataContainer container)
throws ValidationException{
        List<Experiment> experiments = container.getExperiments();
        List<Run> runs = container.getRuns();
        List<RunResult> results = container.getRunResults();

        if (experiments == null){
            throw new ValidationException("File is missing 'experiments' field");
        }
        if (runs == null){
            throw new ValidationException("File is missing 'runs' field");
        }
        if (results == null){
            throw new ValidationException("File is missing 'runResults' field");
        }
        //Проверка уникальности каждого ID внутри коллекций
        checkUniqueIds(experiments, "Experiment");
        checkUniqueIds(runs, "Run");
        checkUniqueIds(results, "RunResult");
        //Проверка каждого объекта на валидность полей
        validateExperimentsFields(experiments);
        validateRunsFields(runs);
        validateRunResultsFields(results);
//Проверка на целостность связей
        Set<Long> experimentIds = collectIds(experiments);
        Set<Long> runIds = collectIds(runs);
//Проверяем, что каждый прогон ссылается на существующий эксперемент, если нет то ошибка
        for (Run run : runs){
            if (!experimentIds.contains(run.getExperimentId())){
                throw new ValidationException("Run id=" + run.getId() + " references es non-existent experiment id=" + run.getExperimentId());
            }
        }
        //Проверяем, что каждый результат прогона ссылается на существующий прогон, если нет то ошибка
        for (RunResult result : results){
            if (!runIds.contains(result.getRunId())){
                throw new ValidationException("RunResult id=" + result.getRunId() + " references non-existent run id=" + result.getRunId());
            }
        }
    }
    //Проверка уникальности ID. Принимаем список любого типа и строку с названием сущности, создаем пустое множество для хранения уже втречаемых ID, проходим по каждому объекту, извлекая ID, если есть дуюликат выбрасываем ошибку, если новый добавляем
    private void checkUniqueIds(List<?> objects, String entityName){
        Set<Long> ids = new HashSet<>();
        for (Object obj : objects){
            long id = extractId(obj);
            if (ids.contains(id)){
                throw new ValidationException("Duplicate " + entityName + " id: " + id);
            }
            ids.add(id);
        }
    }
//С помощью оператора instanceof проверяем, что объект приводит к соответствующему типу и вызывает гетер, если тип неизвестен ошибка
    private long extractId(Object obj){
        if (obj instanceof Experiment){
            return ((Experiment) obj).getId();
        } else if (obj instanceof Run){
            return ((Run) obj).getId();
        }else if (obj instanceof RunResult){
            return ((RunResult) obj).getId();
        }
        throw new ValidationException("Unknown object type: " + obj.getClass());
    }
    //Создаем множество и добавляем а него ID объектов из списка, извлекаем ID  и возвращаем полученное множество
    private Set<Long> collectIds(List<?> objects){
        Set<Long> ids = new HashSet<>();
        for (Object obj : objects){
            ids.add(extractId(obj));
        }
        return ids;
    }
    //Прописываем такие же валидации как в конструкторе Experiment
    private void validateExperimentsFields(List<Experiment> experiments){
        for (Experiment exp : experiments){
            if (exp.getId()<= 0){
                throw new ValidationException("Experiment id must be positive, got: " + exp.getId());
            }
            if (exp.getName() == null || exp.getName().isBlank()) {
                throw new ValidationException("Experiment name cannot be empty, id=" + exp.getId());
            }
            if (exp.getName().length() > 128){
                throw new ValidationException("Experiment name too long, id=" + exp.getId());
            }
            if (exp.getDescription() != null && exp.getDescription().length() > 512){
                throw new ValidationException("Experiment description too long, id=" + exp.getId());
            }
            if (exp.getOwnerUsername() == null || exp.getOwnerUsername().isBlank()){
                throw new ValidationException("Experiment ownerUsername cannot be empty, id=" + exp.getId());
            }
            if (exp.getOwnerUsername().length() > 128){
                throw new ValidationException("Experiment ownerUsername too long, id=" + exp.getId());
            }
            // Дата создания и изменения могут быть null, если Jackson не смог их перевести в JSON
            if (exp.getCreatedAt() == null){
                throw new ValidationException("Experiment createdAt is missing, id=" + exp.getId());
            }
            if (exp.getUpdatedAt() == null){
                throw new ValidationException("Experiment updatedAt is missing, id=" + exp.getId());
            }
        }
    }
    //Прописываем такие же валидации как в конструкторе Run
    private void validateRunsFields(List<Run> runs){
        for (Run run: runs){
            if (run.getId() <= 0){
                throw new ValidationException("Run id must be positive, got: " + run.getId());
            }
            if (run.getExperimentId() <= 0){
                throw new ValidationException("Run experimentId must be positive, run id=" + run.getId());
            }
            if (run.getName().length() > 128){
                throw new ValidationException("Run name too long, id=" + run.getId());
            }
            if (run.getName() == null || run.getName().isBlank()){
                throw new ValidationException("Run name cannot be empty, id=" + run.getId());
            }
            if (run.getOperatorName().length() > 64){
                throw new ValidationException("Run operatorName too long, id=" + run.getId());
            }
            // Дата создания и изменения могут быть null, если Jackson не смог их перевести в JSON
            if (run.getCreatedAt() == null){
                throw new ValidationException("Run createdAt is missing, id=" + run.getId());
            }
            if (run.getUpdatedAt() == null){
                throw new ValidationException("Run updatedAt is missing, id=" + run.getId());
            }
        }
    }
    //Прописываем такие же валидации как в конструкторе RunResult
    private void validateRunResultsFields(List<RunResult> results){
        for (RunResult result : results){
            if (result.getId() <= 0){
                throw new ValidationException("RunResult id must be positive, got:" + result.getId());
            }
            if (result.getRunId() <= 0){
                throw new ValidationException("RunResult runId must be positive, id=" + result.getId());
            }
            if (result.getParam() == null){
                throw new ValidationException("RunResult param cannot be null, id=" + result.getId());
            }
            // Проверка значения в зависимости от параметра
            switch (result.getParam()){
                case pH:
                if (result.getValue() < 0 || result.getValue() > 14 ){
                    throw new ValidationException("pH must be between 0 and 14, id=" + result.getId());
                }
                break;
                case Temperature:
                    //Без ограничений
                    break;
                case Concentration:
                    if (result.getValue() < 0){
                        throw new ValidationException("Concentration cannot be negative, id=" + result.getId());
                    }
                    break;
            }
            if (result.getUnit().length() > 16 ){
                throw new ValidationException("RunResult unit too long, id=" + result.getId());
            }
            if (result.getComment() != null && result.getComment().length() > 128){
                throw new ValidationException("RunResult comment too long, id=" + result.getId());
            }
            // Дата создания может быть null, если Jackson не смог его перевести в JSON
            if (result.getCreatedAt() == null){
                throw new ValidationException("RunResult createdAt is missing, id=" + result.getId());
            }
        }
    }
}


