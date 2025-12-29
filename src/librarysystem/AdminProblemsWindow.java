package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminProblemsWindow extends JDialog {
    private JTable problemTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton, backButton;
    private LibraryDashboard parentFrame;

    public AdminProblemsWindow(LibraryDashboard parentFrame) {
        super(parentFrame, "Student Problems", true);
        this.parentFrame = parentFrame;
        setSize(700, 500);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        loadTableData();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Student Problem Reports");
        titleLabel.setBounds(200, 20, 300, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        // Table
        String[] columns = {"ID", "Student", "Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        problemTable = new JTable(tableModel);
        
        // Hide ID
        problemTable.getColumnModel().getColumn(0).setMinWidth(0);
        problemTable.getColumnModel().getColumn(0).setMaxWidth(0);
        problemTable.getColumnModel().getColumn(3).setPreferredWidth(350);

        JScrollPane scroll = new JScrollPane(problemTable);
        scroll.setBounds(30, 70, 620, 300);
        add(scroll);

        // Delete Button
        deleteButton = new JButton("Resolve / Delete");
        deleteButton.setBounds(50, 400, 200, 40);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelected());
        add(deleteButton);

        backButton = new JButton("Close");
        backButton.setBounds(450, 400, 200, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> dispose());
        add(backButton);
    }

    private void loadTableData() {
        parentFrame.getManager().loadAllProblems(tableModel);
    }

    private void deleteSelected() {
        int row = problemTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a problem to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        parentFrame.getManager().deleteFeedback(id);
        loadTableData();
        JOptionPane.showMessageDialog(this, "Problem Deleted / Resolved.");
    }
}
