package com.son.SpringBatch;

import com.son.SpringBatch.config.JpaSampleBatchConfig;
import com.son.SpringBatch.domain.Many;
import com.son.SpringBatch.domain.One;
import com.son.SpringBatch.repository.ManyRepository;
import com.son.SpringBatch.repository.OneRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.type.LocalDateType.FORMATTER;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JpaSampleBatchConfig.class, BatchJobConfig.class})
@TestPropertySource(properties = "job.name=jpaSampleBatchJob")
public class SpringBatchJobTests {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OneRepository oneRepository;

    @Autowired
    private ManyRepository manyRepository;


    @Test
    public void 통합테스트() throws Exception {

        One one = new One("one", 10);

        oneRepository.save(one);

        Many many1 = new Many("many1", 10);
        Many many2 = new Many("many2", 10);

        many1.updateOne(one);
        many2.updateOne(one);

        manyRepository.save(many1);
        manyRepository.save(many2);

        LocalDateTime now = LocalDateTime.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", now.format(FORMATTER))
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        List<One> all = oneRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        List<Many> manyList = all.get(0).getManyList();
        assertThat(manyList.size()).isEqualTo(2);
    }


}
