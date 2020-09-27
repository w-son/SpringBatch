package com.son.SpringBatch.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ParameterBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(ParameterBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    /*
     어플리케이션 설정부에 Edit Configuration > Program Arguments 을
     --job.name=scopeJob requestDate=20200927
     와 같이 파라미터 값을 주면 requestDate 값을 각각의 Scope 에 전달하여 실행할 수 있다
     Job 과 Step 을 호출하는 쪽에서는 "null" 을 전달하여 어플리케이션 실행 시점에 인자값이 할당 되지 않도록 한다
     */
    @Bean
    public Job scopeJob() {

        return jobBuilderFactory.get("scopeJob")
                .start(scopeStep1(null))
                .next(scopeStep2())
                .build();
    }


    @Bean
    @JobScope
    public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return stepBuilderFactory.get("scopeStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("1 번째 Step 의 요청 날짜 " + requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public Step scopeStep2() {

        return stepBuilderFactory.get("scopeStep2")
                .tasklet(scopeTasklet(null))
                .build();
    }


    @Bean
    @StepScope
    public Tasklet scopeTasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return (stepContribution, chunkContext) -> {
            log.info("2 번째 Step 의 요청 날짜 " + requestDate);
            return RepeatStatus.FINISHED;
        };
    }


}
