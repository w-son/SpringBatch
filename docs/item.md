# &#127759; ItemReader, ItemProcessor, ItemWriter
Tasklet 과 마찬가지로 Step 을 구성하는 요소들이다  
하나의 Step 을 구성하는 Item 에서 `Reader` 와 `Writer` 는 필수이고 `Processor` 는 선택적으로 이용이 가능하다  
이들 요소들이 작업 중 실패 하더라도 상태를 기억해서 실패 지점으로부터 재시도 가능하게끔 지원하는 인터페이스가 ItemStream 이다  
사용자는 필요에 따라 Step 의 요소들이나 ItemStream 을 커스텀 할 수 있다    
이제 각각의 요소들의 기능과 동작 원리에 대해서 살펴보자   


## ItemReader
데이터베이스, 파일시스템, JSON 등의 소스에서 데이터를 읽어들이는 역할을 한다  
이 외의 데이터 소스를 Custom Reader 를 통해서 읽을 수 있다  
ItemReader 인터페이스의 여러 구현체가 존재하지만 이 프로젝트에서는 JPA 를 사용하는 인터페이스 구현체를 사용해볼 예정이다  
JPA 사용시 Reader 의 여러가지 이용법, 주의할 점, 해결 방법을 소스와 주석을 보며 공부하자  

- [JpaReaderBatchConfig](../src/main/java/com/son/SpringBatch/config/JpaReaderBatchConfig.java)  


## ItemWriter
ItemReader 는 Chunk 기반으로 묶인 Item 을 기반으로 하나씩 읽은 후 필요에 따라 Processor 를 적용하게 된다  
ItemWriter 는 이렇게 처리된 Chunk 를 다시 개별로 처리하는 것이 아닌 하나의 묶음, 즉 list 의 형태로 처리 가능하다    
다시말해서 Chunk 내의 10개 Item 을 하나씩 Reader 가 읽어서 하나씩 Processor 가 알맞는 작업을 처리했다면  
Writer 가 처리된 10개를 한꺼번에 받아서 처리할 수 있다는 의미이다  

JPA 기반 Writer 는 Entity 클래스를 제네릭 타입으로 받아야 하기 때문에 리턴 타입을 결정해 주기 위해서 Processor 를 사용해야하고   
목적에 따라서 Writer 를 커스터마이징하여 시스템 출력을 이용하거나 외부 API 호출에 이용할 수 있다  
관련 소스를 코드와 주석을 보며 공부 해보자  

- [JpaWriterBatchConfig](../src/main/java/com/son/SpringBatch/config/JpaWriterBatchConfig.java)


## ItemProcessor
ItemProcessor 는 사실 선택적으로 Step 에 포함 될 수도 있고 그렇지 않을 수도 있다  
Reader 에서 읽어들인 데이터를 `가공`하거나 `필터`하여 Writer 에게 전달하는 역할을 맡는다  
다시 말해, 데이터를 읽고 쓰는 과정에서 필요한 비즈니스 로직을 분리해 내 처리하는 부분이 바로 ItemProcessor 라고 볼 수 있다   


ItemProcessor 의 구현체중 가장 많이 사용되는 CompositeItemProcessor 은  
여러가지 로직을 각각의 ItemProcessor 에 분리한 후 이를 병합하는데 이용된다  
관련 소스를 코드와 주석을 보며 공부 해보자  

- [JpaProcessorBatchConfig](../src/main/java/com/son/SpringBatch/config/JpaProcessorBatchConfig.java)
