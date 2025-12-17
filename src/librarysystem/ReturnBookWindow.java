package librarysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class ReturnBookWindow extends JDialog implements ActionListener {
    private JComboBox<String> bookSelector; 
    private JButton returnButton;
    private JButton backButton;
    private LibraryDashboard parentFrame;
    private ArrayList<String> loanHistory; 

    public ReturnBookWindow(LibraryDashboard parentFrame, ArrayList<String> loanHistory) {

        super(parentFrame, "Return Book", true);
        
        this.parentFrame = parentFrame;
        this.loanHistory = loanHistory; 
        
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame); 
        
        addGuiComponents();
        setVisible(true); 
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Return a Book");
        titleLabel.setBounds(100, 30, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130)); 
        add(titleLabel);

        JLabel nameLabel = new JLabel("Select Book:");
        nameLabel.setBounds(30, 100, 120, 30);
        nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        add(nameLabel);

        bookSelector = new JComboBox<>();
        bookSelector.setBounds(140, 100, 220, 30);
        bookSelector.setBackground(Color.WHITE);
        
        Member user = parentFrame.getCurrentMember();
        LibraryManager manager = parentFrame.getManager();
        
        List<String> myBooks = manager.getBorrowedBooksByMember(user.getMemberId());
        
        if (myBooks.isEmpty()) {
            bookSelector.addItem("No books to return");
            bookSelector.setEnabled(false);
        } else {
            for (String title : myBooks) {
                bookSelector.addItem(title);
            }
        }
        
        add(bookSelector);

        returnButton = new JButton("Return");
        returnButton.setBounds(50, 180, 150, 40); 
        returnButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        returnButton.setBackground(new Color(2, 132, 130));
        returnButton.setForeground(Color.WHITE);
        returnButton.addActionListener(this);
        add(returnButton);

        backButton = new JButton("Back"); 
        backButton.setBounds(210, 180, 100, 40); 
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        backButton.setBackground(Color.GRAY); 
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            String selectedBook = (String) bookSelector.getSelectedItem();
            
            if (selectedBook == null || selectedBook.equals("No books to return")) {
                 JOptionPane.showMessageDialog(this, "No valid book selected!", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            LibraryManager manager = parentFrame.getManager();
            Member user = parentFrame.getCurrentMember();
            
            Book b = manager.findBook(selectedBook);
            
            if (b != null && !b.isAvailable()) {
                manager.returnBook(selectedBook);
                parentFrame.updateBookCount(false); 
                
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                loanHistory.add("Returned: " + selectedBook + " (" + date + ")");
                
                double fee = user.calculateFee(2); 
                
                JOptionPane.showMessageDialog(this, 
                    "Returned: " + selectedBook + "\n" +
                    "the price is:" + fee + "$" , 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error processing return.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
