import dao.StudentDao;
import model.Student;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class StudentsPanel extends JPanel {
    private StudentDao studentDAO;
    private JTextField firstNameField, lastNameField, addressField, telField, searchField;
    private JButton addButton, deleteButton, updateButton, exportStudentsButton, exportStudentsSerialisedButton;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public StudentsPanel() {
        studentDAO = new StudentDao();
        setLayout(new BorderLayout());

        String[] columns = {"ID", "First Name", "Last Name", "Address", "Tel"};
        tableModel = new DefaultTableModel(columns, 0);
        studentsTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        studentsTable.setRowSorter(sorter);
        updateStudentTable();

        JScrollPane scrollPane = new JScrollPane(studentsTable);

        firstNameField = new JTextField(10);
        lastNameField = new JTextField(10);
        addressField = new JTextField(10);
        telField = new JTextField(10);
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");
        exportStudentsButton = new JButton("Export Students");
        exportStudentsSerialisedButton = new JButton("Export Students Serialised");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Tel:"));
        inputPanel.add(telField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(exportStudentsButton);
        inputPanel.add(exportStudentsSerialisedButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        updateButton.addActionListener(e -> updateStudent());
        exportStudentsButton.addActionListener(e -> exportStudents());
        exportStudentsSerialisedButton.addActionListener(e -> exportStudentsSerialised());
        studentsTable.getSelectionModel().addListSelectionListener(e -> populateFields());
    }

    private void updateStudentTable() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getStudents();
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getAddress(),
                    student.getTel()
            });
        }
    }

    public void refreshData() {
        updateStudentTable();
    }

    private void addStudent() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String address = addressField.getText();
        String tel = telField.getText();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !address.isEmpty() && !tel.isEmpty()) {
            studentDAO.addStudent(firstName, lastName, address, tel);
            refreshData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            studentDAO.deleteStudent(id);
            refreshData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Select a student to delete!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            studentDAO.updateStudent(id, firstNameField.getText(), lastNameField.getText(),
                    addressField.getText(), telField.getText());
            refreshData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Select a student to update!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportStudents() {
        List<Student> students = studentDAO.getStudents();
        try (FileWriter writer = new FileWriter("students.txt")) {
            for (Student s : students) {
                writer.write(String.format("%d;%s;%s;%s;%s%n",
                        s.getId(), s.getFirstName(), s.getLastName(), s.getAddress(), s.getTel()));
            }
            JOptionPane.showMessageDialog(this, "Exported successfully to students.txt",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportStudentsSerialised() {
        List<Student> students = studentDAO.getStudents();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.ser"))) {
            for (Student s : students) {
                oos.writeObject(s);
            }
            JOptionPane.showMessageDialog(this, "Exported successfully to students.ser",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
            firstNameField.setText((String) tableModel.getValueAt(modelRow, 1));
            lastNameField.setText((String) tableModel.getValueAt(modelRow, 2));
            addressField.setText((String) tableModel.getValueAt(modelRow, 3));
            telField.setText((String) tableModel.getValueAt(modelRow, 4));
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        telField.setText("");
    }
}