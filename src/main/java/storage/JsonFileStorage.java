package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class JsonFileStorage {
    private final ObjectMapper mapper;

//Создаем пустой конструктор
    public JsonFileStorage(){
        mapper = new ObjectMapper();
//Добаляем модуль для работы со временем
        mapper.registerModule(new JavaTimeModule());
//Добовляем модуль для форматирования с отступами, читаемое JSON
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    // Создаем метод save, принимающий контейнер с данными и строку с путем в файл, если с чтением/записью файлоа проблемы  выкидываем ощибку, возвращает файл с JSON
    public void save(DataContainer container, String path)
        throws IOException{
        File file = new File(path);// создаем файл из указанного пути
        mapper.writeValue(file, container);//Вызываем метод который берет контейнер переводит в JSON и записывает результат в файл,
    }
//Создаем метод принимающий путь к файлу и возвращает объект контейнер, если есть проблемы выкидываем ошибку
    public DataContainer load(String parh)
        throws IOException{
        File file = new File(parh);
return mapper.readValue(file, DataContainer.class);//Jacson читает файл парсит JSON  и создает контейнер
}
}