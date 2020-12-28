# &#127759; Job Scope, Step Scope, Parameters
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

- [ParameterBatchConfig](../src/main/java/com/son/SpringBatch/config/ParameterBatchConfig.java)

