-- Blogging App Database Schema
-- This script creates the necessary tables for the tagging system

-- Create tags table if it doesn't exist
CREATE TABLE IF NOT EXISTS tags (
    tag_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create post_tags table if it doesn't exist
CREATE TABLE IF NOT EXISTS post_tags (
    post_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);
CREATE INDEX IF NOT EXISTS idx_post_tags_post_id ON post_tags(post_id);
CREATE INDEX IF NOT EXISTS idx_post_tags_tag_id ON post_tags(tag_id);

-- Insert some sample tags (optional)
INSERT INTO tags (name) VALUES 
('javafx'), 
('socialmedia'), 
('webdev'), 
('programming'), 
('database'), 
('ui'), 
('performance')
ON CONFLICT (name) DO NOTHING;

-- Display table information
SELECT 'Tables created successfully' as status;
SELECT 'tags' as table_name, COUNT(*) as record_count FROM tags;
SELECT 'post_tags' as table_name, COUNT(*) as record_count FROM post_tags;
