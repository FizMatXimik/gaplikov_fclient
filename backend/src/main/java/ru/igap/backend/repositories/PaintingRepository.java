package ru.igap.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.igap.backend.models.Painting;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

}
