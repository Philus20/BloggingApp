package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;
import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Database.Services.CrudQueries;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.PostEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository implements Repository<PostEntity> {

    private final IConnection connectionFactory;
    private final ICrudQueries crudQueries;

    public PostRepository() {
        this.connectionFactory = new ConnectionFactory();
        this.crudQueries = new CrudQueries();
    }

    @Override
    public void create(PostEntity post) {
        String sql = crudQueries.createQuery("posts", "title, content, created_at, user_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(post.getCreatedAt()));
            statement.setInt(4, post.getUserId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setPostId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create post", e);
        }
    }

    @Override
    public PostEntity findByInteger(int id) {
        String sql = crudQueries.getByIntegerQuery(id, "posts", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToPost(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find post by ID", e);
        }
        return null;
    }

    @Override
    public PostEntity findByString(String identifier) {
        String sql = crudQueries.getStringQuery(identifier, "posts", "title");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToPost(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find post by title", e);
        }
        return null;
    }

    @Override
    public List<PostEntity> findAll() {
        String sql = crudQueries.getAllQuery("posts");
        List<PostEntity> posts = new ArrayList<>();
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                posts.add(mapResultSetToPost(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all posts", e);
        }
        return posts;
    }

    @Override
    public void updateById(int id) {
        String sql = crudQueries.updateByIdQuery(id, "posts", "title = ?, content = ?, user_id = ?", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "updated_title");
            statement.setString(2, "updated_content");
            statement.setInt(3, 1);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update post", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "posts", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    private PostEntity mapResultSetToPost(ResultSet resultSet) throws SQLException {
        PostEntity post = new PostEntity();
        post.setPostId(resultSet.getInt("post_id"));
        post.setTitle(resultSet.getString("title"));
        post.setContent(resultSet.getString("content"));
        post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        post.setUserId(resultSet.getInt("user_id"));
        return post;
    }
}
