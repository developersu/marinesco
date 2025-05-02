package ru.redrise.marinesco.library.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.library.Genre;

@Slf4j
public class GenresUpload {

    public static String upload(Resource resource, long fileSize, GenreRepository repository) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            if (fileSize == 0)
                throw new Exception("empty file");

            String line;
            List<Genre> genres = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split(":::");
                
                if (arr.length != 2)
                    throw new Exception("Malformed file");
                
                genres.add(new Genre(arr[0], arr[1]));
            }

            repository.saveAll(genres);
        } catch (Exception e) {
            log.debug(e.toString());
            return "Upload failed: " + e.getMessage();
        }

        return "Successfully uploaded: " + resource.getFilename();
    }
}
