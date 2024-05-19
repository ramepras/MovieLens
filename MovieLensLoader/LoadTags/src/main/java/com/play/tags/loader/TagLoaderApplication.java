package com.play.tags.loader;

import jakarta.persistence.*;
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
import java.sql.Timestamp;
import java.time.Instant;

@SpringBootApplication
public class TagLoaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(TagLoaderApplication.class, args);
    }

    @Bean
    Job job(TagBatchConfig tagBatchConfig) {
        return new JobBuilder("JobCsvTagToDb", tagBatchConfig.jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tagBatchConfig.csvTagToDb())
                .build();
    }

    @Configuration
    class TagBatchConfig {
        private JobRepository jobRepository;
        private PlatformTransactionManager platformTransactionManager;
        private DataSource dataSource;

        public TagBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
            this.jobRepository = jobRepository;
            this.platformTransactionManager = platformTransactionManager;
            this.dataSource = dataSource;
        }

        @Bean
        FlatFileItemReader<Tag> tagReader() {
            return new FlatFileItemReaderBuilder<Tag>()
                    .name("csvTagReader")
                    .resource(new ClassPathResource("data/movielens-tags-bigfile.csv"))
                    .delimited().delimiter(",")
                    .names("userId", "movieId", "tag", "timestamp")
                    .linesToSkip(1)
                    .fieldSetMapper(new FieldSetMapper<Tag>() {
                        @Override
                        public Tag mapFieldSet(FieldSet fieldSet) throws BindException {
                            long ts = fieldSet.readLong("timestamp");
                            long epochMilli = Instant.now().toEpochMilli();
                            Timestamp timestamp = (isNotNullNotEmpty(String.valueOf(ts))) ? new Timestamp(ts) : new Timestamp(epochMilli); // if null then keep current time.
                            Tag tag = new Tag(fieldSet.readLong("userId"),
                                    fieldSet.readLong("movieId"),
                                    fieldSet.readString("tag"),
                                    timestamp);

                            return tag;
                        }
                    })
                    .build();

        }

        @Bean
        JdbcBatchItemWriter<Tag> tagWriter() {
            String sql = "INSERT INTO Tag (user_id, movie_id, tag, timestamp) VALUES (?,?,?,?)";
            return new JdbcBatchItemWriterBuilder<Tag>()
                    .sql(sql)
                    .dataSource(dataSource)
                    .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Tag>() {
                        @Override
                        public void setValues(Tag item, PreparedStatement ps) throws SQLException {
                            System.out.println("insert");
                            ps.setLong(1, item.getUserId());
                            ps.setLong(2, item.getMovieId());
                            ps.setString(3, item.getTag());
                            ps.setTimestamp(4, item.getTimestamp());
                        }
                    })
                    .build();

        }

        @Bean
        Step csvTagToDb() {
            return new StepBuilder("StepCsvToDb", jobRepository)
                    .<Tag, Tag>chunk(100, platformTransactionManager)
                    .reader(tagReader())
                    .writer(tagWriter())
                    .build();
        }

        private static boolean isNotNullNotEmpty(String str) {
            return str != null && !str.trim().isEmpty();
        }
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
