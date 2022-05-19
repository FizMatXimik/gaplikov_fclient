package ru.igap.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.igap.backend.models.Museum;
import ru.igap.backend.models.Painting;
import ru.igap.backend.repositories.MuseumRepository;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MuseumController {

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/museums")
    public List getAllMuseums() {
        return museumRepository.findAll();
    }

    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getArtistPaintings(@PathVariable(value="id") Long museumId) {
        Optional<Museum> cc = museumRepository.findById(museumId);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) throws Exception {
        try {
            Museum nc = museumRepository.save(museum);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                error = "museumalreadyexists";
            else
                error = ex.getMessage();
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }


}
