package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminReportWindow extends JDialog {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton, backButton;
    private LibraryDashboard parentFrame;

    public AdminReportWindow(LibraryDashboard parentFrame) {
        super(parentFrame, "Admin - Global Loan Report", true);
        this.parentFrame = parentFrame;
        setSize(750, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        loadReportData();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Global Loan Report");
        titleLabel.setBounds(250, 20, 300, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        // Table Setup
        // Column 0 is "ID" (Hidden)
        String[] columns = {"ID", "Book Title", "Borrowed By", "Date", "Current Status"};
        tableModel = new DefaultTableModel(columns, 0);
        reportTable = new JTable(tableModel);
        
      
        reportTable.getColumnModel().getColumn(0).setMinWidth(0);
        reportTable.getColumnModel().getColumn(0).setMaxWidth(0);
        
       
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(150); 

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBounds(30, 70, 670, 300);
        add(scrollPane);

        // Delete Button
        deleteButton = new JButton("Delete Record");
        deleteButton.setBounds(50, 400, 200, 40);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelected());
        add(deleteButton);

        // Close Button
        backButton = new JButton("Close Report");
        backButton.setBounds(500, 400, 200, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> dispose());
        add(backButton);
    }

    private void loadReportData() {
        // Use the new manager method that includes IDs
        parentFrame.getManager().loadLoanReportTable(tableModel);
    }

    private void deleteSelected() {
        int row = reportTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this loan record?\n(If the book was borrowed, it will become available)", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0); 
            parentFrame.getManager().deleteLoan(id);
            
           
            loadReportData(); 
            JOptionPane.showMessageDialog(this, "Record Deleted.");
        }
    }
}
