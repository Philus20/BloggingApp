# Blogging App Dashboard

A comprehensive JavaFX dashboard for managing posts, viewing performance metrics, and performing cache optimizations.

## Features

### 📊 Performance Metrics Tab
- **Real-time Statistics**: Total posts, users, cache hit rate, cache size, and eviction count
- **Interactive Charts**: Live performance charts showing hit rates and response times over time
- **Detailed Metrics**: Comprehensive cache statistics for both posts and users

### 📝 Posts Management Tab
- **CRUD Operations**: Create, read, update, and delete posts
- **Search Functionality**: Real-time search through posts by title and content
- **Data Validation**: Input validation for all post operations
- **Cache Integration**: All operations leverage the in-memory caching system

### 🗂️ Cache Management Tab
- **Cache Control**: Clear individual caches or all caches at once
- **Configuration**: Adjust cache size and expiration times
- **Status Monitoring**: Real-time cache status and health information
- **Maintenance Operations**: Cleanup expired entries and force refreshes

### ⚡ Optimization Tab
- **Performance Analysis**: Automated analysis with recommendations
- **Cache Optimization**: Intelligent cache tuning based on usage patterns
- **Data Preloading**: Preload frequently accessed data for better performance
- **Cache Warmup**: Warm up cache with simulated access patterns

## Architecture

### Core Components

1. **DashboardController**: Main UI controller managing all dashboard interactions
2. **PerformanceMonitor**: Tracks and analyzes cache performance metrics
3. **CacheOptimizationService**: Provides intelligent cache optimization features
4. **CacheManager**: Centralized cache management with automatic cleanup

### Integration with Caching System

The dashboard seamlessly integrates with the existing caching infrastructure:

- **PostService**: Enhanced with caching for fast post retrieval
- **UserService**: Enhanced with caching for fast user operations
- **Cache Statistics**: Real-time monitoring of hit rates, evictions, and performance
- **Automatic Optimization**: Background optimization based on usage patterns

## Getting Started

### Prerequisites

- Java 21 or higher
- JavaFX 21 or higher
- Maven for dependency management

### Running the Dashboard

1. **Build the project**:
   ```bash
   mvn clean compile
   ```

2. **Run the dashboard**:
   ```bash
   mvn javafx:run -Djavafx.mainClass=org.example.bloggingapp.Dashboard.DashboardApp
   ```

### Alternative: Run from IDE

1. Open `DashboardApp.java` in your IDE
2. Run the main method
3. The dashboard will open in a new window

## Usage Guide

### Posts Management

1. **Creating Posts**:
   - Enter title, content, and user ID
   - Click "Create Post"
   - The post is automatically cached

2. **Updating Posts**:
   - Select a post from the table
   - Modify the fields
   - Click "Update Post"

3. **Deleting Posts**:
   - Select a post from the table
   - Click "Delete Post"
   - Confirm the deletion

4. **Searching Posts**:
   - Enter search terms in the search field
   - Results update automatically

### Performance Monitoring

1. **View Real-time Metrics**:
   - Navigate to the Performance Metrics tab
   - Metrics update every 5 seconds automatically

2. **Monitor Charts**:
   - Hit rate chart shows cache effectiveness over time
   - Response time chart shows performance trends

3. **Detailed Statistics**:
   - Check the metrics text area for detailed cache statistics

### Cache Management

1. **Clear Caches**:
   - Use individual cache clear buttons for targeted cleanup
   - Use "Clear All Cache" for complete cache reset

2. **Configure Cache**:
   - Adjust cache size with the slider
   - Set expiration time with the dropdown
   - Click "Apply Configuration"

3. **Monitor Status**:
   - Check the cache status area for real-time information
   - Use "Force Refresh" to update status immediately

### Optimization

1. **Analyze Performance**:
   - Click "Analyze Performance"
   - Review recommendations in the log area

2. **Optimize Cache**:
   - Click "Optimize Cache" for automatic tuning
   - Monitor progress with the progress bar

3. **Preload Data**:
   - Click "Preload Data" to load frequently accessed data
   - Improves initial performance after startup

4. **Warmup Cache**:
   - Click "Warmup Cache" to simulate access patterns
   - Prepares cache for typical usage

## Performance Features

### Automatic Optimization

The dashboard includes automatic optimization that runs every 5 minutes:

- **Cache Cleanup**: Removes expired entries
- **Performance Snapshots**: Records performance history
- **Intelligent Tuning**: Adjusts cache parameters based on usage

### Performance Monitoring

- **Hit Rate Tracking**: Monitors cache effectiveness
- **Response Time Analysis**: Tracks operation performance
- **Eviction Monitoring**: Tracks cache eviction patterns
- **Historical Data**: Maintains performance history for analysis

### Cache Strategies

- **LRU Eviction**: Least Recently Used eviction policy
- **Time-based Expiration**: Configurable expiration times
- **Multi-level Caching**: Different caches for different data types
- **Smart Invalidation**: Intelligent cache invalidation on data changes

## Configuration

### Cache Settings

Default cache configurations:

- **Post Cache**: 500 entries, 10 minutes expiration
- **Post by Title Cache**: 200 entries, 15 minutes expiration
- **User Posts Cache**: 100 entries, 5 minutes expiration
- **All Posts Cache**: 10 entries, 2 minutes expiration
- **User Cache**: 1000 entries, 15 minutes expiration
- **User by Email Cache**: 500 entries, 20 minutes expiration
- **User by Username Cache**: 500 entries, 20 minutes expiration

### Performance Settings

- **Metrics Update Interval**: 5 seconds
- **Automatic Cleanup Interval**: 1 minute
- **Automatic Optimization Interval**: 5 minutes
- **Performance History Size**: 100 snapshots

## Troubleshooting

### Common Issues

1. **Dashboard Not Starting**:
   - Check JavaFX installation
   - Verify Java version (21+)
   - Check Maven dependencies

2. **Cache Not Working**:
   - Ensure cache manager is started
   - Check cache configuration
   - Verify service integration

3. **Performance Issues**:
   - Check cache hit rates
   - Review optimization recommendations
   - Consider increasing cache size

4. **Memory Issues**:
   - Monitor cache sizes
   - Adjust expiration times
   - Use cleanup operations

### Debug Information

Enable debug logging by checking the log areas in:
- Performance Metrics tab (detailed metrics)
- Cache Management tab (cache status)
- Optimization tab (operation logs)

## Development

### Extending the Dashboard

1. **Add New Metrics**:
   - Extend `PerformanceMonitor`
   - Update `DashboardController`
   - Modify FXML views

2. **Add New Optimizations**:
   - Extend `CacheOptimizationService`
   - Add new optimization methods
   - Update UI controls

3. **Add New Cache Types**:
   - Create new cache instances
   - Update service integration
   - Add monitoring support

### Architecture Patterns

- **MVC Pattern**: Model-View-Controller architecture
- **Observer Pattern**: Real-time UI updates
- **Strategy Pattern**: Different optimization strategies
- **Singleton Pattern**: Cache manager and performance monitor

## License

This dashboard is part of the Blogging App project and follows the same licensing terms.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the log outputs
3. Verify cache configurations
4. Check service integrations
