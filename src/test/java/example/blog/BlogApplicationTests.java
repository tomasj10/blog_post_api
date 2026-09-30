package example.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List; 

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogApplicationTests {

	@LocalServerPort
    private int port;

	private RestClient restClient; 

	@BeforeEach 
	void setUp() {
		this.restClient = RestClient.create("http://localhost:" + port);
	}

	@Test
	void shouldReturnAPostWhenDataIsSaved() {
		ResponseEntity<String> response = this.restClient.get()
			.uri("/posts/99")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).
			isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();
		assertThat(id).isEqualTo(1);

		String title = documentContext.read("$.title");
		assertThat(title).isNotNull(); 
		assertThat(title).isEqualTo("Test Post");

		String content = documentContext.read("$.content");
		assertThat(content).isNotNull(); 
		assertThat(content).isEqualTo("This is the Test Post Content");

		String category = documentContext.read("$.category");
		assertThat(category).isNotNull(); 
		assertThat(category).isEqualTo("This is the Post Category");
		
		List<String> tags = documentContext.read("$.tags");
		assertThat(tags).isNotEmpty(); 
		assertThat(tags).containsExactly("This is the 1st Post Tag", "This is the 2nd Post Tag");

		String createdAt = documentContext.read("$.createdAt");
		assertThat(createdAt).isNotNull(); 
		assertThat(createdAt).isEqualTo("2021-09-01T12:00:00Z");

		String updatedAt = documentContext.read("$.updatedAt");
		assertThat(updatedAt).isNotNull(); 
		assertThat(updatedAt).isEqualTo("2021-09-01T12:00:00Z");
	}
}
