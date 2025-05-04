package ru.redrise.marinesco.library.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.library.Genre;

@Slf4j
public class GenresUpload {

    public static String upload(MultipartFile file, GenreRepository repository) {
        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){

            String line;
            var genres = new ArrayList<Genre>();
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

        return "Successfully uploaded: " + file.getResource().getFilename();
    }
}
