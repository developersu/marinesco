package ru.redrise.marinesco.library.settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.library.Genre;


@Slf4j
@Controller
@RequestMapping("/settings/genres")
@PreAuthorize("hasRole('ADMIN')")
public class GenreSettingsController {

    private final GenreRepository genreRepository;

    public GenreSettingsController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public String getPage(@ModelAttribute("rescanError") String err) {
        return "genres_settings";
    }

    @ModelAttribute(name = "genresHolder")
    public GenresHolder setRegistrationSetting() {
        List<Genre> genres = new ArrayList<>();
        
        genreRepository.findAll().iterator()
                .forEachRemaining(genres::add);
        
        return new GenresHolder(genres);
    }

    @PostMapping
    public String getGenresUpdated(@ModelAttribute GenresHolder genreHolder) {
        genreHolder.getGenres().forEach(genreRepository::save);
        return "genres_settings";
    }

    @Data
    @AllArgsConstructor
    private class GenresHolder {
        private List<Genre> genres;
    }

    @PostMapping("/upload")
    public String postUpload(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        var message = GenresUpload.upload(file, genreRepository);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/settings/genres";
    }
}
