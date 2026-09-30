package example.blog;

import java.util.List;

public record Post(
    Long id,
    String title, 
    String content, 
    String category,
    List<String> tags,
    String createdAt, 
    String updatedAt
) {}
