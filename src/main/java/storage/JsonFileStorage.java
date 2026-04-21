package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ===== 3 ЭТАП: JSON =====
// Этот класс только читает и пишет JSON-файл.
public class JsonFileStorage {
    private final ObjectMapper mapper;

    public JsonFileStorage() {
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Path path, DataSnapshot snapshot) throws IOException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        mapper.writeValue(path.toFile(), snapshot);
    }

    public DataSnapshot load(Path path) throws IOException {
        return mapper.readValue(path.toFile(), DataSnapshot.class);
    }
}
