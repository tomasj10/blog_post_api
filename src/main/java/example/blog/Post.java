package example.blog;


import org.springframework.data.annotation.Id;

public record Post(
    @Id Long id,
    String title, 
    String content, 
    String category,
    String owner,
    String createdAt, 
    String updatedAt
) {}
