package ru.igap.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.igap.backend.models.Artist;
import ru.igap.backend.models.Country;
import ru.igap.backend.models.Museum;
import ru.igap.backend.models.Painting;
import ru.igap.backend.repositories.ArtistRepository;
import ru.igap.backend.repositories.MuseumRepository;
import ru.igap.backend.repositories.PaintingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MuseumRepository museumRepository;


    @GetMapping("/paintings")
    public List getAllPaintings() {
        return paintingRepository.findAll();
    }

    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws Exception {
        try {
            Optional<Artist> aa = artistRepository.findById(painting.artist.id);
            if (aa.isPresent()) {
                painting.artist = aa.get();
            }
            Optional<Museum> mm = museumRepository.findById(painting.museum.id);
            if (mm.isPresent()) {
                painting.museum = mm.get();
            }
            Painting nc = paintingRepository.save(painting);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("painting.name_UNIQUE"))
                error = "paintingalreadyexists";
            else
                error = ex.getMessage();
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }
}
