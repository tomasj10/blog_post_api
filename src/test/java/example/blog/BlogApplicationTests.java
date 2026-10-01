package example.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogApplicationTests {

	@LocalServerPort
    private int port;

	private RestClient restClient; 

	@BeforeEach 
	void setUp() {
		this.restClient = RestClient.builder()
		.defaultHeaders(headers -> headers.setBasicAuth("sarah1", "abc123"))
		.baseUrl("http://localhost:" + port)
		.defaultStatusHandler(status -> status.isError(), (request, response) -> {
			// Ignore exceptions to assert any HTTP status code in tests.  
        })
		.build();
	}

	@Test
	void shouldReturnAPostWhenDataIsSaved() {
		ResponseEntity<String> response = this.restClient.get()
			.uri("/posts/1")
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

		String createdAt = documentContext.read("$.createdAt");
		assertThat(createdAt).isNotNull(); 
		assertThat(createdAt).isEqualTo("2021-09-01T12:00:00Z");

		String updatedAt = documentContext.read("$.updatedAt");
		assertThat(updatedAt).isNotNull(); 
		assertThat(updatedAt).isEqualTo("2021-09-01T12:00:00Z");
	}
	
	@Test 
	void shouldNotReturnAPostWithAnUnknownId() {
		ResponseEntity<String> response = restClient.get()
			.uri("/posts/9999")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test 
	@DirtiesContext 
	void shouldCreateANewPost() {
		// If we don't insert ID in our data.sql, h2 will automaticaly assign 1 to the 1st post created, and the next post (this test) will be 2 always.

		Post newPost = new Post(
			null, 
			"2nd post", 
			"This is the 2nd Post content", 
			"This is another category",
			"sarah1", 
			"982837372839", 
			"982837372839"
		);

		ResponseEntity<Void> createReponse = restClient.post()
			.uri("/posts")
			.body(newPost)
			.retrieve()
			.toBodilessEntity();

		assertThat(createReponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);	 

		URI locationOfNewPost = createReponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restClient.get()
			.uri(locationOfNewPost)
			.retrieve()
			.toEntity(String.class);
		
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");

		assertThat(id).isNotNull();

		String title = documentContext.read("$.title");
		assertThat(title).isNotNull(); 
		assertThat(title).isEqualTo("2nd post");

		String content = documentContext.read("$.content");
		assertThat(content).isNotNull(); 
		assertThat(content).isEqualTo("This is the 2nd Post content");

		String category = documentContext.read("$.category");
		assertThat(category).isNotNull(); 
		assertThat(category).isEqualTo("This is another category");

		String createdAt = documentContext.read("$.createdAt");
		assertThat(createdAt).isNotNull(); 
		assertThat(createdAt).isEqualTo("982837372839");

		String updatedAt = documentContext.read("$.updatedAt");
		assertThat(updatedAt).isNotNull(); 
		assertThat(updatedAt).isEqualTo("982837372839");
	}

	@Test 
	void shouldReturnAllPostsWhenListIsrequested() {
		ResponseEntity<String> response = restClient.get()
			.uri("/posts")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int postCount = documentContext.read("$.length()");
		assertThat(postCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(
			1,
			2,
			3
		);

		JSONArray titles = documentContext.read("$..title");
		assertThat(titles).containsExactlyInAnyOrder(
			"Test Post",
			"Getting Started with GraphQL",
			"Mastering UI Design Systems"
		);

		JSONArray contents = documentContext.read("$..content");
		assertThat(contents).containsExactlyInAnyOrder(
			"This is the Test Post Content",
			"A comprehensive guide to building flexible APIs and querying data efficiently.",
			"How to create consistent, scalable design tokens and components in modern web apps."
		);

		JSONArray categories = documentContext.read("$..category");
		assertThat(categories).containsExactlyInAnyOrder(
			"This is the Post Category",
			"Backend Development",
			"UI/UX"
		);

		JSONArray createdsAt = documentContext.read("$..createdAt");
		assertThat(createdsAt).containsExactlyInAnyOrder(
			"2021-09-01T12:00:00Z",
			"2022-04-10T08:15:00Z",
			"2023-01-22T17:45:00Z"
		);

		JSONArray updatedsAt = documentContext.read("$..updatedAt");
		assertThat(updatedsAt).containsExactlyInAnyOrder(
			"2021-09-01T12:00:00Z",
			"2022-04-12T14:30:00Z",
			"2023-01-22T17:45:00Z"
		);
	}

	@Test 
	void shouldReturnAPageOfPosts() {
		ResponseEntity<String> response = restClient
			.get()
			.uri("/posts?page=0&size=1")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test 
	void shouldReturnASortedPageOfPosts() {
		ResponseEntity<String> response = restClient
			.get()
			.uri("/posts?page=0&size=1&sort=title,asc")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);

		String firstTitle = documentContext.read("$[0].title");
		assertThat(firstTitle).isEqualTo("Getting Started with GraphQL");
	}

	@Test 
	void shouldReturnAPageOfPostsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restClient
			.get()
			.uri("/posts")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(3);

		JSONArray titles = documentContext.read("$..title");
		assertThat(titles).containsExactly(
			"Test Post",
			"Mastering UI Design Systems",
			"Getting Started with GraphQL"
		);
	}

	@Test
	void shouldNotReturnAPostWhenUsingBadCredentials() {
		ResponseEntity<String> response = restClient.get()
			.uri("/posts/2")
			.headers(headers -> headers.setBasicAuth("sarah1", "BAD-PASSWORD"))
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restClient.get()
			.uri("/posts/2")
			.headers(headers -> headers.setBasicAuth("hank-owns-no-posts", "qrs456"))
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
