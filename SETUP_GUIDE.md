# Blogging App Setup Guide

This guide will help you set up and run the Blogging Application with advanced search functionality, caching, and optimization features.

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+** or use the included Maven wrapper
- **PostgreSQL 12+** (for database functionality)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

## Quick Start

### 1. Clone/Download the Project

```bash
git clone <repository-url>
cd BloggingApp
```

### 2. Database Setup

#### Option A: Using PostgreSQL (Recommended)

1. **Install PostgreSQL** if not already installed
2. **Create the database:**
   ```sql
   CREATE DATABASE blogging_db;
   ```
3. **Run the schema script:**
   ```bash
   psql -d blogging_db -f database_schema.sql
   ```

#### Option B: Using the Optimized Schema

For better performance with search and caching:

```bash
psql -d blogging_db -f database_schema_optimized.sql
```

### 3. Configure Database Connection

Edit the database configuration in your application properties:

```properties
# For PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/blogging_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build and Run the Application

#### Using Maven Wrapper (Recommended)

```bash
# Build the project
.\mvnw.cmd clean compile

# Run tests
.\mvnw.cmd test

# Run the application
.\mvnw.cmd javafx:run
```

#### Using System Maven

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn javafx:run
```

## Application Features

### Core Functionality
- **User Management**: Register, login, and manage user profiles
- **Blog Posts**: Create, read, update, and delete blog posts
- **Comments**: Add comments to posts with user attribution
- **Tagging System**: Organize posts with tags
- **Reviews**: Rate and review posts

### Advanced Features (Epic 3)
- **Advanced Search**: Multiple search algorithms (Hash, Binary, Hybrid)
- **Performance Optimization**: Caching and indexing strategies
- **Sorting**: Sort posts by various criteria (title, views, date)
- **Algorithm Comparison**: Compare performance of different search methods

## Running the Demo Applications

### 1. Main Application
```bash
.\mvnw.cmd javafx:run
```
This launches the main JavaFX application with full UI.

### 2. Search Optimization Demo
```bash
java -cp target/classes org.example.bloggingapp.Demo.SearchOptimizationDemo
```
This runs a console-based demo showcasing search features.

### 3. Advanced Search Demo
```bash
java -cp target/classes org.example.bloggingapp.AdvancedSearchMainApplication
```
This demonstrates the advanced search algorithms.

## Testing

### Run All Tests
```bash
.\mvnw.cmd test
```

### Run Specific Test Classes
```bash
# Advanced Search Service Tests
.\mvnw.cmd test -Dtest=AdvancedSearchServiceTest

# Cache Service Tests
.\mvnw.cmd test -Dtest=InMemoryCacheServiceTest

# Repository Tests
.\mvnw.cmd test -Dtest=PostServiceTest
```

### Test Coverage
The test suite covers:
- ✅ Search algorithms (Hash, Binary, QuickSort)
- ✅ Caching mechanisms
- ✅ Database operations
- ✅ Performance benchmarks
- ✅ Error handling and validation

## Project Structure

```
BloggingApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/bloggingapp/
│   │   │       ├── Models/          # Data models
│   │   │       ├── Services/        # Business logic
│   │   │       ├── Controllers/     # UI controllers
│   │   │       ├── Database/        # Database layer
│   │   │       ├── Utils/           # Utilities
│   │   │       └── Demo/            # Demo applications
│   │   └── resources/               # Configuration files
│   └── test/                        # Test files
├── database_schema.sql              # Basic database schema
├── database_schema_optimized.sql    # Optimized schema with indexes
├── pom.xml                          # Maven configuration
└── SETUP_GUIDE.md                   # This file
```

## Performance Features

### Search Algorithms
1. **Hash Search**: O(1) average time complexity for keyword searches
2. **Binary Search**: O(log n) for sorted title searches
3. **Hybrid Search**: Combines multiple algorithms for optimal results
4. **QuickSort**: O(n log n) sorting for various criteria

### Caching Strategy
- **In-Memory Cache**: Frequently accessed data cached in memory
- **Cache Hit/Miss Tracking**: Performance monitoring
- **Automatic Cache Invalidation**: Smart cache management

### Database Optimization
- **Comprehensive Indexes**: Optimized for search queries
- **Full-Text Search**: PostgreSQL GIN indexes for content search
- **Composite Indexes**: Multi-column query optimization

## Troubleshooting

### Common Issues

#### 1. Database Connection Errors
**Problem**: `Connection refused` or `database doesn't exist`
**Solution**: 
- Ensure PostgreSQL is running
- Verify database name and credentials
- Check if database was created correctly

#### 2. Java Version Issues
**Problem**: `Unsupported class file major version`
**Solution**: 
- Ensure you're using Java 21+
- Update JAVA_HOME environment variable

#### 3. Maven Build Failures
**Problem**: Compilation errors or missing dependencies
**Solution**: 
- Run `.\mvnw.cmd clean install`
- Check your internet connection for dependency downloads

#### 4. JavaFX Runtime Issues
**Problem**: JavaFX components not loading
**Solution**: 
- Ensure JavaFX dependencies are properly configured
- Try running with `.\mvnw.cmd javafx:run`

### Performance Tips

1. **Use the Optimized Schema**: The `database_schema_optimized.sql` includes performance indexes
2. **Enable Caching**: Ensure cache is properly initialized in your services
3. **Monitor Performance**: Use the built-in performance benchmarking tools

## Development

### Adding New Features

1. **Create Models**: Add new entity classes in `Models/`
2. **Implement Services**: Add business logic in `Services/`
3. **Add Controllers**: Create UI controllers in `Controllers/`
4. **Write Tests**: Add comprehensive tests in `src/test/`

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Write unit tests for all new functionality

## Support

For issues and questions:
1. Check this guide first
2. Review the test files for usage examples
3. Examine the demo applications for feature demonstrations
4. Check the JavaDoc comments in the source code

## License

This project is for educational purposes to demonstrate advanced Java programming concepts including search algorithms, caching strategies, and database optimization.
