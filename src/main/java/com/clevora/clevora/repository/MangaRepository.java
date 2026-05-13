package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Integer> {

}
