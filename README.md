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


## Chunk 
Chunk 는 Spring Batch 의 트랜잭션 기본 단위이다  
예를 들어 Chunk 의 크기가 10 개의 row 라고 가정하면 10 개의 row 단위로 commit 및 rollback 이 실행된다는 의미이다  
Chunk 내에서는 `ItemReader`, `IteamProcessor`, `ItemWriter` 가 데이터를 읽기, 처리하기, 쓰기 를 진행하는데  
`ItemReader` 내에서는 읽어 들일 데이터를 Page 단위로 나눠 읽어 들일 수 있다  
Chunk 의 크기가 100 이고 Page 의 크기가 10 이면 하나의 Chunk 를 처리하기 위해 10 번의 조회가 필요한 셈이다  
성능 최적화를 위해 가능하면 Chunk 와 Page 의 크기를 같게 해서 조회 하는 것이 바람직하다고 한다  


## ItemReader, ItemProcessor, ItemWriter
Tasklet 과 마찬가지로 Step 을 구성하는 요소들이다  
하나의 Step 을 구성하는 Item 에서 `Reader` 와 `Writer` 는 필수이고 `Processor` 는 선택적으로 이용이 가능하다  
이들 요소들이 작업 중 실패 하더라도 상태를 기억해서 실패 지점으로부터 재시도 가능하게끔 지원하는 인터페이스가 ItemStream 이다  
사용자는 필요에 따라 Step 의 요소들이나 ItemStream 을 커스텀 할 수 있다    
이제 각각의 요소들의 기능과 동작 원리에 대해서 살펴보자   


### ItemReader
데이터베이스, 파일시스템, JSON 등의 소스에서 데이터를 읽어들이는 역할을 한다  
이 외의 데이터 소스를 Custom Reader 를 통해서 읽을 수 있다  
ItemReader 인터페이스의 여러 구현체가 존재하지만 이 프로젝트에서는 JPA 를 사용하는 인터페이스 구현체를 사용해볼 예정이다  
JPA 사용시 Reader 의 여러가지 이용법, 주의할 점, 해결 방법을 소스와 주석을 보며 공부하자  

- [JpaReaderBatchConfig](src/main/java/com/son/SpringBatch/config/JpaReaderBatchConfig.java)  


### ItemWriter
ItemReader 는 Chunk 기반으로 묶인 Item 을 기반으로 하나씩 읽은 후 필요에 따라 Processor 를 적용하게 된다  
ItemWriter 는 이렇게 처리된 Chunk 를 다시 개별로 처리하는 것이 아닌 하나의 묶음, 즉 list 의 형태로 처리 가능하다    
다시말해서 Chunk 내의 10개 Item 을 하나씩 Reader 가 읽어서 하나씩 Processor 가 알맞는 작업을 처리했다면  
Writer 가 처리된 10개를 한꺼번에 받아서 처리할 수 있다는 의미이다  

JPA 기반 Writer 는 Entity 클래스를 제네릭 타입으로 받아야 하기 때문에 리턴 타입을 결정해 주기 위해서 Processor 를 사용해야하고   
목적에 따라서 Writer 를 커스터마이징하여 시스템 출력을 이용하거나 외부 API 호출에 이용할 수 있다  
관련 소스를와 주석을 보며 공부 해보자  

- [JpaWriterBatchConfig](src/main/java/com/son/SpringBatch/config/JpaWriterBatchConfig.java)