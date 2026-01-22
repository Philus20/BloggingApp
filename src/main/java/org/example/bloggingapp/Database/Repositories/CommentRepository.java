package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;
import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Services.CrudQueries;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.CommentEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository implements Repository<CommentEntity> {

    private final IConnection connectionFactory;
    private final ICrudQueries crudQueries;

    public CommentRepository() {
        this.connectionFactory = new ConnectionFactory();
        this.crudQueries = new CrudQueries();
    }

    @Override
    public void create(CommentEntity comment) {
        String sql = crudQueries.createQuery("comments", "content, created_at, post_id, user_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, comment.getContent());
            statement.setTimestamp(2, Timestamp.valueOf(comment.getCreatedAt()));
            statement.setInt(3, comment.getPostId());
            statement.setInt(4, comment.getUserId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create comment", e);
        }
    }

    @Override
    public CommentEntity findByInteger(int id) {
        String sql = crudQueries.getByIntegerQuery(id, "comments", "comment_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToComment(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comment by ID", e);
        }
        return null;
    }

    @Override
    public CommentEntity findByString(String identifier) {
        String sql = crudQueries.getStringQuery(identifier, "comments", "content");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToComment(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comment by content", e);
        }
        return null;
    }

    @Override
    public List<CommentEntity> findAll() {
        String sql = crudQueries.getAllQuery("comments");
        List<CommentEntity> comments = new ArrayList<>();
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                comments.add(mapResultSetToComment(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all comments", e);
        }
        return comments;
    }

    @Override
    public void updateById(int id) {
        String sql = crudQueries.updateByIdQuery(id, "comments", "content = ?, postId = ?, userId = ?", "comment_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "updated_content");
            statement.setInt(2, 1);
            statement.setInt(3, 1);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update comment", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "comments", "comment_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }

    private CommentEntity mapResultSetToComment(ResultSet resultSet) throws SQLException {
        CommentEntity comment = new CommentEntity();
        comment.setCommentId(resultSet.getInt("comment_id"));
        comment.setContent(resultSet.getString("content"));
        comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        comment.setPostId(resultSet.getInt("post_id"));
        comment.setUserId(resultSet.getInt("user_id"));
        return comment;
    }
}
