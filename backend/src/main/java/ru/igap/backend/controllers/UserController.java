package ru.igap.backend.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.igap.backend.models.Country;
import ru.igap.backend.models.Museum;
import ru.igap.backend.models.User;
import ru.igap.backend.repositories.MuseumRepository;
import ru.igap.backend.repositories.UserRepository;
import ru.igap.backend.tools.DataValidationException;
import ru.igap.backend.tools.Utils;
import ru.igap.backend.tools.View;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") Long userId)
            throws DataValidationException {
        User user = userRepository.findById(userId).orElseThrow(()->new DataValidationException("Пользователь с таким индексом не найден"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user) throws DataValidationException {
        try {
            User nc = userRepository.save(user);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            if (ex.getMessage().contains("users.name_UNIQUE"))
                throw new DataValidationException("Этот пользователь уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails) throws DataValidationException{
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь с таким индексом не найден"));
            user.login = userDetails.login;
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            if (ex.getMessage().contains("users.name_UNIQUE"))
                throw new DataValidationException("Этот пользователь уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }

    }

    @PostMapping("/deleteusers")
    public ResponseEntity<Object> deleteUsers(@Valid @RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
//        Optional<User> user = userRepository.findById(userId);
//        Map<String, Boolean> resp = new HashMap<>();
//        if (user.isPresent()) {
//            userRepository.delete(user.get());
//            resp.put("deleted", Boolean.TRUE);
//        }
//        else
//            resp.put("deleted", Boolean.FALSE);
//        return ResponseEntity.ok(resp);
//    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId, @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum>
                        mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId, @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : u.museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }
}
