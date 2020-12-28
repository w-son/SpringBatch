# Spring Batch
대용량 데이터 처리 작업의 자동화 및 복구 작업의 단위  
무거운 프로세스를 계획적으로 특정 시기에 독립적으로 구동해야하고 이에 대한 수정, 복구 및 재실행이 필요할 때 사용된다   


`Batch` 작업의 단위를 `JOB`이라 하고  
`JOB` 내에는 여러 세부 단계인 `STEP`이 존재하는데  
`STEP`은 `TASKLET` 혹은 `READER & PROCESSOR & WRITER`로 이루어져있다  

## Contents
- [Meta Data](docs/meta.md)
- [Steps on Conditions](docs/condition.md)
- [Job Scope, Step Scope, Parameters](docs/scope.md)
- [Chunk](docs/chunk.md)
- [ItemReader, ItemProcessor, ItemWriter](docs/item.md)
- [Relational Mapping](docs/jpa.md)
- [Spring Batch Tests](docs/test.md)