package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentInboxWindow extends JDialog {
    private JTable messageTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton, closeButton;
    private LibraryDashboard parentFrame;

    public StudentInboxWindow(LibraryDashboard parentFrame) {
        super(parentFrame, "My Inbox", true);
        this.parentFrame = parentFrame;
        setSize(600, 400);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        loadMessages();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Messages from Admin");
        titleLabel.setBounds(150, 20, 300, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        // Table (ID is hidden column 0)
        String[] columns = {"ID", "Date", "Message"};
        tableModel = new DefaultTableModel(columns, 0);
        messageTable = new JTable(tableModel);
        
        // Hide ID column
        messageTable.getColumnModel().getColumn(0).setMinWidth(0);
        messageTable.getColumnModel().getColumn(0).setMaxWidth(0);
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(400); // Message wider

        JScrollPane scroll = new JScrollPane(messageTable);
        scroll.setBounds(30, 70, 520, 230);
        add(scroll);

        // Delete Button
        deleteButton = new JButton("Delete Selected");
        deleteButton.setBounds(50, 320, 150, 35);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelected());
        add(deleteButton);

        closeButton = new JButton("Close");
        closeButton.setBounds(380, 320, 150, 35);
        closeButton.setBackground(Color.GRAY);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
    }

    private void loadMessages() {
        String username = parentFrame.getCurrentMember().getMemberId();
        parentFrame.getManager().loadStudentMessages(tableModel, username);
    }

    private void deleteSelected() {
        int row = messageTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a message to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0); // Get Hidden ID
        parentFrame.getManager().deleteMessage(id);
        loadMessages(); // Refresh
        JOptionPane.showMessageDialog(this, "Message Deleted.");
    }
}
