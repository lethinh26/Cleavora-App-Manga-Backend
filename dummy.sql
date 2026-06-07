-- Set charset
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE chapter_pages;
TRUNCATE TABLE chapters;
TRUNCATE TABLE manga_genres;
TRUNCATE TABLE mangas;
TRUNCATE TABLE genres;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- password_hash is for '123456'
INSERT INTO users (email, password_hash, display_name, role, is_active) VALUES 
('admin@gmail.com', '$2a$10$yFwA3L0jV/lH22E/7O3lTOd3E80n5vXN9z1H1x9J2yN.32C3A.0K6', 'Admin', 'SUPERADMIN', 1),
('user1@gmail.com', '$2a$10$yFwA3L0jV/lH22E/7O3lTOd3E80n5vXN9z1H1x9J2yN.32C3A.0K6', 'Trí Nguyễn', 'USER', 1),
('user2@gmail.com', '$2a$10$yFwA3L0jV/lH22E/7O3lTOd3E80n5vXN9z1H1x9J2yN.32C3A.0K6', 'Test User', 'USER', 1);

INSERT INTO genres (id, name, slug) VALUES 
(1, 'Action', 'action'),
(2, 'Romance', 'romance'),
(3, 'Comedy', 'comedy'),
(4, 'Adventure', 'adventure'),
(5, 'Fantasy', 'fantasy'),
(6, 'Drama', 'drama'),
(7, 'Horror', 'horror');

INSERT INTO mangas (id, title, slug, description, cover_image_url, author_name, artist_name, status, approval_status, view_count, like_count, follow_count) VALUES 
(1, 'Naruto', 'naruto', 'A young ninja who seeks recognition from his peers and dreams of becoming the Hokage.', 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 'Masashi Kishimoto', 'Masashi Kishimoto', 'COMPLETED', 'APPROVED', 15000, 300, 100),
(2, 'One Piece', 'one-piece', 'Follows the adventures of Monkey D. Luffy and his pirate crew in order to find the greatest treasure ever left by the legendary Pirate, Gold Roger.', 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 'Eiichiro Oda', 'Eiichiro Oda', 'ONGOING', 'APPROVED', 25000, 500, 200),
(3, 'Attack on Titan', 'attack-on-titan', 'After his hometown is destroyed and his mother is killed, young Eren Jaeger vows to cleanse the earth of the giant humanoid Titans that have brought humanity to the brink of extinction.', 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 'Hajime Isayama', 'Hajime Isayama', 'COMPLETED', 'APPROVED', 18000, 450, 150),
(4, 'Jujutsu Kaisen', 'jujutsu-kaisen', 'A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself. He enters a shaman''s school to be able to locate the demon''s other body parts and thus exorcise himself.', 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 'Gege Akutami', 'Gege Akutami', 'ONGOING', 'APPROVED', 12000, 320, 180);

INSERT INTO manga_genres (manga_id, genre_id) VALUES 
(1, 1), (1, 3), (1, 4),
(2, 1), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 6), (3, 7),
(4, 1), (4, 5), (4, 6);

INSERT INTO chapters (id, manga_id, chapter_number, title, page_count, view_count, updated_at) VALUES 
(1, 1, 1, 'Uzumaki Naruto', 3, 500, NOW()),
(2, 1, 2, 'Konohamaru', 3, 400, NOW()),
(3, 2, 1, 'Romance Dawn', 3, 800, NOW()),
(4, 3, 1, 'To You, in 2000 Years: The Fall of Shiganshina, Part 1', 3, 600, NOW()),
(5, 4, 1, 'Ryomen Sukuna', 3, 700, NOW());

INSERT INTO chapter_pages (chapter_id, image_url, page_number) VALUES 
(1, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 1),
(1, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 2),
(1, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 3),

(2, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 1),
(2, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 2),
(2, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 3),

(3, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 1),
(3, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 2),
(3, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 3),

(4, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 1),
(4, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 2),
(4, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 3),

(5, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 1),
(5, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 2),
(5, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', 3);
