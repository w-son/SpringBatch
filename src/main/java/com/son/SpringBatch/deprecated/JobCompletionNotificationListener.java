//package com.son.SpringBatch.config;
//
//import com.son.SpringBatch.repository.PersonRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.listener.JobExecutionListenerSupport;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
//
//    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
//
//    private final PersonRepository personRepository;
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//
//        if(jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
//            logger.info("Insertion Detected");
//
//             personRepository.findAll()
//                     .forEach(person -> {
//                         final String firstName = person.getFirstName();
//                         final String lastName = person.getLastName();
//                         logger.info(firstName + " " + lastName);
//                     });
//        }
//    }
//}
