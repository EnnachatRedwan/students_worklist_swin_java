import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainApplication extends JFrame {
    private StudentsPanel studentsPanel;
    private SubjectsPanel subjectsPanel;
    private ScoresPanel scoresPanel;

    public MainApplication() {
        setTitle("School Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 500);

        studentsPanel = new StudentsPanel();
        subjectsPanel = new SubjectsPanel();
        scoresPanel = new ScoresPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Students", studentsPanel);
        tabbedPane.add("Subjects", subjectsPanel);
        tabbedPane.add("Scores", scoresPanel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                switch (selectedIndex) {
                    case 0:
                        studentsPanel.refreshData();
                        break;
                    case 1:
                        subjectsPanel.refreshData();
                        break;
                    case 2:
                        scoresPanel.refreshData();
                        break;
                }
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApplication());
    }
}