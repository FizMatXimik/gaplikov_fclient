package ru.igap.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import ru.igap.backend.tools.View;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User {
    public User() {}

    public User(Long id) { this.id = id; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.REST.class)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @JsonView(View.REST.class)
    @Column(name = "login", unique = true, nullable = false)
    public String login;

    @JsonIgnore
    @Column(name = "password")
    public String password;

    @JsonView(View.REST.class)
    @Column(name = "email", unique = true, nullable = false)
    public String email;

    @JsonIgnore
    @Column(name = "salt")
    public String salt;

    @JsonView(View.LOGIN.class)
    @Column(name = "token")
    public String token;

    @JsonView(View.REST.class)
    @Column(name = "activity")
    public LocalDateTime activity;

    @JsonView(View.REST.class)
    @ManyToMany(mappedBy = "users")
    public Set<Museum> museums = new HashSet<>();

    public void addMuseum(Museum m) {
        this.museums.add(m);
        m.users.add(this);
    }
    public void removeMuseum(Museum m) {
        this.museums.remove(m);
        m.users.remove(this);
    }
}
