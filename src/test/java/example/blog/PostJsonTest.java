package example.blog;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest 
public class PostJsonTest {
    @Autowired 
    private JacksonTester<Post> json; 
    
    @Test 
    void postSerializationTest() throws IOException {
        Post post = new Post(
            1L, 
            "Test Post", 
            "This is the Test Post Content", 
            "This is the Post Category",
            List.of("This is the 1st Post Tag", "This is the 2nd Post Tag"),
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
        
        assertThat(json.write(post)).hasJsonPathArrayValue("@.tags");
        assertThat(json.write(post)).extractingJsonPathArrayValue("@.tags").containsExactly(
            "This is the 1st Post Tag", 
            "This is the 2nd Post Tag"
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
                "tags": [
                    "This is the 1st Post Tag",
                    "This is the 2nd Post Tag"
                ],
                "createdAt": "2021-09-01T12:00:00Z",
                "updatedAt": "2021-09-01T12:00:00Z"
            }
        """;

        Post post = new Post(
            1L, 
            "Test Post", 
            "This is the Test Post Content", 
            "This is the Post Category",
            List.of(
                "This is the 1st Post Tag",
                "This is the 2nd Post Tag"
            ),
            "2021-09-01T12:00:00Z",
            "2021-09-01T12:00:00Z"
        );

        assertThat(json.parse(expectedOutput)).isEqualTo(post);
    }
}
