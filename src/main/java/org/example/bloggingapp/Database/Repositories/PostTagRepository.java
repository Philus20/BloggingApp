package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;
import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Services.CrudQueries;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.PostTagEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostTagRepository implements Repository<PostTagEntity> {

    private final IConnection connectionFactory;
    private final ICrudQueries crudQueries;

    public PostTagRepository() {
        this.connectionFactory = new ConnectionFactory();
        this.crudQueries = new CrudQueries();
    }

    @Override
    public void create(PostTagEntity postTag) {
        String sql = crudQueries.createQuery("post_tags", "post_id, tag_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, postTag.getPostId());
            statement.setInt(2, postTag.getTagId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create post-tag relationship", e);
        }
    }

    @Override
    public PostTagEntity findByInteger(int id) {
        String sql = crudQueries.getByIntegerQuery(id, "post_tags", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToPostTag(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find post-tag relationship by post ID", e);
        }
        return null;
    }

    @Override
    public PostTagEntity findByString(String identifier) {
        String sql = "SELECT * FROM post_tags WHERE post_id = ? OR tag_id = ?";
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, Integer.parseInt(identifier));
            statement.setInt(2, Integer.parseInt(identifier));
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToPostTag(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find post-tag relationship", e);
        }
        return null;
    }

    @Override
    public List<PostTagEntity> findAll() {
        String sql = crudQueries.getAllQuery("post_tags");
        List<PostTagEntity> postTags = new ArrayList<>();
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                postTags.add(mapResultSetToPostTag(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all post-tag relationships", e);
        }
        return postTags;
    }

    @Override
    public void updateById(int id) {
        String sql = crudQueries.updateByIdQuery(id, "post_tags", "post_id = ?, tag_id = ?", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, 1);
            statement.setInt(2, 1);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update post-tag relationship", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "post_tags", "post_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete post-tag relationship", e);
        }
    }

    private PostTagEntity mapResultSetToPostTag(ResultSet resultSet) throws SQLException {
        PostTagEntity postTag = new PostTagEntity();
        postTag.setPostId(resultSet.getInt("post_id"));
        postTag.setTagId(resultSet.getInt("tag_id"));
        return postTag;
    }
}
