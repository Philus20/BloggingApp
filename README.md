# BloggingApp - JavaFX Blogging Application

A comprehensive blogging application built with JavaFX, PostgreSQL, and performance optimization features.

## Features

### Epic 2: Data Access and CRUD Operations
- **Full CRUD Operations**: Create, Read, Update, Delete posts and comments through JavaFX interface
- **Input Validation**: Real-time validation with user feedback messages
- **Database Constraints**: Schema validation prevents duplicate and invalid entries
- **Secure Database Operations**: Parameterized queries prevent SQL injection

### Epic 3: Searching, Sorting, and Optimization
- **Case-Insensitive Search**: Fast, responsive search functionality
- **Performance Optimization**: In-memory caching layer for improved query performance
- **Sorting Algorithms**: Multiple sorting options (date, views, title)
- **Cache Invalidation**: Automatic cache consistency after updates

### Epic 4: Performance and Query Optimization
- **Performance Monitoring**: Real-time query execution tracking
- **Performance Reports**: Before/after optimization comparisons
- **Measurable Improvements**: Documented performance gains
- **NoSQL Support**: Flexible comment storage option

### Epic 5: Reporting and Documentation
- **Comprehensive Documentation**: Complete setup and usage instructions
- **Database Schema**: Clear ERD documentation
- **Performance Metrics**: Detailed performance analysis tools

## Architecture

### Database Layer
- **PostgreSQL**: Primary relational database
- **MongoDB**: Optional NoSQL for comments/reviews
- **Connection Factory**: Centralized database connection management
- **Repository Pattern**: Clean data access abstraction

### Caching System
- **PostCache**: Singleton pattern for thread-safe caching
- **5-minute TTL**: Automatic cache expiration
- **Multi-level Caching**: Posts, comments, and search results
- **Performance Monitoring**: Integrated cache hit/miss tracking

### Performance Monitoring
- **Query Timing**: Millisecond-precision execution tracking
- **Statistical Analysis**: Mean, median, min/max query times
- **Before/After Comparison**: Optimization impact measurement
- **Real-time Reporting**: Live performance statistics

## Setup Instructions

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Maven 3.6+
- JavaFX 21

### Database Setup

1. **Create PostgreSQL Database**:
```sql
CREATE DATABASE blogging_db;
```

2. **Create Tables**:
```sql
-- Users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Posts table
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER REFERENCES users(user_id),
    status VARCHAR(20) DEFAULT 'Draft',
    views INTEGER DEFAULT 0
);

-- Comments table
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    post_id INTEGER REFERENCES posts(post_id),
    user_id INTEGER REFERENCES users(user_id)
);

-- Create indexes for performance
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_created_at ON comments(created_at DESC);
```

### Application Configuration

1. **Update Database Connection**:
   - Modify connection details in `ConnectionFactory.java`
   - Default: `localhost:5432/blogging_db`

2. **Build Application**:
```bash
mvn clean compile
```

3. **Run Application**:
```bash
mvn javafx:run
```

## Usage Guide

### Main Dashboard
- **View Posts**: Paginated list of all blog posts
- **Search**: Real-time search with caching
- **Sort**: By date, views, or title
- **CRUD Operations**: Create, edit, delete posts
- **Performance Metrics**: View cache statistics

### Post Management
- **Create Post**: Rich text editor with validation
- **Edit Post**: Modify existing posts
- **Delete Post**: Confirmation-based deletion
- **Status Management**: Draft/Published states

### Comment System
- **View Comments**: Threaded comment display
- **Add Comments**: Real-time comment posting
- **Comment Validation**: Length and content validation
- **Performance**: Cached comment loading

## Performance Features

### Caching Strategy
- **Posts Cache**: Reduces database queries for frequently accessed posts
- **Search Cache**: Stores search results for 5 minutes
- **Comment Cache**: Per-post comment caching
- **Cache Statistics**: Real-time cache performance metrics

### Performance Monitoring
- **Query Tracking**: All database operations timed
- **Statistical Analysis**: Average, median, min/max execution times
- **Performance Reports**: Before/after optimization comparisons
- **Cache Hit Rates**: Monitor caching effectiveness

## Database Schema

### Entity Relationship Diagram
```
Users (1) -----> (N) Posts (1) -----> (N) Comments
```

### Tables Description

#### Users
- `user_id`: Primary key
- `user_name`: Unique username
- `email`: Unique email address
- `password_hash`: Encrypted password
- `created_at`: Account creation timestamp

#### Posts
- `post_id`: Primary key
- `title`: Post title (max 200 chars)
- `content`: Post content (max 10,000 chars)
- `created_at`: Creation timestamp
- `user_id`: Foreign key to users
- `status`: Draft/Published
- `views`: View counter

#### Comments
- `comment_id`: Primary key
- `content`: Comment text (max 1,000 chars)
- `created_at`: Creation timestamp
- `post_id`: Foreign key to posts
- `user_id`: Foreign key to users

## Performance Optimization

### Indexes
- `idx_posts_created_at`: Optimizes post ordering by date
- `idx_posts_user_id`: Optimizes user-specific queries
- `idx_comments_post_id`: Optimizes comment retrieval
- `idx_comments_created_at`: Optimizes comment ordering

### Query Optimization
- **Parameterized Queries**: Prevents SQL injection
- **Connection Pooling**: Efficient connection management
- **Batch Operations**: Reduced database round trips
- **Prepared Statements**: Query plan caching

## Testing

### Unit Tests
```bash
mvn test
```

### Performance Tests
- Query execution time measurement
- Cache hit/miss ratio testing
- Load testing for concurrent users
- Memory usage profiling

## Security Features

### Input Validation
- **Title Validation**: 3-200 characters required
- **Content Validation**: 10-10,000 characters required
- **Comment Validation**: 3-1,000 characters required
- **SQL Injection Prevention**: Parameterized queries

### Data Protection
- **Password Hashing**: Secure password storage
- **Input Sanitization**: XSS prevention
- **Error Handling**: Graceful error responses
- **Transaction Management**: Data consistency

## Monitoring and Maintenance

### Performance Monitoring
- **Real-time Metrics**: Query execution times
- **Cache Statistics**: Hit rates and sizes
- **Error Tracking**: Exception logging
- **Resource Usage**: Memory and connection monitoring

### Maintenance Tasks
- **Cache Cleanup**: Automatic expired entry removal
- **Database Optimization**: Regular index rebuilding
- **Log Rotation**: Manage log file sizes
- **Performance Reports**: Regular analysis generation

## Troubleshooting

### Common Issues

1. **Database Connection Failed**:
   - Check PostgreSQL service status
   - Verify connection parameters
   - Ensure database exists

2. **Slow Performance**:
   - Check cache hit rates
   - Verify indexes exist
   - Monitor query execution times

3. **Memory Issues**:
   - Monitor cache sizes
   - Check for memory leaks
   - Adjust cache TTL settings

### Debug Mode
Enable debug logging by setting:
```java
System.setProperty("bloggingapp.debug", "true");
```

## Future Enhancements

### Planned Features
- **User Authentication**: Complete login system
- **Tag System**: Post categorization
- **File Uploads**: Image and media support
- **Email Notifications**: Comment and post alerts
- **API Integration**: REST API endpoints
- **Mobile Support**: Responsive design improvements

### Performance Roadmap
- **Distributed Caching**: Redis integration
- **Database Sharding**: Horizontal scaling
- **Query Optimization**: Advanced indexing strategies
- **Load Balancing**: Multiple database instances

## Contributing

### Development Setup
1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Update documentation
5. Submit pull request

### Code Standards
- Follow Java naming conventions
- Include unit tests
- Update documentation
- Performance test new features

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For issues and questions:
- Check troubleshooting section
- Review performance metrics
- Examine application logs
- Contact development team
