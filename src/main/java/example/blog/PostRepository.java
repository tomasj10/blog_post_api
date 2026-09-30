package example.blog;

import org.springframework.data.repository.CrudRepository;

interface PostRepository extends CrudRepository<Post, Long> {
    
}
