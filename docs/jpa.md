# &#127759; JPA Batch 에서의 Relational Mapping
JPA Batch 사용 시 여러 연관관계의 놓인 엔티티를 Reader 를 통해 읽어들이는 경우   
즉 One to Many 관계에 놓인 엔티티에서 One 을 기준으로 `1개의 연관된 Many 만` join fetch 로 가져올 수 있다는 제약이 있다  
이런 상황에서 LAZY 관계에 놓인 엔티티들을 그냥 get 메서드로 가져오게 된다면 `N + 1` 문제가 발생한다  


이를 해결하기 위해 application.properties 의 설정에  
hibernate 구현체의 default batch fetch size 를 적용해 줌으로써  
연관 관계에 놓인 인스턴스들을 IN 절로 가져올 수 있게끔 최적화 할 수 았다  


하지만 Item 을 읽어들이는 단위인 트랜잭션의 범위가 일반적으로 Chunk 인 다른 구현체들과는 달리  
JPA 를 구현체로 삼는 Job 들은 트랜잭션이 Reader 으로 한정되어 있어 IN 쿼리 사용이 불가능하다  
`HibernatePagingItemReader` 구현체를 사용하는 Job 을 구성하거나 `ItemReader 커스텀` 을 통해 문제를 해결할 수 있다  
관련된 내용을 [이 곳](https://jojoldu.tistory.com/414?category=902551) 에서 참고하였으니 구체적인 해결 방안은 해당 글을 참조할 수 있도록 하자    
문제가 되지 않는 상황인 하나의 자식 엔티티만 join fetch 로 조회하는 Job 을 구성한 코드는 다음을 참고하면 된다        

- [JpaSampleBatchConfig](../src/main/java/com/son/SpringBatch/config/JpaSampleBatchConfig.java)
