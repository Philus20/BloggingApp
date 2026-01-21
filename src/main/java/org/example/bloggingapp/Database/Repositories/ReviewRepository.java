package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;
import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Database.Services.CrudQueries;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.ReviewEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository implements Repository<ReviewEntity> {

    private final IConnection connectionFactory;
    private final ICrudQueries crudQueries;

    public ReviewRepository() {
        this.connectionFactory = new ConnectionFactory();
        this.crudQueries = new CrudQueries();
    }

    @Override
    public void create(ReviewEntity review) {
        String sql = crudQueries.createQuery("Review", "rating, comment, userId, postId");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, review.getRating());
            statement.setString(2, review.getComment());
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getPostId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        review.setReviewId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create review", e);
        }
    }

    @Override
    public ReviewEntity findByInteger(int id) {
        String sql = crudQueries.getByIntegerQuery(id, "Review", "reviewId");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToReview(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find review by ID", e);
        }
        return null;
    }

    @Override
    public ReviewEntity findByString(String identifier) {
        String sql = crudQueries.getStringQuery(identifier, "Review", "comment");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToReview(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find review by comment", e);
        }
        return null;
    }

    @Override
    public List<ReviewEntity> findAll() {
        String sql = crudQueries.getAllQuery("Review");
        List<ReviewEntity> reviews = new ArrayList<>();
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reviews.add(mapResultSetToReview(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all reviews", e);
        }
        return reviews;
    }

    @Override
    public void updateById(int id) {
        String sql = crudQueries.updateByIdQuery(id, "Review", "rating = ?, comment = ?, userId = ?, postId = ?", "reviewId");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, 5);
            statement.setString(2, "updated_comment");
            statement.setInt(3, 1);
            statement.setInt(4, 1);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update review", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "Review", "reviewId");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review", e);
        }
    }

    private ReviewEntity mapResultSetToReview(ResultSet resultSet) throws SQLException {
        ReviewEntity review = new ReviewEntity();
        review.setReviewId(resultSet.getInt("reviewId"));
        review.setRating(resultSet.getInt("rating"));
        review.setComment(resultSet.getString("comment"));
        review.setUserId(resultSet.getInt("userId"));
        review.setPostId(resultSet.getInt("postId"));
        return review;
    }
}
