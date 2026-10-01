package example.blog;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController 
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;

    private PostController(PostRepository postRepository) {
        this.postRepository = postRepository; 
    }

    @GetMapping("/{requestedId}") 
    private ResponseEntity<Post> findById(@PathVariable Long requestedId) {
        Optional<Post> postOptional = postRepository.findById(requestedId);
        
        if (postOptional.isPresent()) {
            return ResponseEntity.ok(postOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping() 
    private ResponseEntity<Iterable<Post>> findAll() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    //UCB is inyected from Spring's IoC Container
    @PostMapping 
    private ResponseEntity<Void> createPost(@RequestBody Post newPost, UriComponentsBuilder ucb) {
        Post savedPost = postRepository.save(newPost);

        URI locationOfNewPost = ucb
            .path("posts/{id}")
            .buildAndExpand(savedPost.id())
            .toUri();

        return ResponseEntity.created(locationOfNewPost).build(); 
    }
}
