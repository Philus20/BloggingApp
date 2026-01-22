package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;
import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Services.CrudQueries;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.TagEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository implements Repository<TagEntity> {

    private final IConnection connectionFactory;
    private final ICrudQueries crudQueries;

    public TagRepository() {
        this.connectionFactory = new ConnectionFactory();
        this.crudQueries = new CrudQueries();
    }

    @Override
    public void create(TagEntity tag) {
        String sql = crudQueries.createQuery("tags", "name");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, tag.getName());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tag.setTagId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tag", e);
        }
    }

    @Override
    public TagEntity findByInteger(int id) {
        String sql = crudQueries.getByIntegerQuery(id, "tags", "tag_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToTag(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find tag by ID", e);
        }
        return null;
    }

    @Override
    public TagEntity findByString(String identifier) {
        String sql = crudQueries.getStringQuery(identifier, "tags", "name");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToTag(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find tag by name", e);
        }
        return null;
    }

    @Override
    public List<TagEntity> findAll() {
        String sql = crudQueries.getAllQuery("tags");
        List<TagEntity> tags = new ArrayList<>();
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                tags.add(mapResultSetToTag(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all tags", e);
        }
        return tags;
    }

    @Override
    public void updateById(int id) {
        String sql = crudQueries.updateByIdQuery(id, "tags", "name = ?", "tag_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "updated_name");
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update tag", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "tags", "tag_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag", e);
        }
    }

    private TagEntity mapResultSetToTag(ResultSet resultSet) throws SQLException {
        TagEntity tag = new TagEntity();
        tag.setTagId(resultSet.getInt("tag_id"));
        tag.setName(resultSet.getString("name"));
        return tag;
    }
}
