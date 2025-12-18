package librarysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

public class AddBookWindow extends JDialog implements ActionListener {
    private JTextField titleField;
    private JTextField isbnField;
    private JComboBox<String> categoryField; 
    private JButton addButton;
    private JButton backButton;
    private LibraryDashboard parentFrame;

    public AddBookWindow(LibraryDashboard parentFrame) {
        super(parentFrame, "Add New Book", true);
        
        this.parentFrame = parentFrame;
        setSize(400, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Add New Book");
        titleLabel.setBounds(100, 20, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        int y = 70;

       
        JLabel nameLabel = new JLabel("Book Title:");
        nameLabel.setBounds(30, y, 100, 30);
        add(nameLabel);

        titleField = new JTextField();
        titleField.setBounds(140, y, 220, 30);
        add(titleField);
        y += 50;

        
        JLabel isbnLabel = new JLabel("ISBN:");
        isbnLabel.setBounds(30, y, 100, 30);
        add(isbnLabel);

        isbnField = new JTextField();
        isbnField.setBounds(140, y, 220, 30);
        add(isbnField);
        y += 50;

        
        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(30, y, 100, 30);
        add(catLabel);

        categoryField = new JComboBox<>();
        categoryField.setBounds(140, y, 220, 30);
        categoryField.setBackground(Color.WHITE);
        categoryField.setEditable(true); 
        
        
        LibraryManager manager = parentFrame.getManager();
        List<String> categories = manager.getAllCategories();
        for(String c : categories) {
            categoryField.addItem(c);
        }
        add(categoryField);
        y += 60; 

        // Buttons
        addButton = new JButton("Add Book");
        addButton.setBounds(50, y, 150, 40);
        addButton.setBackground(new Color(2, 132, 130));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(this);
        add(addButton);

        backButton = new JButton("Cancel");
        backButton.setBounds(210, y, 100, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String title = titleField.getText().trim();
            String isbn = isbnField.getText().trim();
            
            String category = (String) categoryField.getSelectedItem();

            if (title.isEmpty() || isbn.isEmpty() || category == null || category.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LibraryManager manager = parentFrame.getManager();
           
            manager.addBook(new Book(isbn, title, category.trim()));

            JOptionPane.showMessageDialog(this, "Success! Book added to library.");
            dispose(); 

        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
