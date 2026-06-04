package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    Optional<Genre> findById(Integer id);

    boolean existsGenreByName(String name);

    boolean existsGenreBySlug(String slug);

    void deleteById(Integer id);
}
