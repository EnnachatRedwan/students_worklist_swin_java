import dao.SubjectDao;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SubjectsPanel extends JPanel {
    private SubjectDao subjectDao;
    private JTextField nameField;
    private JButton addButton, deleteButton, updateButton;
    private JTable subjectsTable;
    private DefaultTableModel tableModel;

    public SubjectsPanel() {
        subjectDao = new SubjectDao();
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Subject Name"};
        tableModel = new DefaultTableModel(columns, 0);
        subjectsTable = new JTable(tableModel);
        updateSubjectTable();

        JScrollPane scrollPane = new JScrollPane(subjectsTable);

        nameField = new JTextField(20);
        addButton = new JButton("Add Subject");
        deleteButton = new JButton("Delete Subject");
        updateButton = new JButton("Update Subject");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Subject Name:"));
        inputPanel.add(nameField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        addButton.addActionListener(e -> addSubject());
        deleteButton.addActionListener(e -> deleteSubject());
        updateButton.addActionListener(e -> updateSubject());
        subjectsTable.getSelectionModel().addListSelectionListener(e -> populateFields());

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateSubjectTable() {
        tableModel.setRowCount(0);
        List<Subject> subjects = subjectDao.getSubjects();
        for (Subject subject : subjects) {
            tableModel.addRow(new Object[]{
                    subject.getId(),
                    subject.getName()
            });
        }
    }

    private void addSubject() {
        String name = nameField.getText();

        if (!name.isEmpty()) {
            subjectDao.addSubject(name);
            updateSubjectTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Subject name is required!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int selectedRow = subjectsTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            subjectDao.deleteSubject(id);
            updateSubjectTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Select a subject to delete!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateSubject() {
        int selectedRow = subjectsTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText();

            if (!name.isEmpty()) {
                subjectDao.updateSubject(id, name);
                updateSubjectTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Subject name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a subject to update!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populateFields() {
        int selectedRow = subjectsTable.getSelectedRow();
        if (selectedRow != -1) {
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        }
    }

    private void clearFields() {
        nameField.setText("");
    }

    public void refreshData() {
        updateSubjectTable();
    }
}