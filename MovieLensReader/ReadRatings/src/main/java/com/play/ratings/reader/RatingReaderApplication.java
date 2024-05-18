package com.play.ratings.reader;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@SpringBootApplication
public class RatingReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatingReaderApplication.class, args);
    }
}

@Component
class RatingReaderRunner implements CommandLineRunner {
    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Rating> ratings = ratingRepository.findAll();
        ratings.forEach(rating -> System.out.println("Rating: " + rating.getRating()));
        System.out.println(String.format("Ratings count: %s", ratings.size()));
    }
}

@Repository
interface RatingRepository extends JpaRepository<Rating, Long> {
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
