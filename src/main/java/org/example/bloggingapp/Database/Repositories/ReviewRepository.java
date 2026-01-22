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
        String sql = "INSERT INTO reviews (rating, comment, user_id, post_id) VALUES (?, ?, ?, ?)";
        
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
        String sql = crudQueries.getByIntegerQuery(id, "reviews", "review_id");
        
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
        String sql = crudQueries.getStringQuery(identifier, "reviews", "comment");
        
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
        String sql = crudQueries.getAllQuery("reviews");
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
        String sql = "UPDATE reviews SET rating = ?, comment = ?, user_id = ?, post_id = ? WHERE review_id = ?";
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            // Note: In a real implementation, you'd pass the actual review object
            // For now, this is a placeholder that would need the review parameters
            statement.setInt(1, 5);  // rating placeholder
            statement.setString(2, "updated_comment");  // comment placeholder
            statement.setInt(3, 1);  // user_id placeholder
            statement.setInt(4, 1);  // post_id placeholder
            statement.setInt(5, id);  // WHERE clause
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update review", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = crudQueries.deleteByIdQuery(id, "reviews", "review_id");
        
        try (Connection connection = connectionFactory.createConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review", e);
        }
    }

    private ReviewEntity mapResultSetToReview(ResultSet resultSet) throws SQLException {
        ReviewEntity review = new ReviewEntity();
        review.setReviewId(resultSet.getInt("review_id"));
        review.setRating(resultSet.getInt("rating"));
        review.setComment(resultSet.getString("comment"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setPostId(resultSet.getInt("post_id"));
        
        // Don't set created_at since the column doesn't exist in the database
        // The entity will use the default timestamp from the constructor
        
        return review;
    }
}
