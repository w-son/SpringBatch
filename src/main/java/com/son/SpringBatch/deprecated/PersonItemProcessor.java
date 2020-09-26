//package com.son.SpringBatch.batch;
//
//import com.son.SpringBatch.domain.Person;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.item.ItemProcessor;
//
///**
// * Batch 프로세스는 통상적으로 데이터를 받고 변환한 후 다른 파이프라인으로 전송 시키는 것이 목적이다.
// * PersonItemProcessor 클래스는 Person 객체를 받아 대문자로 벼환 후 리턴하는 Batch 프로세스이다.
// */
//
//public class PersonItemProcessor implements ItemProcessor<Person, Person> {
//
//    private static final Logger logger = LoggerFactory.getLogger(PersonItemProcessor.class);
//
//    @Override
//    public Person process(final Person person) throws Exception {
//
//        final String firstName = person.getFirstName().toUpperCase();
//        final String lastName = person.getLastName().toUpperCase();
//
//        final Person personToUpper = new Person(firstName, lastName);
//        logger.info("Converted " + person +  " to " + personToUpper + '\n');
//
//        return personToUpper;
//    }
//
//}
