package com.son.SpringBatch;

import com.son.SpringBatch.config.JpaSampleBatchConfig;
import com.son.SpringBatch.domain.Many;
import com.son.SpringBatch.domain.One;
import com.son.SpringBatch.repository.ManyRepository;
import com.son.SpringBatch.repository.OneRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.type.LocalDateType.FORMATTER;

@SpringBatchTest
@SpringBootTest(classes = {JpaSampleBatchConfig.class, BatchJobConfig.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SpringBatchJobUnitTests {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OneRepository oneRepository;

    @Autowired
    private ManyRepository manyRepository;

    @Autowired
    private JpaPagingItemReader<One> reader;


    @Before
    public void tearDown() {
        oneRepository.deleteAll();
        manyRepository.deleteAll();
    }


    /*
     원래 @TestExecutionListeners 에
     DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class 을 등록함으로써
     Spring Batch 의 테스트 환경을 구축하기 위한 설정이 필요하고
     이 중 StepScopeTestExecutionListener.class 이 필요로 하는 팩토리 메서드가 getStepExecution 메서드이다

     @SpringBatchTest Annotation 을 활용한다면 이런 부분들을 자동으로 설정해주며
     팩토리 메소드인 getStepExecution 을 구현해 놓기만 하면 정상적으로 동작한다
     메서드명은 조건과 무방하고 StepExecution 을 리턴하는 메서드만 정의해 놓으면 된다
     */
    public StepExecution getStepExecution() {

        LocalDateTime now = LocalDateTime.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("orderDate", now.format(FORMATTER))
                .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }


    @Test
    public void 단위테스트() throws Exception {

        One one = new One("one", 10);

        oneRepository.save(one);

        Many many1 = new Many("many1", 10);
        Many many2 = new Many("many2", 10);

        many1.updateOne(one);
        many2.updateOne(one);

        manyRepository.save(many1);
        manyRepository.save(many2);

        reader.open(new ExecutionContext());

        assertThat(reader.read().getNumber()).isEqualTo(10);
        assertThat(reader.read()).isNull();
    }


}
