package ru.igap.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.igap.backend.models.Museum;

@Repository
public interface MuseumRepository extends JpaRepository<Museum, Long> {
}
