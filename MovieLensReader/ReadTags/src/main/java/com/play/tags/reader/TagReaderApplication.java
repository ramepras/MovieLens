package com.play.tags.reader;

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
public class TagReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(TagReaderApplication.class, args);
    }
}

@Component
class TagReaderRunner implements CommandLineRunner {
    @Autowired
    private TagRepository tagRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Tag> tags = tagRepository.findAll();
        tags.forEach(tag -> System.out.println("Tag: " + tag.getTag()));
        System.out.println(String.format("Tags count: %s", tags.size()));
    }
}

@Repository
interface TagRepository extends JpaRepository<Tag, Long> {
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