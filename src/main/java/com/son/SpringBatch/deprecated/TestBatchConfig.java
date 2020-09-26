//package com.son.SpringBatch.config;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.persistence.EntityManagerFactory;
//
//
//@Configuration
//@RequiredArgsConstructor
//public class TestConfig {
//
//
//    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final EntityManagerFactory entityManagerFactory;
//
//
//    @Bean
//    public Job mainJob() {
//
//        return jobBuilderFactory.get("mainJob")
//                .start(stepWithTasklet())
//                .build();
//    }
//
//    @Bean
//    public Step stepWithTasklet() {
//
//        return stepBuilderFactory.get("stepWithTasklet")
//                .tasklet((contribution, chunkContext) -> {
//                    log.info("This step is using tasklet");
//                    return RepeatStatus.FINISHED;
//                })
//                .build();
//    }
//
//    @Bean
//    public FlatFileItemReader<Person> reader() {
//
//        return new FlatFileItemReaderBuilder<Person>()
//                .name("personItemReader")
//                .resource(new ClassPathResource("sample-data.csv"))
//                .delimited()
//                .names(new String[]{"firstName", "lastName"})
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
//                    {
//                        setTargetType(Person.class);
//                    }
//                })
//                .build();
//    }
//
//    @Bean
//    public PersonItemProcessor processor() {
//
//        return new PersonItemProcessor();
//    }
//
//    @Bean
//    public JpaItemWriter<Person> writer() {
//
//        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriter<>();
//        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//        return jpaItemWriter;
//    }
//
//    @Bean
//    public Step stepOne() {
//
//        return stepBuilderFactory.get("stepOne")
//                .<Person, Person> chunk(10)
//                .reader(reader())
//                .processor(processor())
//                .writer(writer())
//                .build();
//    }
//
//    @Bean
//    public Job importUserJob(JobCompletionNotificationListener listener, Step stepOne) {
//
//        return jobBuilderFactory.get("importUserJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .flow(stepOne)
//                .end()
//                .build();
//    }
//
//}
