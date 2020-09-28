package com.son.SpringBatch.config;

import com.son.SpringBatch.domain.Person;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JpaBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(JpaBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final int chunkSize = 10;


    @Bean
    public Job jpaBatchJob() {

        return jobBuilderFactory.get("jpaBatchJob")
                .start(jpaBatchStep())
                .build();
    }


    /*
     <Person, Person> chunk() 을 확인해보면
     첫 번째 Person 은 Reader 에서 반환할 타입
     두 번째 Person 은 Writer 에서 파라미터로 넘어올 타입
     */
    @Bean
    public Step jpaBatchStep() {

        return stepBuilderFactory.get("jpaBatchStep")
                .<Person, Person> chunk(chunkSize)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }


    @Bean
    public JpaPagingItemReader<Person> jpaPagingItemReader() {

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("firstName", "William");

        return new JpaPagingItemReaderBuilder<Person>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select p from Person p where p.firstName = :firstName")
                .parameterValues(parameterValues)
                .build();
    }


    /*

    1. JpaPagingItemReader 를 직접 생성 하는 경우

    @Bean
    @StepScope
    public JpaPagingItemReader<Person> itemReader() {

        JpaPagingItemReader<Person> jpaPagingItemReader = new JpaPagingItemReader<Person>();
        jpaPagingItemReader.setQueryString(
                "select p from Person p where p.firstName = :firstName"
        );

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", "William");
        jpaPagingItemReader.setParameterValues(params);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(chunkSize);

        return jpaPagingItemReader;
    }

    2. Spring Data 를 사용하는 RepositoryItemReader 를 사용 하는 경우

    @Bean
    @StepScope
    public RepositoryItemReader<Person> reader() {
        RepositoryItemReader<Person> reader = new RepositoryItemReader<>();
        reader.setRepository(personRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("name", Sort.Direction.ASC));
        return reader;
    }

    */


    private ItemWriter<Person> jpaPagingItemWriter() {

        return people -> {
            for(Person person : people) {
                log.info(person.getFirstName() + ' ' + person.getLastName());
            }
        };
    }


}
