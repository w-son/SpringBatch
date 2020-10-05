package com.son.SpringBatch;

import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@SpringBatchTest
@RunWith(SpringRunner.class)
public class SpringBatchJobTests {


    // 준비중
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


}
