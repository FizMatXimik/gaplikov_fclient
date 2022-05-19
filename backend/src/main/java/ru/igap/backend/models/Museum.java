package ru.igap.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import ru.igap.backend.models.User;
import ru.igap.backend.tools.View;

@Entity
@Table(name = "museums")
@Access(AccessType.FIELD)
public class Museum {

    public Museum() {}

    public Museum(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.REST.class)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @JsonView(View.REST.class)
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @JsonView(View.REST.class)
    @Column(name = "location", nullable = false)
    public String location;

    @JsonIgnore
    @OneToMany(mappedBy = "museum")
    public List<Painting> paintings = new ArrayList<Painting>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "usersmuseums", joinColumns = @JoinColumn(name = "museums"), inverseJoinColumns = @JoinColumn(name = "users"))
    public Set<User> users = new HashSet<>();
}
