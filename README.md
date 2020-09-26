# Spring Batch
대용량 데이터 처리 작업의 자동화 및 복구 작업의 단위  
무거운 프로세스를 계획적으로 특정 시기에 독립적으로 구동해야하고 이에 대한 수정, 복구 및 재실행이 필요할 때 사용된다   


`Batch` 작업의 단위를 `JOB`이라 하고  
`JOB` 내에는 여러 세부 단계인 `STEP`이 존재하는데  
`STEP`은 `TASKLET` 혹은 `READER & PROCESSOR & WRITER`로 이루어져있다  


## Meta Data
application.properties 파일의 설정에  
`spring.batch.initialize-schema=always`를 추가하면   
어플리케이션 구동 시 Spring Batch 와 관련된 테이블이 자동으로 생성된다   
다음 스키마들의 내용을 간단히 살펴보며 Batch 프로세스에 대한 감을 익혀보자  
 
- BATCH_JOB_INSTANCE  

  - JobBuilderFactory 의 get() 으로 생성한 Job 객체들이 저장된다 
  - 같은 이름의 Job 은 중복되어 저장되지 않고 파라미터가 같은 경우 재실행 되지 않는다  
   
- BATCH_JOB_EXECUTION

  - Job 의 실행 이력을 담은 객체들이 저장된다 
  - Spring Configuration 의 Program argument 의 인자값에 따라 같은 Job 을 구분하는 요소가 될 수 있다  
  - 실행 시도한 모든 Job 기록들이 담겨 있으며   
    Job 이름이 같은데 argument 값이 같아 시도하지 않은 경우  
    혹은 Job 실행 중 실패한 경우의 정보도 담겨 있다  

- BATCH_JOB_EXECUTION_PARAMS  

  - 어플리케이션 실행 시 Program argument 로 넘긴 인자에 대한 정보가 저장된다
  

## Steps on conditional circumstances
Job 내의 각각의 Step 이 실패할 상황을 고려해 분기 처리를 이용한다  
if, else if, else 와 유사하게 메서드를 사용하여 step 간의 분기 처리가 가능하다  
예제 소스를 보며 각각의 Step 이 분기되어 처리될 수 있는지 확인해보자  

- [ConditionalBatchConfig](src/main/java/com/son/SpringBatch/config/ConditionalBatchConfig.java)  