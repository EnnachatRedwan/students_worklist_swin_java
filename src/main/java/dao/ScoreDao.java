package dao;

import model.Score;
import model.Student;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreDao {

    public void addScore(int studentId, int subjectId, double score) {
        String sql = "INSERT INTO score (student_id, subject_id, score) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setDouble(3, score);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Score> getScores() {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM score";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                scores.add(new Score(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("subject_id"),
                        rs.getDouble("score")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public List<Score> getScoresByStudentId(int studentId) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM score WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(new Score(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("subject_id"),
                            rs.getDouble("score")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public Map<Integer, Double> getStudentScoresBySubject(int studentId) {
        Map<Integer, Double> subjectScores = new HashMap<>();
        String sql = "SELECT subject_id, score FROM score WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjectScores.put(rs.getInt("subject_id"), rs.getDouble("score"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjectScores;
    }

    public void updateScore(int id, double score) {
        String sql = "UPDATE score SET score = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, score);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScore(int id) {
        String sql = "DELETE FROM score WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScoresByStudentId(int studentId) {
        String sql = "DELETE FROM score WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScoresBySubjectId(int subjectId) {
        String sql = "DELETE FROM score WHERE subject_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}