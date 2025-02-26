import dao.ScoreDao;
import dao.StudentDao;
import dao.SubjectDao;
import model.Score;
import model.Student;
import model.Subject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScoresPanel extends JPanel {
    private ScoreDao scoreDao;
    private StudentDao studentDao;
    private SubjectDao subjectDao;

    private JComboBox<StudentItem> studentComboBox;
    private JComboBox<SubjectItem> subjectComboBox;
    private JTextField scoreField;
    private JButton addButton, deleteButton, updateButton, meanScoreButton;
    private JTable scoresTable;
    private DefaultTableModel tableModel;

    private Integer selectedScoreId = null;

    public ScoresPanel() {
        scoreDao = new ScoreDao();
        studentDao = new StudentDao();
        subjectDao = new SubjectDao();

        setLayout(new BorderLayout());

        String[] columns = {"ID", "Student", "Subject", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        scoresTable = new JTable(tableModel);
        scoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scoresTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    populateFields();
                }
            }
        });

        studentComboBox = new JComboBox<>();
        subjectComboBox = new JComboBox<>();
        scoreField = new JTextField(10);
        addButton = new JButton("Add Score");
        updateButton = new JButton("Update Score");
        deleteButton = new JButton("Delete Score");
        meanScoreButton = new JButton("Show Mean Score");

        loadStudentsComboBox();
        loadSubjectsComboBox();

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Student:"));
        inputPanel.add(studentComboBox);
        inputPanel.add(new JLabel("Subject:"));
        inputPanel.add(subjectComboBox);
        inputPanel.add(new JLabel("Score:"));
        inputPanel.add(scoreField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(meanScoreButton);

        addButton.addActionListener(e -> addScore());
        updateButton.addActionListener(e -> updateScore());
        deleteButton.addActionListener(e -> deleteScore());
        meanScoreButton.addActionListener(e -> showMeanScore());

        studentComboBox.addActionListener(e -> {
            if (studentComboBox.getSelectedItem() != null) {
                updateScoresTableForStudent();
            }
        });

        JScrollPane scrollPane = new JScrollPane(scoresTable);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        if (studentComboBox.getItemCount() > 0) {
            updateScoresTableForStudent();
        }
    }

    private void loadStudentsComboBox() {
        studentComboBox.removeAllItems();
        List<Student> students = studentDao.getStudents();
        for (Student student : students) {
            studentComboBox.addItem(new StudentItem(student));
        }
    }

    private void loadSubjectsComboBox() {
        subjectComboBox.removeAllItems();
        List<Subject> subjects = subjectDao.getSubjects();
        for (Subject subject : subjects) {
            subjectComboBox.addItem(new SubjectItem(subject));
        }
    }

    private void updateScoresTableForStudent() {
        tableModel.setRowCount(0);
        selectedScoreId = null;

        if (studentComboBox.getSelectedItem() == null) {
            return;
        }

        StudentItem selectedStudent = (StudentItem) studentComboBox.getSelectedItem();
        int studentId = selectedStudent.getStudent().getId();

        List<Score> scores = scoreDao.getScoresByStudentId(studentId);
        for (Score score : scores) {
            Subject subject = subjectDao.getSubjectById(score.getSubjectId());
            tableModel.addRow(new Object[]{
                    score.getId(),
                    selectedStudent.toString(),
                    subject.getName(),
                    score.getScore()
            });
        }
    }

    private void populateFields() {
        int selectedRow = scoresTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedScoreId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String studentName = (String) tableModel.getValueAt(selectedRow, 1);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 2);
            String scoreText = tableModel.getValueAt(selectedRow, 3).toString();

            scoreField.setText(scoreText);

            StudentItem currentStudent = (StudentItem) studentComboBox.getSelectedItem();
            if (currentStudent == null || !currentStudent.toString().equals(studentName)) {
                for (int i = 0; i < studentComboBox.getItemCount(); i++) {
                    if (studentComboBox.getItemAt(i).toString().equals(studentName)) {
                        studentComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            SubjectItem currentSubject = (SubjectItem) subjectComboBox.getSelectedItem();
            if (currentSubject == null || !currentSubject.toString().equals(subjectName)) {
                for (int i = 0; i < subjectComboBox.getItemCount(); i++) {
                    if (subjectComboBox.getItemAt(i).toString().equals(subjectName)) {
                        subjectComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            selectedScoreId = null;
        }
    }

    private void addScore() {
        if (studentComboBox.getSelectedItem() == null || subjectComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select both student and subject!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            StudentItem selectedStudent = (StudentItem) studentComboBox.getSelectedItem();
            SubjectItem selectedSubject = (SubjectItem) subjectComboBox.getSelectedItem();
            double scoreValue = Double.parseDouble(scoreField.getText());

            if (scoreValue < 0 || scoreValue > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            scoreDao.addScore(selectedStudent.getStudent().getId(),
                    selectedSubject.getSubject().getId(),
                    scoreValue);
            updateScoresTableForStudent();
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for score!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateScore() {
        if (selectedScoreId == null) {
            JOptionPane.showMessageDialog(this, "Please select a score to update!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double newScore = Double.parseDouble(scoreField.getText());
            if (newScore < 0 || newScore > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            scoreDao.updateScore(selectedScoreId, newScore);
            updateScoresTableForStudent();
            clearFields();
            selectedScoreId = null;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for score!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteScore() {
        if (selectedScoreId == null) {
            JOptionPane.showMessageDialog(this, "Please select a score to delete!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scoreDao.deleteScore(selectedScoreId);
        updateScoresTableForStudent();
        clearFields();
        selectedScoreId = null;
    }

    private void showMeanScore() {
        List<Score> scores = scoreDao.getScores();
        if (scores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No scores available to calculate mean.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double total = 0;
        for (Score score : scores) {
            total += score.getScore();
        }
        double mean = total / scores.size();
        JOptionPane.showMessageDialog(this, String.format("Mean Score: %.2f", mean), "Mean Score", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        scoreField.setText("");
    }

    public void refreshData() {
        loadStudentsComboBox();
        loadSubjectsComboBox();
        if (studentComboBox.getItemCount() > 0) {
            updateScoresTableForStudent();
        } else {
            tableModel.setRowCount(0);
        }
    }

    private class StudentItem {
        private Student student;

        public StudentItem(Student student) {
            this.student = student;
        }

        public Student getStudent() {
            return student;
        }

        @Override
        public String toString() {
            return student.getFirstName() + " " + student.getLastName();
        }
    }

    private class SubjectItem {
        private Subject subject;

        public SubjectItem(Subject subject) {
            this.subject = subject;
        }

        public Subject getSubject() {
            return subject;
        }

        @Override
        public String toString() {
            return subject.getName();
        }
    }
}
