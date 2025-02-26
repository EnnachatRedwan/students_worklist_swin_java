import dao.StudentDao;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class StudentsWorkList {
    private StudentDao studentDAO;
    private JTextField firstNameField, lastNameField, addressField, telField, searchField;
    private JButton addButton, deleteButton, updateButton, exportStudentsButton, exportStudentsSerialisedButton;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public StudentsWorkList() {
        studentDAO = new StudentDao();

        JFrame frame = new JFrame("Student Worklist");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 500);
        frame.setMinimumSize(new Dimension(1300, 500));
        frame.setLayout(new BorderLayout());

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
        JLabel searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
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

        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        updateButton.addActionListener(e -> updateStudent());
        exportStudentsButton.addActionListener(e -> exportStudents());
        exportStudentsSerialisedButton.addActionListener(e -> exportStudentsSerialised());
        studentsTable.getSelectionModel().addListSelectionListener(e -> populateFields());

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
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

    private void addStudent() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String address = addressField.getText();
        String tel = telField.getText();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !address.isEmpty() && !tel.isEmpty()) {
            studentDAO.addStudent(firstName, lastName, address, tel);
            updateStudentTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            studentDAO.deleteStudent(id);
            updateStudentTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(null, "Select a student to delete!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            studentDAO.updateStudent(
                    id,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    addressField.getText(),
                    telField.getText()
            );
            updateStudentTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(null, "Select a student to update!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportStudents() {
        List<Student> students = studentDAO.getStudents();
        try (FileWriter fileWriter = new FileWriter("students.txt")) {
            for (Student s : students) {
                String line = s.getId() + ";" + s.getFirstName() + ";" + s.getLastName() + ";" + s.getAddress() + ";" + s.getTel() + "\n";
                fileWriter.append(line);
            }
            fileWriter.flush();
            JOptionPane.showMessageDialog(null, "Students were exported successfully", "Student exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while exporting students", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportStudentsSerialised() {
        List<Student> students = studentDAO.getStudents();
        try (FileOutputStream fileOutputStream = new FileOutputStream("students.ser")){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (Student s : students) {
                objectOutputStream.writeObject(s);
            }
            objectOutputStream.flush();
            JOptionPane.showMessageDialog(null, "Students were exported successfully", "Student exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while exporting students", "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentsWorkList::new);
    }
}