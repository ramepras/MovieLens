package com.play.movies.loader;

import com.play.movies.loader.util.MovieUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class MovieLoaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieLoaderApplication.class, args);
    }

    @Bean
    Job job(MovieBatchConfig movieBatchConfig) {
        return new JobBuilder("JobCsvMoviesToDb", movieBatchConfig.jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(movieBatchConfig.csvMoviesToDb())
                .build();
    }

    @Configuration
    class MovieBatchConfig {
        private final JobRepository jobRepository;
        private final PlatformTransactionManager platformTransactionManager;
        private final DataSource dataSource;

        public MovieBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
            this.jobRepository = jobRepository;
            this.platformTransactionManager = platformTransactionManager;
            this.dataSource = dataSource;
        }

        @Bean
        FlatFileItemReader<Movie> movieReader() {
            return new FlatFileItemReaderBuilder<Movie>()
                    .name("csvMovieReader")
                    .resource(new ClassPathResource("data/movielens-movies-bigfile.csv"))
                    .delimited().delimiter(",")
                    .names("movieId", "movieTitle", "movieGenres")
                    .linesToSkip(1)
                    .fieldSetMapper(new FieldSetMapper<Movie>() {
                        @Override
                        public Movie mapFieldSet(FieldSet fieldSet) throws BindException {
                            String movieTitle = fieldSet.readString("movieTitle");
                            System.out.println(movieTitle);
                            return new Movie(fieldSet.readLong("movieId"),
                                    MovieUtil.extractMovieTitle(movieTitle),
                                    MovieUtil.extractMovieYear(movieTitle), // year is extracted from title ...
                                    MovieUtil.extractMovieGenres(fieldSet.readString("movieGenres")));
                        }
                    })
                    .build();
        }

        @Bean
        JdbcBatchItemWriter<Movie> movieWriter() {
            String sql = "insert into Movie (movie_id, movie_title, movie_year, movie_genres) values (?,?,?,?)";
            return new JdbcBatchItemWriterBuilder<Movie>()
                    .sql(sql)
                    .dataSource(dataSource)
                    .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Movie>() {
                        @Override
                        public void setValues(Movie item, PreparedStatement ps) throws SQLException {
                            ps.setLong(1, item.getMovieId());
                            ps.setString(2, item.getMovieTitle());
                            ps.setLong(3, item.getMovieYear());
                            ps.setString(4, item.getMovieGenres());
                        }
                    })
                    .build();
        }

        @Bean
        Step csvMoviesToDb() {
            return new StepBuilder("StepCsvMoviesToDb", jobRepository)
                    .<Movie, Movie>chunk(100, platformTransactionManager)
                    .reader(movieReader())
                    .writer(movieWriter())
                    .build();
        }
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

    public Movie(Long movieId, String movieTitle, Long movieYear, String movieGenres) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieYear = movieYear;
        this.movieGenres = movieGenres;
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
}
