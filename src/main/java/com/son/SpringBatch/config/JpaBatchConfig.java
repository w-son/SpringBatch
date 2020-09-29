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

     Reader 를 생성하는 방법
     1. JpaPagingItemReader 직접 구현
     2. JpaPagingItemReaderBuilder 를 통한 생성
     3. Spring Data 를 이용한 RepositoryItemReader 이용
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
    @StepScope
    public JpaPagingItemReader<Person> jpaPagingItemReader() {

        /*
         전체 데이터 개수 1000, Chunk 크기 100 인 상황을 가정했을 때 다음과 같은 문제가 발생할 수 있다

         1. offset 과 limit 설정에 문제가 발생한다
         첫 Chunk 의 offset 과 limit 이 (0, 100) 일 것이고
         두 번째 처리할 Chunk 의 offset 과 limit 은 (100, 100) 일 것이다
         문제는 하나의 Chunk 를 처리한 후 100 번째 데이터가 0 번째로 초기화 된다는 것이다
         이를 방지하기 위해 페이지의 offset 을 0 으로 고정해야 한다
         > JpaPagingItemReader 의 getPage 가 항상 0 을 리턴하게 만들어 준다

         2. pageSize 를 초기화 시켜주지 않는다면
         JpaPagingItemReader 의 기본 페이지 크기인 10 으로 동작하게 되고
         chunk 단위로 영속성 컨텍스트를 초기화 시키는 JpaPagingItemReader 때문에 문제가 발생한다
         때문에 기본 pageSize 인 10 개로 데이터를 읽게되면
         각 Chunk 당 맨 마지막 페이지의 10개의 데이터만 트랜잭션에 남아 있을 것이다
         > setPageSize 로 Chunk 크기를 맞춰준다
         */

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
        params.put("firstName", "William");
        jpaPagingItemReader.setParameterValues(params);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(chunkSize);

        return jpaPagingItemReader;
    }


    /*

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

    @Bean
    @StepScope
    public RepositoryItemReader<Person> repositoryItemReader() {
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
