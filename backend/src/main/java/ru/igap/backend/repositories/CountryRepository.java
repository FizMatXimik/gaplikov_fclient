package ru.igap.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.igap.backend.models.Artist;
import ru.igap.backend.models.Country;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long>
{
    Optional<Country> findByName(String name);
}

