package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentProblemHistoryWindow extends JDialog {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton, clearAllButton, backButton; 
    private LibraryDashboard parentFrame;

    public StudentProblemHistoryWindow(LibraryDashboard parentFrame) {
        super(parentFrame, "My Problem Reports", true);
        this.parentFrame = parentFrame;
        setSize(600, 450);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        loadHistory();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("My Reported Problems");
        titleLabel.setBounds(150, 20, 300, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        // Table
        String[] columns = {"ID", "Date", "Issue Description"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        
        historyTable.getColumnModel().getColumn(0).setMinWidth(0);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(400);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBounds(30, 70, 520, 230);
        add(scroll);

        // Delete Single Button
        deleteButton = new JButton("Delete Selected");
        deleteButton.setBounds(30, 330, 140, 35);
        deleteButton.setBackground(new Color(2, 132, 130));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelected());
        add(deleteButton);

        // Clear All Button (NEW)
        clearAllButton = new JButton("Clear All");
        clearAllButton.setBounds(180, 330, 140, 35);
        clearAllButton.setBackground(Color.RED);
        clearAllButton.setForeground(Color.WHITE);
        clearAllButton.addActionListener(e -> clearAll());
        add(clearAllButton);

        backButton = new JButton("Close");
        backButton.setBounds(410, 330, 140, 35);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> dispose());
        add(backButton);
    }

    private void loadHistory() {
        String username = parentFrame.getCurrentMember().getMemberId();
        parentFrame.getManager().loadUserProblems(tableModel, username);
    }

    private void deleteSelected() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a report to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        parentFrame.getManager().deleteFeedback(id);
        loadHistory();
    }

  
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete ALL your reports?", "Clear All", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String username = parentFrame.getCurrentMember().getMemberId();
            parentFrame.getManager().clearProblemHistory(username);
            loadHistory();
            JOptionPane.showMessageDialog(this, "All reports cleared.");
        }
    }
}
