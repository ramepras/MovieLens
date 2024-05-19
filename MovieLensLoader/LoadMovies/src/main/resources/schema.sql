-- Drop the Rating table if it exists
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS Tag;

-- Create the Movie table
CREATE TABLE IF NOT EXISTS Movie (
    movie_id BIGINT PRIMARY KEY,
    movie_title VARCHAR(255),
    movie_year BIGINT,
    movie_genres VARCHAR(255)
);

-- Create the Rating table with a separate unique identifier
CREATE TABLE IF NOT EXISTS Rating (
    rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    movie_id BIGINT,
    rating DOUBLE,
    timestamp TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
);

-- Create the Tag table with a separate unique identifier
CREATE TABLE IF NOT EXISTS Tag (
    tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    movie_id BIGINT,
    tag VARCHAR(255),
    timestamp TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
);
