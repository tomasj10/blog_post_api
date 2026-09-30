package example.blog;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/posts")
public class PostController {

    @GetMapping("/{requestedId}") 
    private ResponseEntity<Post> findById() {
         Post post = new Post(
            1L, 
            "Test Post", 
            "This is the Test Post Content", 
            "This is the Post Category",
            List.of("This is the 1st Post Tag", "This is the 2nd Post Tag"),
            "2021-09-01T12:00:00Z",
            "2021-09-01T12:00:00Z"
        ); 

        return ResponseEntity.ok(post);
    }
}
