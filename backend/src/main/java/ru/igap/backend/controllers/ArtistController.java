package ru.igap.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.igap.backend.models.Artist;
import ru.igap.backend.models.Country;
import ru.igap.backend.models.Painting;
import ru.igap.backend.repositories.ArtistRepository;
import ru.igap.backend.repositories.CountryRepository;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ArtistController {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/artists")
    public List getAllArtists() {
        return artistRepository.findAll();
    }

    @GetMapping("/artists/{id}/paintings")
    public ResponseEntity<List<Painting>> getArtistPaintings(@PathVariable(value="id") Long artistId) {
        Optional<Artist> cc = artistRepository.findById(artistId);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws Exception {
        try {
            Optional<Country> cc = countryRepository.findById(artist.country.id);
            if (cc.isPresent()) {
                artist.country = cc.get();
            }
            Artist nc = artistRepository.save(artist);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("artists.name_UNIQUE"))
                error = "artistalreadyexists";
            else
                error = ex.getMessage();
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }
}
