package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class AdminInventoryWindow extends JDialog implements ActionListener {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton backButton;
    private LibraryDashboard parentFrame;

    public AdminInventoryWindow(LibraryDashboard parentFrame) {
        
        super(parentFrame, "Admin - Book Inventory Control", true);
        
        this.parentFrame = parentFrame;
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        loadTableData();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Library Inventory Manager");
        titleLabel.setBounds(150, 20, 300, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        
        String[] columns = {"Book Title", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(30, 70, 520, 250);
        add(scrollPane);

        addButton = new JButton("Add New Book");
        addButton.setBounds(50, 350, 150, 40);
        addButton.setBackground(new Color(2, 132, 130));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(this);
        add(addButton);

        deleteButton = new JButton("Delete Selected");
        deleteButton.setBounds(210, 350, 150, 40);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(this);
        add(deleteButton);

        backButton = new JButton("Close");
        backButton.setBounds(380, 350, 150, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        LibraryManager manager = parentFrame.getManager();
        List<Book> books = manager.getBooks();

        for (Book b : books) {
            String status = b.isAvailable() ? "Available" : "Borrowed";
            Object[] row = {b.getTitle(), b.getIsbn(), status};
            tableModel.addRow(row);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
          
            new AddBookWindow(parentFrame);
            dispose(); 
           
            new AdminInventoryWindow(parentFrame);
            
        } else if (e.getSource() == deleteButton) {
            int selectedRow = bookTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
                return;
            }

            String isbn = (String) tableModel.getValueAt(selectedRow, 1);
            String title = (String) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Delete: " + title + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                parentFrame.getManager().removeBook(isbn);
                loadTableData(); 
                JOptionPane.showMessageDialog(this, "Book Deleted!");
            }

        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
