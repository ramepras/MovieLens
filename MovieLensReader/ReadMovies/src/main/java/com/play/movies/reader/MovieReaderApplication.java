package com.play.movies.reader;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@SpringBootApplication
public class MovieReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieReaderApplication.class, args);
    }
}

@Component
class MovieReaderRunner implements CommandLineRunner {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Movie> movies = movieRepository.findAll();
        movies.forEach(movie -> System.out.println("Movie: " + movie.getMovieTitle()));
        System.out.println(String.format("Movies count: %s", movies.size()));
    }
}

@Repository
interface MovieRepository extends JpaRepository<Movie, Integer> {
}

@Entity
@Table(name = "Movie")
class Movie {
    @Id
    @Column(name = "movie_id")
    private Long movieId;
    @Column(name = "movie_title")
    private String movieTitle;
    @Column(name = "movie_year")
    private Long movieYear;
    @Column(name = "movie_genres")
    private String movieGenres;

    public Movie() {
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Long getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(Long movieYear) {
        this.movieYear = movieYear;
    }

    public String getMovieGenres() {
        return movieGenres;
    }

    public void setMovieGenres(String movieGenres) {
        this.movieGenres = movieGenres;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", movieTitle='" + movieTitle + '\'' +
                ", movieYear=" + movieYear +
                ", movieGenres='" + movieGenres + '\'' +
                '}';
    }
}
