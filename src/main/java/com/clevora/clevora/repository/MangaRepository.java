package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Manga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MangaRepository extends JpaRepository<Manga, Integer> {

    // Query lấy danh sách manga đã duyệt, hỗ trợ lọc theo status linh hoạt
    @Query("SELECT m FROM Manga m " +
            "WHERE m.approvalStatus = :approvalStatus " +
            "AND (:status IS NULL OR m.status = :status)")
    List<Manga> findApprovedMangas(
            @Param("approvalStatus") Manga.ApprovalStatus approvalStatus,
            @Param("status") Manga.MangaStatus status,
            Pageable pageable
    );

    @Query("SELECT m FROM Manga m LEFT JOIN FETCH m.genres " +
            "WHERE m.slug = :slug AND m.approvalStatus = 'APPROVED'")
    Optional<Manga> findApprovedMangaBySlugWithGenres(@Param("slug") String slug);

    @Query("SELECT m FROM Manga m " +
            "WHERE m.approvalStatus = 'APPROVED' " +
            "AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.authorName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Manga> searchApprovedMangas(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Manga m JOIN m.genres g " +
            "WHERE m.approvalStatus = 'APPROVED' AND g.slug = :genreSlug")
    Page<Manga> findApprovedMangasByGenreSlug(@Param("genreSlug") String genreSlug, Pageable pageable);

    // danh sach manga cua người dùng
    @Query("SELECT m FROM Manga m " +
            "WHERE m.submittedBy.email = :email " +
            "AND (:approvalStatus IS NULL OR m.approvalStatus = :approvalStatus)")
    Page<Manga> findMyMangas(
            @Param("email") String email,
            @Param("approvalStatus") Manga.ApprovalStatus approvalStatus,
            Pageable pageable
    );

    Page<Manga> findByApprovalStatus(Manga.ApprovalStatus approvalStatus, Pageable pageable);

    long countByApprovalStatus(Manga.ApprovalStatus approvalStatus);
}
