package example.blog;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface PostRepository extends 
    CrudRepository<Post, Long>, 
    PagingAndSortingRepository<Post, Long> 
{    
    
}
