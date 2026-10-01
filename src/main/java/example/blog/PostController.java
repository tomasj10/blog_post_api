package example.blog;

import java.net.URI;
import java.security.Principal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    private Post findPostByIdAndOwner(Long requestedId, Principal principal) {
        return postRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @GetMapping("/{requestedId}") 
    private ResponseEntity<Post> findById(@PathVariable Long requestedId, Principal principal) {
        Post post = findPostByIdAndOwner(requestedId, principal);
        
        if (post != null) return ResponseEntity.ok(post);
        
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

    @PutMapping("/{updateId}")
    private ResponseEntity<Void> putPost(
        @PathVariable Long updateId, 
        @RequestBody Post postUpdate,
        Principal principal
    ) {
        Post post = findPostByIdAndOwner(updateId, principal);

        if (post == null) return ResponseEntity.notFound().build();

        Post updatedPost = new Post(post.id(),
            postUpdate.title() != null && !postUpdate.title().isBlank() ? postUpdate.title() : post.title(),
            postUpdate.content() != null && !postUpdate.content().isBlank() ? postUpdate.content() : post.content(),
            postUpdate.category() != null && !postUpdate.category().isBlank() ? postUpdate.category() : post.category(),
            principal.getName(), 
            postUpdate.createdAt() != null && !postUpdate.createdAt().isBlank() ? postUpdate.createdAt() : post.createdAt(), 
            postUpdate.updatedAt() != null && !postUpdate.updatedAt().isBlank() ? postUpdate.updatedAt() : post.updatedAt()
        );

        postRepository.save(updatedPost);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deletePost(
        @PathVariable Long requestedId,
        Principal principal
    ) {
        if (!postRepository.existsByIdAndOwner(requestedId, principal.getName())) return ResponseEntity.notFound().build();

        postRepository.deleteById(requestedId);

        return ResponseEntity.noContent().build(); 
    }
}
