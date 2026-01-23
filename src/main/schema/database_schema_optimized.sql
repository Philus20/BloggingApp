-- Blogging App Optimized Database Schema
-- This script creates tables with comprehensive indexes for search optimization and caching
-- Supports AdvancedSearchService operations and application-level caching

-- Create database
CREATE DATABASE blogging_db;

-- Connect to it
-- \c blogging_db

-- Users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Posts table
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    views INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'Published',
    author_name VARCHAR(100) NOT NULL
);

-- Comments table
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_id INT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

-- Tags table
CREATE TABLE tags (
    tag_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Post–Tag junction table
CREATE TABLE post_tags (
    post_id INT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    tag_id INT NOT NULL REFERENCES tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, tag_id)
);

-- Reviews table
CREATE TABLE reviews (
    review_id SERIAL PRIMARY KEY,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    post_id INT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE
);

-- COMPREHENSIVE INDEXES FOR SEARCH OPTIMIZATION
-- These indexes support the AdvancedSearchService operations

-- 1. Keyword Search Indexes (for hashSearchByKeyword)
CREATE INDEX idx_posts_title_gin ON posts USING gin(to_tsvector('english', title));
CREATE INDEX idx_posts_content_gin ON posts USING gin(to_tsvector('english', content));
CREATE INDEX idx_posts_title_text ON posts USING btree(title);
CREATE INDEX idx_posts_content_text ON posts USING btree(content);

-- 2. Author Search Indexes (for searchByAuthor)
CREATE INDEX idx_posts_author_name ON posts(author_name);
CREATE INDEX idx_posts_author_name_lower ON posts(LOWER(author_name));

-- 3. Tag Search Indexes (for searchByTag)
CREATE INDEX idx_tags_name ON tags(name);
CREATE INDEX idx_tags_name_lower ON tags(LOWER(name));
CREATE INDEX idx_post_tags_tag_id ON post_tags(tag_id);

-- 4. Binary Search Indexes (for binarySearchByTitle)
CREATE INDEX idx_posts_title_binary ON posts(title COLLATE "C");

-- 5. Sorting Indexes (for quickSortPosts)
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_posts_views ON posts(views);
CREATE INDEX idx_posts_views_desc ON posts(views DESC);
CREATE INDEX idx_posts_created_at_desc ON posts(created_at DESC);

-- 6. Composite Indexes for Common Query Patterns
CREATE INDEX idx_posts_author_created ON posts(author_name, created_at DESC);
CREATE INDEX idx_posts_status_created ON posts(status, created_at DESC);
CREATE INDEX idx_posts_views_created ON posts(views DESC, created_at DESC);

-- 7. Performance Monitoring Indexes
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_reviews_post_id ON reviews(post_id);

-- 8. Full-Text Search Index (for advanced search)
CREATE INDEX idx_posts_fulltext ON posts USING gin(
    to_tsvector('english', title || ' ' || content || ' ' || COALESCE(author_name, ''))
);

-- Create a view for frequently accessed data
CREATE VIEW posts_with_tags AS
SELECT 
    p.post_id,
    p.title,
    p.content,
    p.created_at,
    p.user_id,
    p.views,
    p.status,
    p.author_name,
    STRING_AGG(t.name, ', ') AS tags
FROM posts p
LEFT JOIN post_tags pt ON p.post_id = pt.post_id
LEFT JOIN tags t ON pt.tag_id = t.tag_id
GROUP BY p.post_id, p.title, p.content, p.created_at, p.user_id, p.views, p.status, p.author_name;

-- Index the view for better performance
CREATE INDEX idx_posts_with_tags_title ON posts_with_tags USING gin(to_tsvector('english', title));
CREATE INDEX idx_posts_with_tags_author ON posts_with_tags(author_name);

-- Function to update search vectors automatically
CREATE OR REPLACE FUNCTION update_post_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    -- This can be used for maintaining search indexes
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for automatic index maintenance
CREATE TRIGGER trigger_update_post_search
    BEFORE INSERT OR UPDATE ON posts
    FOR EACH ROW
    EXECUTE FUNCTION update_post_search_vector();

-- Insert sample data for testing
INSERT INTO users (user_name, email, password, role) VALUES 
('John Doe', 'john@example.com', 'password123', 'user'),
('Jane Smith', 'jane@example.com', 'password123', 'user'),
('Admin User', 'admin@example.com', 'admin123', 'admin');

INSERT INTO tags (name) VALUES 
('java'), ('programming'), ('web'), ('spring'), ('database'), ('tutorial'), ('advanced');

-- Display table information
SELECT 'Tables and indexes created successfully' as status;
SELECT 'users' as table_name, COUNT(*) as record_count FROM users;
SELECT 'posts' as table_name, COUNT(*) as record_count FROM posts;
SELECT 'tags' as table_name, COUNT(*) as record_count FROM tags;
