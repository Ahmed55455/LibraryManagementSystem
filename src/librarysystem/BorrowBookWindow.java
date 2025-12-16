package librarysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class BorrowBookWindow extends JDialog implements ActionListener {
    private JComboBox<String> categorySelector; 
    private JTextField searchField;             
    private JComboBox<String> bookSelector;     
    private JButton borrowButton;
    private JButton backButton;
    private LibraryDashboard parentFrame;
    private ArrayList<String> loanHistory; 

    public BorrowBookWindow(LibraryDashboard parentFrame, ArrayList<String> loanHistory) {
        super(parentFrame, "Borrow Book", true);
        this.parentFrame = parentFrame;
        this.loanHistory = loanHistory; 
        
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame); 
        
        addGuiComponents();
        setVisible(true); 
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Borrow a Book");
        titleLabel.setBounds(150, 20, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130)); 
        add(titleLabel);

       
        
        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(30, 70, 70, 30);
        add(catLabel);

        categorySelector = new JComboBox<>();
        categorySelector.setBounds(95, 70, 140, 30); 
        categorySelector.setBackground(Color.WHITE);
        
      
        LibraryManager manager = parentFrame.getManager();
        List<String> categories = manager.getAllCategories();
        categorySelector.addItem("All"); 
        for(String c : categories) {
            categorySelector.addItem(c);
        }
        
        
        categorySelector.addActionListener(e -> updateBookList());
        add(categorySelector);

        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(250, 70, 60, 30);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(300, 70, 150, 30);
        
     
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateBookList();
            }
        });
        add(searchField);

        
        JLabel listLabel = new JLabel("Select Book:");
        listLabel.setBounds(30, 130, 100, 30);
        add(listLabel);

        bookSelector = new JComboBox<>();
        bookSelector.setBounds(130, 130, 320, 30);
        bookSelector.setBackground(Color.WHITE);
        add(bookSelector);
        
        
        updateBookList(); 

      
        borrowButton = new JButton("Borrow");
        borrowButton.setBounds(80, 250, 150, 40); 
        borrowButton.setBackground(new Color(2, 132, 130));
        borrowButton.setForeground(Color.WHITE);
        borrowButton.addActionListener(this);
        add(borrowButton);

        backButton = new JButton("Back"); 
        backButton.setBounds(260, 250, 100, 40); 
        backButton.setBackground(Color.GRAY); 
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    
    private void updateBookList() {
        bookSelector.removeAllItems();
        
      
        String selectedCat = (String) categorySelector.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();
        
        LibraryManager manager = parentFrame.getManager();
        List<Book> allBooks = manager.getBooks();
        
        boolean found = false;
        for (Book b : allBooks) {
            if (b.isAvailable()) {
                
                boolean matchCategory = selectedCat.equals("All") || b.getCategory().equals(selectedCat);
                
               
                boolean matchSearch = searchText.isEmpty() || 
                                      b.getTitle().toLowerCase().contains(searchText) || 
                                      b.getIsbn().contains(searchText);
                
                
                if (matchCategory && matchSearch) {
                    bookSelector.addItem(b.getTitle());
                    found = true;
                }
            }
        }
        
        if (!found) {
            bookSelector.addItem("No books found");
            bookSelector.setEnabled(false);
        } else {
            bookSelector.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == borrowButton) {
            if (parentFrame.getBooksCount() >= 10) {
                JOptionPane.showMessageDialog(this, "Limit Reached! Max 10 books.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            String selectedBook = (String) bookSelector.getSelectedItem();
            if (selectedBook == null || selectedBook.equals("No books found")) {
                 JOptionPane.showMessageDialog(this, "Please select a valid book!", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            LibraryManager manager = parentFrame.getManager();
            Member user = parentFrame.getCurrentMember();

            if (manager.borrowBook(selectedBook, user.getMemberId())) {
                parentFrame.updateBookCount(true); 
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                loanHistory.add("Borrowed: " + selectedBook + " (" + date + ")");
                JOptionPane.showMessageDialog(this, "Success! Borrowed: " + selectedBook);
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error borrowing book.");
            }

        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
