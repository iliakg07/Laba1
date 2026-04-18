package storage;

import domain.Experiment;
import domain.Run;
import domain.RunResult;

import java.util.List;

//Создаем листы с нашими данными
public class DataContainer {
    private List<Experiment> experiments;
    private List<Run> runs;
    private List<RunResult>runResults;

    //Пустой конструктор для Jackson, чтобы он сначала создал пустой объект, а потом через сетеры заполнил данными
    public DataContainer(){
    }
    //Прописываем гетеры возращающие листы с нашими данными и сетеры принимающие листы с нашими данными
    public List<Experiment> getExperiments(){
        return experiments;
    }
    public void setExperiments(List<Experiment> experiments){
        this.experiments = experiments;
    }
    public List<Run> getRuns(){
        return runs;
    }
    public void setRuns(List<Run> runs){
        this.runs = runs;
    }
    public List<RunResult> getRunResults(){
        return runResults;
    }
    public void setRunResults(List<RunResult> runResults){
        this.runResults = runResults;
    }
}
