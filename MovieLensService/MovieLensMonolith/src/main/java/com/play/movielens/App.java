package com.play.movielens;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.Timestamp;
import java.util.List;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@Configuration
class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Change allowedOrigins to allowedOriginPatterns
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

@Repository
interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie getByMovieId(@Param("movieId") Long movieId);

    List<Movie> getByMovieTitle(@Param("movieTitle") String movieTitle);

    List<Movie> getByMovieYear(@Param("movieYear") Long movieYear);

    List<Movie> getByMovieGenres(@Param("movieGenres") String movieGenres);
}

@Repository
interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> getByUserId(@Param("userId") Long userId);

    List<Rating> getByMovieId(@Param("movieId") Long movieId);

    List<Rating> getByRating(@Param("rating") Double rating);

    Rating getByRatingId(@Param("ratingId") Long ratingId);
}

@Repository
interface TagRepository extends JpaRepository<Tag, Long> {
    Tag getByTagId(@Param("tagId") Long tagId);

    List<Tag> getByUserId(@Param("userId") Long userId);

    List<Tag> getByMovieId(@Param("movieId") Long movieId);

    List<Tag> getByTag(@Param("tag") String tag);
}

@Service
class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getMovies() {
        return this.movieRepository.findAll();
    }

    public Movie getByMovieId(Long movieId) {
        return this.movieRepository.getByMovieId(movieId);
    }

    public List<Movie> getByMovieTitle(String movieTitle) {
        return this.movieRepository.getByMovieTitle(movieTitle);
    }

    public List<Movie> getByMovieYear(Long movieYear) {
        return this.movieRepository.getByMovieYear(movieYear);
    }

    public List<Movie> getByMovieGenres(String movieGenres) {
        return this.movieRepository.getByMovieGenres(movieGenres);
    }

}

@Service
class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getRatings() {
        return this.ratingRepository.findAll();
    }

    public Rating getByRatingId(Long ratingId) {
        return this.ratingRepository.getByRatingId(ratingId);
    }

    public List<Rating> getByUserId(Long userId) {
        return this.ratingRepository.getByUserId(userId);
    }

    public List<Rating> getByMovieId(Long movieId) {
        return this.ratingRepository.getByMovieId(movieId);
    }

    public List<Rating> getByRating(Double rating) {
        return this.ratingRepository.getByRating(rating);
    }

}

@Service
class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getTags() {
        return this.tagRepository.findAll();
    }

    public Tag getByTagId(Long tagId) {
        return this.tagRepository.getByTagId(tagId);
    }

    public List<Tag> getByUserId(Long userId) {
        return this.tagRepository.getByUserId(userId);
    }

    public List<Tag> getByMovieId(Long movieId) {
        return this.tagRepository.getByMovieId(movieId);
    }

    public List<Tag> getByTag(String tag) {
        return this.tagRepository.getByTag(tag);
    }

}

@RestController
@RequestMapping("/movies")
class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public ResponseEntity<String> serviceInfo() {
        return new ResponseEntity<>("Movie Service", HttpStatus.OK);
    }

    @GetMapping("/version")
    public ResponseEntity<String> serviceVersionInfo() {
        return new ResponseEntity<>("Movie Service v1.0", HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getMovies() {
        System.out.println("[Controller] Get Movies");
        return new ResponseEntity<>(movieService.getMovies(), HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> getByMovieId(@PathVariable Long movieId) {
        System.out.println(String.format("[Controller] Get Movie by movieId [%s]", movieId));
        Movie movie = movieService.getByMovieId(movieId);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/year/{movieYear}")
    public ResponseEntity<List<Movie>> getByMovieYear(@PathVariable Long movieYear) {
        System.out.println(String.format("[Controller] Get Movies by movieYear [%s]", movieYear));
        List<Movie> byMovieYear = movieService.getByMovieYear(movieYear);
        return new ResponseEntity<>(byMovieYear, HttpStatus.OK);
    }

    @GetMapping("/genres/{movieGenres}")
    public ResponseEntity<List<Movie>> getByMovieGenres(@PathVariable String movieGenres) {
        System.out.println(String.format("[Controller] Get Movies by movieGenres [%s]", movieGenres));
        List<Movie> byMovieGenres = movieService.getByMovieGenres(movieGenres);
        return new ResponseEntity<>(byMovieGenres, HttpStatus.OK);
    }
}

@RestController
@RequestMapping("/ratings")
class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public ResponseEntity<String> serviceInfo() {
        return new ResponseEntity<>("Rating Service", HttpStatus.OK);
    }

    @GetMapping("/version")
    public ResponseEntity<String> serviceVersionInfo() {
        return new ResponseEntity<>("Rating Service v1.0", HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Rating>> getRatings() {
        System.out.println("[Controller] Get Ratings");
        return new ResponseEntity<>(ratingService.getRatings(), HttpStatus.OK);
    }

    @GetMapping("/{ratingId}")
    public ResponseEntity<Rating> getByRatingId(@PathVariable Long ratingId) {
        System.out.println(String.format("[Controller] Get Rating by ratingId [%s]", ratingId));
        Rating byRatingId = ratingService.getByRatingId(ratingId);
        return new ResponseEntity<>(byRatingId, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getByUserId(@PathVariable Long userId) {
        System.out.println(String.format("[Controller] Get Ratings by userId [%s]", userId));
        List<Rating> byUserId = ratingService.getByUserId(userId);
        return new ResponseEntity<>(byUserId, HttpStatus.OK);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Rating>> getByMovieId(@PathVariable Long movieId) {
        System.out.println(String.format("[Controller] Get Ratings by movieId [%s]", movieId));
        List<Rating> byMovieId = ratingService.getByMovieId(movieId);
        return new ResponseEntity<>(byMovieId, HttpStatus.OK);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<Rating>> getByRating(@PathVariable Double rating) {
        System.out.println(String.format("[Controller] Get Ratings by rating [%s]", rating));
        List<Rating> byRating = ratingService.getByRating(rating);
        return new ResponseEntity<>(byRating, HttpStatus.OK);
    }

}

@RestController
@RequestMapping("/tags")
class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public ResponseEntity<String> serviceInfo() {
        return new ResponseEntity<>("Tag Service", HttpStatus.OK);
    }

    @GetMapping("/version")
    public ResponseEntity<String> serviceVersionInfo() {
        return new ResponseEntity<>("Tag Service v1.0", HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tag>> getTags() {
        System.out.println("[Controller] Get Tags");
        return new ResponseEntity<>(tagService.getTags(), HttpStatus.OK);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getByRatingId(@PathVariable Long tagId) {
        System.out.println(String.format("[Controller] Get Tag by tagId [%s]", tagId));
        Tag byTagId = tagService.getByTagId(tagId);
        return new ResponseEntity<>(byTagId, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tag>> getByUserId(@PathVariable Long userId) {
        System.out.println(String.format("[Controller] Get Tags by userId [%s]", userId));
        List<Tag> byUserId = tagService.getByUserId(userId);
        return new ResponseEntity<>(byUserId, HttpStatus.OK);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Tag>> getByMovieId(@PathVariable Long movieId) {
        System.out.println(String.format("[Controller] Get Tags by movieId [%s]", movieId));
        List<Tag> byMovieId = tagService.getByMovieId(movieId);
        return new ResponseEntity<>(byMovieId, HttpStatus.OK);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Tag>> getByRating(@PathVariable String tag) {
        System.out.println(String.format("[Controller] Get Tags by tag [%s]", tag));
        List<Tag> byTag = tagService.getByTag(tag);
        return new ResponseEntity<>(byTag, HttpStatus.OK);
    }

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

@Entity
@Table(name = "Rating")
class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    Long ratingId;
    @Column(name = "user_id")
    Long userId;
    @Column(name = "movie_id")
    Long movieId;
    @Column(name = "rating")
    Double rating;
    @Column(name = "timestamp")
    Timestamp timestamp;

    public Rating() {
    }

    public Rating(Long userId, Long movieId, Double rating, Timestamp timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

@Entity
@Table(name = "Tag")
class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    Long tagId;
    @Column(name = "user_id")
    Long userId;
    @Column(name = "movie_id")
    Long movieId;
    @Column(name = "tag")
    String tag;
    @Column(name = "timestamp")
    Timestamp timestamp;

    public Tag() {
    }

    public Tag(Long userId, Long movieId, String tag, Timestamp timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", userId=" + userId +
                ", movieId=" + movieId +
                ", tag='" + tag + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}