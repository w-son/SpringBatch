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


## Job Scope, Step Scope, Parameters
Spring Batch 는 MVC 와 비슷하게 Job 과 Step 에 Scope 를 부여한다  
Spring Singleton Scope 와 마찬가지로 Job 혹은 Step 이 Bean 으로 등록되어 호출 시에 생성 및 반환 작업이 이루어진다  
Job Parameters 역시 이런 Scope 들이 생성될 때 사용될 수 있고 이는 Job 과 Step 들을 상황별로 구분하여 호출하기에 용이하다  


이처럼 Job 과 Step Bean 을 생성 시점에 할당 하게끔 지연 시킨다면 
1. Late Binding 을 통해 상황에 따른 Batch 실행에 유연함을 더할 수 있다  
   실행 시점에 Bean 과 Parameter 가 결정되는 구조라면 리턴 값에 따라 실행해야하는 Batch 가 다른 작업에 대한 설계가 힘들다   
2. 시스템 변수 (application.properties) 를 사용하는 구조 와 달리 Spring Batch 에서 관리하는 스키마를 이용할 수 있다
3. 각 Job 과 Step 이 별도의 Scope 를 관리하기 때문에 race condition 을 피할 수 있다  
 

이와 더불어  
Job Parameters 는 Scope 의 value 값이 `job` 이거나 `step`  
즉 @JobScope 나 @StepScope 에 해당하는 빈으로 선언된 클래스나 메서드에서만 받아 이용할 수 있음에 유의하자   
일반 Singleton Bean 으로 선언된 빈은 Job Parameters 를 사용할 수 없다  

- [ParameterBatchConfig](src/main/java/com/son/SpringBatch/config/ParameterBatchConfig.java)