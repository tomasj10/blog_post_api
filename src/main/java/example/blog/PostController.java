package example.blog;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private ResponseEntity<Post> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<Post> postOptional = Optional.ofNullable(
            postRepository.findByIdAndOwner(requestedId, principal.getName())
        );
        
        if (postOptional.isPresent()) {
            return ResponseEntity.ok(postOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping() 
    private ResponseEntity<Iterable<Post>> findAll(Pageable pageable, Principal principal) {
        Page<Post> page = postRepository.findByOwner(
            principal.getName(),
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "title"))
            )
        );

        return ResponseEntity.ok(page.getContent());
    }

    //UCB is inyected from Spring's IoC Container
    @PostMapping 
    private ResponseEntity<Void> createPost(
        @RequestBody Post newPost, 
        UriComponentsBuilder ucb,
        Principal principal
    ) {
        Post postWithOwner = new Post(
            null,newPost.title(),newPost.content(),newPost.category(),newPost.owner(),newPost.createdAt(),newPost.updatedAt()
        );
        
        Post savedPost = postRepository.save(postWithOwner);

        URI locationOfNewPost = ucb
            .path("posts/{id}")
            .buildAndExpand(savedPost.id())
            .toUri();

        return ResponseEntity.created(locationOfNewPost).build(); 
    }
}
