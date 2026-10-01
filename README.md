# Blogging API
## Challenge source
In [Roadmap Backend Projects: Building a Blogging API](https://roadmap.sh/projects/blogging-platform-api)

## Project Stack
Java 21, Apache Maven, Spring Boot 4.1.1, JUnit 5. Following the [Building a REST API with Spring Boot](https://spring.academy/courses/building-a-rest-api-with-spring-boot) course.

## Project Documentation
### API Contract

#### Create Blog Post

Create a new blog post using the POST method
```
plaintext

POST /posts
{
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"]
}
```
Each blog post should have the following fields:
```
json

{
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"]
}
```
The endpoint should validate the request body and return a `201 Created` status code with the newly created blog post i.e.

```
json

{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:00:00Z"
}
```
or a `400 Bad Request` status code with error messages in case of validation errors.

#### Update Blog Post
Update an existing blog post using the PUT method
```
plaintext

PUT /posts/1
{
  "title": "My Updated Blog Post",
  "content": "This is the updated content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"]
}
```
The endpoint should validate the request body and return a `200 OK` status code with the updated blog post i.e.
```
json

{
  "id": 1,
  "title": "My Updated Blog Post",
  "content": "This is the updated content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:30:00Z"
}
```
or a `400 Bad Request` status code with error messages in case of validation errors. It should return a `404 Not Found` status code if the blog post was not found.
#### Delete Blog Post
Delete an existing blog post using the `DELETE` method
```
plaintext

DELETE /posts/1
``` 
The endpoint should return a `204 No Content` status code if the blog post was successfully deleted or a `404 Not Found` status code if the blog post was not found.
#### Get Blog Post
Get a single blog post using the `GET` method
```
plaintext

GET /posts/1
```
The endpoint should return a `200 OK` status code with the blog post i.e.
```
json

{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:00:00Z"
}
```
or a `404 Not Found` status code if the blog post was not found.
#### Get All Blog Posts
Get all blog posts using the `GET` method
```
plaintext

GET /posts
```
The endpoint should return a `200 OK` status code with an array of blog posts i.e.
```
json

[
  {
    "id": 1,
    "title": "My First Blog Post",
    "content": "This is the content of my first blog post.",
    "category": "Technology",
    "tags": ["Tech", "Programming"],
    "createdAt": "2021-09-01T12:00:00Z",
    "updatedAt": "2021-09-01T12:00:00Z"
  },
  {
    "id": 2,
    "title": "My Second Blog Post",
    "content": "This is the content of my second blog post.",
    "category": "Technology",
    "tags": ["Tech", "Programming"],
    "createdAt": "2021-09-01T12:30:00Z",
    "updatedAt": "2021-09-01T12:30:00Z"
  }
]
```
You don't have to implement pagination, authentication or authorization for this project. You can focus on the core functionality of the API.

While retrieving posts, user can also filter posts by a search term. You should do a wildcard search on the title, content or category fields of the blog posts. For example:
```
plaintext

GET /posts?term=tech
```
This should return all blog posts that have the term "tech" in their title, content or category. You can use a simple SQL query if you are using a SQL database or a similar query for a NoSQL database.

### Additional Notes
I deleted the use of tags, this is a test project to just learn throughout the course, and working with lists in the database is a later feature, for now, we will forget tags.   
