package com.son.SpringBatch.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class ConditionalBatchConfig {


    private static final Logger log = LoggerFactory.getLogger(ConditionalBatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job conditionalJob() {

        /*
         start() : 처음 시작할 step 지정
         on() : stepContribution.setExitStatus(ExitStatus.FAILED); 와 같이
                step 에서 지정된 ExitStatus 상태에 따라 어떤 step 을 수행할 것인지 지정 가능
                * 일 경우 이전의 on 에 명시된 status 를 제외한 모든 경우
                JobExecutionDecider 를 상속받는 클래스를 정의하여 분기 상황을 커스텀 할 수 있다
         to() : 다음 수행할 step 지정
         next() : start, to 이후에 수행될 step 을 지정
         from() : 특정 step 의 상황을 정의할 때 사용하는 메서드
                  start() 과 활용법이 비슷하다
                  이미 정의된 step 이라면 필수적으로 사용
         */

        return jobBuilderFactory.get("conditionalJob")
                .start(conditionalStep1())
                .next(decider())
                    .from(decider())
                        .on("TO STEP 2")
                        .to(conditionalStep2())
                    .from(decider())
                        .on("TO STEP 3")
                        .to(conditionalStep3())
                            .on("*")
                            .end()
                .end()
                .build();
    }


    @Bean
    public Step conditionalStep1() {

        return stepBuilderFactory.get("conditionalStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("첫 번째 Step 입니다");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public Step conditionalStep2() {

        return stepBuilderFactory.get("conditionalStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("두 번째 Step 입니다");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public Step conditionalStep3() {

        return stepBuilderFactory.get("conditionalStep3")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("세 번째 Step 입니다");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public JobExecutionDecider decider() {
        return new StepDecider();
    }


    public static class StepDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();

            int randomNumber = random.nextInt();
            log.info(randomNumber + " 가 랜덤 넘버로 선정되었습니다");
            if(randomNumber % 2 == 0) {
                return new FlowExecutionStatus("TO STEP 2");
            }
            else {
                return new FlowExecutionStatus("TO STEP 3");
            }
        }

    }


}
