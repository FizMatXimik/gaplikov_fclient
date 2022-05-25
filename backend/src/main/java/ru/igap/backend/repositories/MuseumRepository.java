package ru.igap.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.igap.backend.models.Country;
import ru.igap.backend.models.Museum;

import java.util.Optional;

@Repository
public interface MuseumRepository extends JpaRepository<Museum, Long> {

    Optional<Museum> findByName(String name);
}
