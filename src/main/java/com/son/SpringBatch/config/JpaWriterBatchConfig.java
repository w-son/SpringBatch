package com.son.SpringBatch.config;

import com.son.SpringBatch.domain.Person;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class JpaWriterBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(JpaWriterBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final int chunkSize = 10;


    @Bean
    public Job jpaWriterBatchJob() {

        return jobBuilderFactory.get("jpaWriterBatchJob")
                .start(jpaWriterBatchStep())
                .build();
    }


    @Bean
    public Step jpaWriterBatchStep() {

        return stepBuilderFactory.get("jpaWriterBatchStep")
                .<Person, Person> chunk(chunkSize)
                .reader(jpaWriterPagingItemReader())
                .processor(jpaWriterItemProcessor())
                .writer(jpaWriterPagingItemWriter())
                .build();
    }


    @Bean
    @StepScope
    public JpaPagingItemReader<Person> jpaWriterPagingItemReader() {

        JpaPagingItemReader<Person> jpaPagingItemReader = new JpaPagingItemReader<>() {

            @Override
            public int getPage() {
                return 0;
            }
        };

        jpaPagingItemReader.setQueryString(
                "select p from Person p where p.firstName = :firstName"
        );

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", "Peter");
        jpaPagingItemReader.setParameterValues(params);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(chunkSize);

        return jpaPagingItemReader;
    }


    @Bean
    public ItemProcessor<Person, Person> jpaWriterItemProcessor() {
        return person -> new Person(person.getFirstName(), "Griffin");
    }


    @Bean
    public JpaItemWriter<Person> jpaWriterPagingItemWriter() {

        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return jpaItemWriter;
    }


    /*

    시스템 출력, 외부 API 호출 등
    Writer 상 커스터마이징이 필요한 경우

    @Bean
    public ItemWriter<Person> customItemWriter() {

        return items -> {
            for (Person item : items) {
                System.out.println(item);
            }
        };
    }

    */


}
