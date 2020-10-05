package com.son.SpringBatch.config;

import com.son.SpringBatch.domain.Many;
import com.son.SpringBatch.domain.One;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JpaSampleBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(JpaSampleBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final String JPA_SAMPLE_BATCH_JOB = "jpaSampleBatchJob";
    private static final String JPA_SAMPLE_BATCH_STEP = "jpaSampleBatchStep";
    private static final String JPA_SAMPLE_BATCH_READER = "jpaSampleBatchReader";

    private final int chunkSize = 10;


    @Bean(JPA_SAMPLE_BATCH_JOB)
    public Job jpaSampleBatchJob() {

        return jobBuilderFactory.get(JPA_SAMPLE_BATCH_JOB)
                .start(jpaSampleBatchStep())
                .build();
    }


    @Bean(JPA_SAMPLE_BATCH_STEP)
    public Step jpaSampleBatchStep() {

        return stepBuilderFactory.get(JPA_SAMPLE_BATCH_STEP)
                .<One, Integer> chunk(chunkSize)
                .reader(jpaSampleBatchReader())
                .processor(jpaSampleBatchProcessor())
                .writer(jpaSampleBatchWriter())
                .build();
    }


    @Bean
    public JpaPagingItemReader<One> jpaSampleBatchReader() {

        return new JpaPagingItemReaderBuilder<One>()
                .name(JPA_SAMPLE_BATCH_READER)
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select o from One o join fetch o.manyList m")
                .build();
    }


    @Bean
    public ItemProcessor<One, Integer> jpaSampleBatchProcessor() {

        return one -> {
            List<Many> manyList = one.getManyList();

            return manyList.stream()
                    .mapToInt(Many::getNumber)
                    .sum(); // Integer Collection 의 .stream().reduce(0, Integer::sum);
        };
    }


    @Bean
    public ItemWriter<Integer> jpaSampleBatchWriter() {

        return sumOfManyNumbers -> {
            System.out.println("One 의 숫자 집계 결과 : " + sumOfManyNumbers);
        };
    }


}
