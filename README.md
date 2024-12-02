# **Post Service**

Post Service는 게시글 등록, 수정, 삭제, 조회를 처리합니다.   
다른 마이크로서비스와의 통신을 위해 OpenFeign을 사용하며, CircuitBreaker와 OpenFeign Fallback을 설정하여 시스템의 안정성을 향상시켰습니다.

## **기술스택**

- **Java 21**
- **Spring boot**
- **Spring Cloud Netflix Eureka Client**
- **Spring Cloud Config Client**
- **Spring Cloud OpenFeign**
- **Apache Kafka**
- **Resilience4j CircuitBreaker**


## **API 엔드포인트**

### **게시글 등록**

- **URI**: `POST /api/posts`
- **요청 헤더**: 

    ```http
    Authorization: Bearer <JWT 토큰>
- **요청 본문**:

  ```json
  {
    "title": "title",
    "content": "content"
  }
 

### **게시글 수정**

- **URI**: `PUT /api/posts/{postId}`
- **요청 헤더**: 

    ```http
    Authorization: Bearer <JWT 토큰>
- **요청 본문**:

  ```json
  {
    "title": "title",
    "content": "content"
  }

### **게시글 조회**

- **URI**: `GET /api/posts/{postId}`
- **응답 본문**:

  ```json
  {
    "id": 1,
    "userName": "user",
    "title": "title",
    "content": "content",
    "comments": []
  }

### **게시글 삭제**

- **URI**: `DELETE /api/posts/{postId}`
- **요청 헤더**: 

    ```http
    Authorization: Bearer <JWT 토큰>
- 게시글 삭제 요청시, 카프카서버로 메시지를 보냅니다. 해당 메시지를 Comment Service가 구독하여 처리합니다.