# &#127759; Chunk 
> Chunk 는 Spring Batch 의 트랜잭션 기본 단위이다  

예를 들어 Chunk 의 크기가 10 개의 row 라고 가정하면 10 개의 row 단위로 commit 및 rollback 이 실행된다는 의미이다  
Chunk 내에서는 `ItemReader`, `IteamProcessor`, `ItemWriter` 가 데이터를 읽기, 처리하기, 쓰기 를 진행하는데  
`ItemReader` 내에서는 읽어 들일 데이터를 Page 단위로 나눠 읽어 들일 수 있다  
Chunk 의 크기가 100 이고 Page 의 크기가 10 이면 하나의 Chunk 를 처리하기 위해 10 번의 조회가 필요한 셈이다  
성능 최적화를 위해 가능하면 Chunk 와 Page 의 크기를 같게 해서 조회 하는 것이 바람직하다고 한다  


추가적으로 Spring Batch 에서는 Chunk 단위로 트랜잭션이 이루어지기 때문에   
ItemProcessor 나 ItemWriter 에서 연관관계에 놓여 있는 프록시 객체들을 LAZY 로딩 가능하다  
예를 들어 일대다 관계에 놓여 있는 클래스 A 와 B 사이에서  
A 를 기준으로 LAZY 객체들인 B 들을 불러들일 수 있다는 의미이다    
