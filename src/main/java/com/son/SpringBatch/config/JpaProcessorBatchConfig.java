package com.son.SpringBatch.config;

import com.son.SpringBatch.domain.Person;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JpaProcessorBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(JpaProcessorBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public static final String JPA_PROCESSOR_BATCH_JOB = "jpaProcessorBatchJob";
    public static final String JPA_PROCESSOR_BATCH_STEP = "jpaProcessorBatchStep";
    public static final String JPA_PROCESSOR_BATCH_READER = "jpaProcessorBatchReader";

    private final int chunkSize = 10;


    @Bean(JPA_PROCESSOR_BATCH_JOB)
    public Job jpaProcessorBatchJob() {

        return jobBuilderFactory.get(JPA_PROCESSOR_BATCH_JOB)
                .preventRestart()
                .start(jpaProcessorBatchStep())
                .build();
    }


    @Bean(JPA_PROCESSOR_BATCH_STEP)
    public Step jpaProcessorBatchStep() {

        return stepBuilderFactory.get(JPA_PROCESSOR_BATCH_STEP)
                .<Person, String> chunk(chunkSize)
                .reader(jpaProcessorBatchReader())
                .processor(compositeItemProcessor())
                .writer(jpaProcessorBatchWriter())
                .build();
    }


    @Bean
    public JpaPagingItemReader<Person> jpaProcessorBatchReader() {

        return new JpaPagingItemReaderBuilder<Person>()
                .name(JPA_PROCESSOR_BATCH_READER)
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select p from Person p")
                .build();
    }


    /*
     Person -> String -> String
     으로 변환되는 프로세서 체이닝이기 때문에
     CompositeItemProcessor 에 제네릭 타입을 주지 않았다
     */
    @Bean
    public CompositeItemProcessor compositeItemProcessor() {

        List<ItemProcessor> processors = new ArrayList<>(2);
        processors.add(jpaProcessorBatchProcessor1());
        processors.add(jpaProcessorBatchProcessor2());

        CompositeItemProcessor processor = new CompositeItemProcessor<>();
        processor.setDelegates(processors);

        return processor;
    }


    @Bean
    public ItemProcessor<Person, String> jpaProcessorBatchProcessor1() {

        return person -> {

            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName + " " + lastName;

            return firstName.contains("Peter") ? null : fullName;
        };
    }


    @Bean
    public ItemProcessor<String, String> jpaProcessorBatchProcessor2() {

        return String::toUpperCase;
    }


    private ItemWriter<String> jpaProcessorBatchWriter() {

        return people -> {
            for(String fullName : people) {
                log.info(fullName);
            }
        };
    }


}
