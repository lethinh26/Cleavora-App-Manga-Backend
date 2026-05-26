package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Integer> {

    Optional<UserFavorite> findByUserIdAndMangaId(Integer userId, Integer mangaId);

    boolean existsByUserIdAndMangaId(Integer userId, Integer mangaId);

    @Query("SELECT uf.manga FROM UserFavorite uf WHERE uf.user.id = :userId AND uf.manga.approvalStatus = 'APPROVED' ORDER BY uf.createdAt DESC")
    Page<Manga> findApprovedMangasByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserFavorite uf WHERE uf.user.id = :userId AND uf.manga.id = :mangaId")
    void deleteByUserIdAndMangaId(@Param("userId") Integer userId, @Param("mangaId") Integer mangaId);
}
