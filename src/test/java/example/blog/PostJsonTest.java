package example.blog;

import java.io.IOException;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest 
public class PostJsonTest {
    @Autowired 
    private JacksonTester<Post> json; 

    @Autowired 
    private JacksonTester<Post[]> jsonList; 

    private Post[] posts;

    @BeforeEach
    void setUp() {
        posts = Arrays.array(
            new Post(
                1L,
                "Test Post",
                "This is the Test Post Content",
                "This is the Post Category",
                "sarah1",
                "2021-09-01T12:00:00Z",
                "2021-09-01T12:00:00Z"
            ),
            new Post(
                2L,
                "Getting Started with GraphQL",
                "A comprehensive guide to building flexible APIs and querying data efficiently.",
                "Backend Development",
                "sarah1",
                "2022-04-10T08:15:00Z",
                "2022-04-12T14:30:00Z"
            ),
            new Post(
                3L,
                "Mastering UI Design Systems",
                "How to create consistent, scalable design tokens and components in modern web apps.",
                "UI/UX",
                "sarah1",
                "2023-01-22T17:45:00Z",
                "2023-01-22T17:45:00Z"
            )
        );
    }
    
    @Test 
    void postSerializationTest() throws IOException {
        Post post = new Post(
            1L, 
            "Test Post", 
            "This is the Test Post Content", 
            "This is the Post Category",
            "sarah1",
            "2021-09-01T12:00:00Z",
            "2021-09-01T12:00:00Z"
        ); 

        assertThat(json.write(post)).isStrictlyEqualToJson("expected.json");

        assertThat(json.write(post)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(post)).extractingJsonPathNumberValue("@.id").isEqualTo(
            1
        );

        assertThat(json.write(post)).hasJsonPathStringValue("@.title");
        assertThat(json.write(post)).extractingJsonPathStringValue("@.title").isEqualTo(
            "Test Post"
        );

        assertThat(json.write(post)).hasJsonPathStringValue("@.content");
        assertThat(json.write(post)).extractingJsonPathStringValue("@.content").isEqualTo(
            "This is the Test Post Content"
        );
        
        assertThat(json.write(post)).hasJsonPathStringValue("@.category");
        assertThat(json.write(post)).extractingJsonPathStringValue("@.category").isEqualTo(
            "This is the Post Category"
        );
        
        assertThat(json.write(post)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(post)).extractingJsonPathStringValue("@.createdAt").isEqualTo(
            "2021-09-01T12:00:00Z"
        );

        assertThat(json.write(post)).hasJsonPathStringValue("@.updatedAt");
        assertThat(json.write(post)).extractingJsonPathStringValue("@.updatedAt").isEqualTo(
            "2021-09-01T12:00:00Z"
        );
    }
    
    @Test 
    void postDeserializationTest() throws IOException {
        String expectedOutput = """
            {
                "id": 1,
                "title":"Test Post", 
                "content":"This is the Test Post Content",
                "category":"This is the Post Category",
                "owner": "sarah1",
                "createdAt": "2021-09-01T12:00:00Z",
                "updatedAt": "2021-09-01T12:00:00Z"
            }
        """;

        Post post = new Post(
            1L, 
            "Test Post", 
            "This is the Test Post Content", 
            "This is the Post Category",
            "sarah1",
            "2021-09-01T12:00:00Z",
            "2021-09-01T12:00:00Z"
        );

        assertThat(json.parse(expectedOutput)).isEqualTo(post);
    }

    @Test 
    void postListSerializationTest() throws IOException {
        assertThat(jsonList.write(posts)).isStrictlyEqualToJson("list.json");
    }

    @Test 
    void postListDeserializationTest() throws IOException {
        String expected = """
            [
                {
                    "id": 1,
                    "title": "Test Post",
                    "content": "This is the Test Post Content",
                    "category": "This is the Post Category",
                    "owner": "sarah1",
                    "createdAt": "2021-09-01T12:00:00Z",
                    "updatedAt": "2021-09-01T12:00:00Z"
                },
                {
                    "id": 2,
                    "title": "Getting Started with GraphQL",
                    "content": "A comprehensive guide to building flexible APIs and querying data efficiently.",
                    "category": "Backend Development",
                    "owner": "sarah1",
                    "createdAt": "2022-04-10T08:15:00Z",
                    "updatedAt": "2022-04-12T14:30:00Z"
                },
                {
                    "id": 3,
                    "title": "Mastering UI Design Systems",
                    "content": "How to create consistent, scalable design tokens and components in modern web apps.",
                    "category": "UI/UX",
                    "owner": "sarah1",
                    "createdAt": "2023-01-22T17:45:00Z",
                    "updatedAt": "2023-01-22T17:45:00Z"
                }
            ]    
        """;

        assertThat(jsonList.parse(expected)).isEqualTo(posts);
    }
}
