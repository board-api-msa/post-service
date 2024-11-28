package me.junbyoung.PostService;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.junbyoung.PostService.model.Post;
import me.junbyoung.PostService.payload.PostRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, topics = {"post-events"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceApplicationTests {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private KafkaTemplate<String, Long> kafkaTemplate; // KafkaTemplate을 Mock 처리

	private Post newPost;

	@BeforeEach
	void setUp() {
		when(kafkaTemplate.send(eq("post-events"), anyLong()))
				.thenReturn(CompletableFuture.completedFuture(null));
	}

	@Test
	@Order(1)
	void addPost() throws Exception {
		PostRequest postRequest = new PostRequest("test", "test content");
		String jsonContent = objectMapper.writeValueAsString(postRequest);

		newPost = objectMapper.readValue(
				mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
								.header("X-User-Id", "1")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonContent))
						.andExpect(status().isCreated())
						.andReturn()
						.getResponse()
						.getContentAsString(),
				Post.class
		);
	}

	@Test
	@Order(2)
	void getPost() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}", newPost.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(newPost.getTitle()))
				.andExpect(jsonPath("$.content").value(newPost.getContent()));
	}

	@Test
	@Order(3)
	void updatePost() throws Exception {
		PostRequest postRequest = new PostRequest("test update", "content update");
		newPost.updatePost(postRequest.getTitle(), postRequest.getContent());
		String jsonContent = objectMapper.writeValueAsString(postRequest);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}", newPost.getId())
						.header("X-User-Id", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(newPost.getTitle()))
				.andExpect(jsonPath("$.content").value(newPost.getContent()));
	}

	@Test
	@Order(4) //게시글 소유자가 아닌 사용자가 게시글을 수정하려고할때의 테스트
	void updatePostByUnauthorizedUser() throws Exception {
		PostRequest postRequest = new PostRequest("test update", "content update");
		String jsonContent = objectMapper.writeValueAsString(postRequest);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}", newPost.getId())
						.header("X-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonContent))
				.andExpect(status().isForbidden());
	}

	@Test
	@Order(5) //게시글 소유자가 아닌 사용자가 게시글을 삭제하려고할때의 테스트
	void deletePostByUnauthorizedUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}", newPost.getId())
						.header("X-User-Id", "2"))
				.andExpect(status().isForbidden());
	}

	@Test
	@Order(6)
	void deletePost() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}", newPost.getId())
						.header("X-User-Id", "1"))
				.andExpect(status().isNoContent());

		verify(kafkaTemplate).send("post-events", newPost.getId()); // 카프카서버에 메시지 전송 여부 확인
	}
}