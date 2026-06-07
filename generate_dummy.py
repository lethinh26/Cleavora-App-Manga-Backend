import random

num_mangas = 50
num_chapters_per_manga = 10
num_images_per_chapter = 5

sql = """
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
"""

authors = ["Tite Kubo", "Akira Toriyama", "Naoko Takeuchi", "Kentaro Miura", "Hiro Mashima", "Sui Ishida", "ONE", "Yusuke Murata", "Takeshi Obata", "Tsugumi Ohba"]
adjectives = ["Dark", "Light", "Red", "Blue", "Eternal", "Fading", "Hidden", "Lost", "Final", "First", "Zero", "Infinite", "Silent", "Shattered"]
nouns = ["Moon", "Sun", "Blade", "Soul", "Heart", "Dragon", "Star", "Knight", "Mage", "World", "Kingdom", "God", "Demon", "Spirit"]

manga_inserts = []
manga_genres_inserts = []
chapter_inserts = []
chapter_page_inserts = []

start_manga_id = 10
start_chapter_id = 10

current_manga_id = start_manga_id
current_chapter_id = start_chapter_id

for i in range(num_mangas):
    title = f"{random.choice(adjectives)} {random.choice(nouns)} {i}"
    slug = title.lower().replace(" ", "-")
    description = f"This is the epic tale of {title}. A story filled with adventure, action, and mystery."
    author = random.choice(authors)
    status = random.choice(['ONGOING', 'COMPLETED', 'HIATUS'])
    approval_status = random.choice(['PENDING', 'APPROVED', 'REJECTED'])
    
    view_count = random.randint(100, 100000)
    like_count = random.randint(10, 10000)
    follow_count = random.randint(5, 5000)
    
    manga_inserts.append(f"({current_manga_id}, '{title}', '{slug}', '{description}', 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', '{author}', '{author}', '{status}', '{approval_status}', {view_count}, {like_count}, {follow_count})")
    
    # Assign 2-4 random genres (genres exist from 1 to 7)
    genres = random.sample(range(1, 8), random.randint(2, 4))
    for g in genres:
        manga_genres_inserts.append(f"({current_manga_id}, {g})")
        
    for j in range(1, num_chapters_per_manga + 1):
        chapter_title = f"Chapter {j}: The Beginning of {random.choice(nouns)}"
        chapter_inserts.append(f"({current_chapter_id}, {current_manga_id}, {j}, '{chapter_title}', {num_images_per_chapter}, {random.randint(10, 1000)}, NOW())")
        
        for p in range(1, num_images_per_chapter + 1):
            chapter_page_inserts.append(f"({current_chapter_id}, 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg', {p})")
            
        current_chapter_id += 1
        
    current_manga_id += 1


sql += "INSERT INTO mangas (id, title, slug, description, cover_image_url, author_name, artist_name, status, approval_status, view_count, like_count, follow_count) VALUES \n"
sql += ",\n".join(manga_inserts) + ";\n\n"

sql += "INSERT INTO manga_genres (manga_id, genre_id) VALUES \n"
sql += ",\n".join(manga_genres_inserts) + ";\n\n"

sql += "INSERT INTO chapters (id, manga_id, chapter_number, title, page_count, view_count, updated_at) VALUES \n"
sql += ",\n".join(chapter_inserts) + ";\n\n"

sql += "INSERT INTO chapter_pages (chapter_id, image_url, page_number) VALUES \n"
sql += ",\n".join(chapter_page_inserts) + ";\n\n"

sql += "SET FOREIGN_KEY_CHECKS = 1;\n"

with open("more_dummy_data.sql", "w") as f:
    f.write(sql)

print("Generated more_dummy_data.sql")
