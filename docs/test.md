# &#127759; Spring Batch Tests
서비스를 제공하는데 있어 중요한 파트라 볼 수 있는 테스트이다  
Spring Batch 역시 JUnit & Mock 을 통해 테스트 환경을 구축할 수 있다  
통합 테스트, 단위 테스트 별로 예제를 참고하며 공부해보자  

## 통합 테스트 

각 소스의 Annotation 은 무엇을 의미하는지  
Spring Batch 의 Transaction 과 SpringBoot MVC 의 Transaction 이 어떻게 다른지  
그리고 내친김에 EntityManager 와 Persistent Context 까지 중점적으로 살펴보며 공부하면 좋을 것 같다  

- [JobLauncherTestUtils 설정](../src/test/java/com/son/SpringBatch/BatchJobConfig.java)
- [Spring Batch Test](../src/test/java/com/son/SpringBatch/SpringBatchJobTests.java)


## 단위 테스트

사실상 @SpringBootTest 와 @SpringBatchTest Annotation 으로 Spring Batch 의 통합 및 단위 테스트의 여러 설정들을 퉁(?) 칠 수 있다  
- 테스트 환경 조성을 위한 빈 초기화 및 DI 설정, Datasource 주입 등  
- JobLauncherTestUtils 빈 의 주입 *  
- StepExecution 수행을 위한 Listener 클래스 등록 *  

세부 사항들은 테스트 코드 소스를 참고하며 공부하자  

- [Spring Batch Unit Test](../src/test/java/com/son/SpringBatch/SpringBatchJobUnitTests.java)