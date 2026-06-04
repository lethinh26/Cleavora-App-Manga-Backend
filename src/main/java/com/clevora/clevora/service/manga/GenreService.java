package com.clevora.clevora.service.manga;

import com.clevora.clevora.dto.manga.GenreRequest;
import com.clevora.clevora.entity.Genre;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ForbiddenException;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.GenreRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.engine.internal.UnsavedValueFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
    private GenreRepository genreRepository;
    private UserRepository userRepository;
    // crud (genre = genreResponse)
    public Genre createGenre(String email, GenreRequest genre){
        // check role voi email
        validateAdminRole(email);
        Genre genreCreate = new Genre();
        genreCreate.setName(genre.getName());
        genreCreate.setSlug(genre.getSlug());

        return genreRepository.save(genreCreate);
    }

    public Genre updateGenre(String email, int id, GenreRequest genreRequest) {
        validateAdminRole(email);

        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID"));

        // check ton tai (loại trừ chính nó)
        if(!existingGenre.getName().equals(genreRequest.getName()) && genreRepository.existsGenreByName(genreRequest.getName())){
            throw new ResourceNotFoundException("Không cho phép trùng lặp tên thể loại");
        }
        if(!existingGenre.getSlug().equals(genreRequest.getSlug()) && genreRepository.existsGenreBySlug(genreRequest.getSlug())){
            throw new ResourceNotFoundException("Không cho phép trùng lặp tên Slug");
        }

        existingGenre.setSlug(genreRequest.getSlug());
        existingGenre.setName(genreRequest.getName());

        return genreRepository.save(existingGenre);
    }

    public void deleteGenre(String email, int id) {
        validateAdminRole(email);

//        if (!genreRepository.existsByName((id))) {
//            throw new ResourceNotFoundException("Không tìm thấy thể loại để xóa");
//        }

        genreRepository.deleteById(id);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }


    private void validateAdminRole(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.SUPERADMIN) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
    }
}
