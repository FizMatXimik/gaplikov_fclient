package ru.igap.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.igap.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
