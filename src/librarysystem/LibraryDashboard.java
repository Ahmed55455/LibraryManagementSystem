package librarysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class LibraryDashboard extends JFrame implements ActionListener {
    
    private LibraryManager manager;
    private Member currentMember;
    private JTextField booksBorrowedField;
    private JPanel contentPanel; 
    private String memberName;
    private String userRole; 
    
    private ArrayList<String> loanHistory = new ArrayList<>();
    private int booksCount = 0; 

    public LibraryDashboard(String memberName, String role) {
        this.memberName = memberName;
        this.userRole = role;
        
        manager = new LibraryManager();
        
        // 1. Always fetch full details (Student OR Admin)
        currentMember = manager.getMemberDetails(memberName);
        
        // Safety check if database fetch fails
        if (currentMember == null) {
            currentMember = new StudentMember(memberName, "Unknown", "", "000", "No Email", 0);
        }
        
        // 2. Only count loans for Students
        if (role.equals("Student")) {
            booksCount = manager.getActiveLoanCount(memberName); 
        }

        setTitle("LIBRARY SYSTEM - " + role); 
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(true);
        setLocationRelativeTo(null);
        addGuiComponents();
        setVisible(true);
    }

    protected void addGuiComponents() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 200, 600); 
        sidebar.setBackground(new Color(2, 132, 130));

        contentPanel = new JPanel(null);
        contentPanel.setBounds(200, 0, 600, 600); 
        contentPanel.setBackground(Color.WHITE);

        JLabel AppLabel = new JLabel("LIBRARY SYSTEM");
        AppLabel.setBounds(0, 30, 200, 35);
        AppLabel.setFont(new Font("Times New Roman", Font.BOLD, 20)); 
        AppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        AppLabel.setForeground(Color.WHITE);
        sidebar.add(AppLabel);

        // Greeting
        JLabel greetingLabel = new JLabel("HELLO, " + currentMember.getName().toUpperCase());
        greetingLabel.setBounds(-5, 25, 600, 60);
        greetingLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setForeground(new Color(2, 132, 130)); 
        contentPanel.add(greetingLabel);
        
        JLabel roleLabel = new JLabel("(Role: " + userRole + ")");
        roleLabel.setBounds(0, 75, 600, 30);
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setForeground(Color.GRAY);
        contentPanel.add(roleLabel);

      
        addProfileInfo(contentPanel);

        // Buttons Logic
        if (userRole.equals("Student")) {
            addStudentButtons(sidebar);
        } 
        
        if (userRole.equals("Admin")) {
            addAdminButtons(sidebar);
        }

        JButton settingsButton = new JButton("Settings / Options");
        settingsButton.setBounds(0, 450, 200, 40);
        settingsButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        settingsButton.setBackground(Color.WHITE);
        settingsButton.setForeground(new Color(2, 132, 130));
        settingsButton.addActionListener(e -> new SettingsWindow(LibraryDashboard.this));
        sidebar.add(settingsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(0, 510, 200, 40); 
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(new Color(2, 132, 130));
        logoutButton.addActionListener(e -> { new LoginPage(); dispose(); });
        sidebar.add(logoutButton);

        add(sidebar);
        add(contentPanel);
    }

    private void addProfileInfo(JPanel panel) {
        int y = 130;
        int x = 150;
        
        // Show Info
        addProfileLabel(panel, "User ID:", currentMember.getStudentId(), x, y); y+=40;
        addProfileLabel(panel, "Full Name:", currentMember.getName() + " " + currentMember.getLastName(), x, y); y+=40;
        addProfileLabel(panel, "Email:", currentMember.getEmail(), x, y); y+=40;
        addProfileLabel(panel, "Age:", String.valueOf(currentMember.getAge()), x, y); y+=40;

        // Only show Borrowing Count if Student
        if (userRole.equals("Student")) {
            JLabel maxBooksLabel = new JLabel("Max Books Allowed: 10");
            maxBooksLabel.setBounds(0, y+10, 600, 30);
            maxBooksLabel.setFont(new Font("Arial", Font.BOLD, 14));
            maxBooksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            maxBooksLabel.setForeground(Color.GRAY);
            panel.add(maxBooksLabel);
            
            JLabel booksBorrowedLabel = new JLabel("Books Currently Borrowed");
            booksBorrowedLabel.setBounds(0, y+40, 600, 30);
            booksBorrowedLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
            booksBorrowedLabel.setHorizontalAlignment(SwingConstants.CENTER);
            booksBorrowedLabel.setForeground(new Color(2, 132, 130)); 
            panel.add(booksBorrowedLabel);

            booksBorrowedField = new JTextField(String.valueOf(booksCount)); 
            booksBorrowedField.setBounds(200, y+80, 200, 40);
            booksBorrowedField.setHorizontalAlignment(SwingConstants.CENTER);
            booksBorrowedField.setEditable(false);
            panel.add(booksBorrowedField);
        } else {
            // Admin Message
            JLabel adminMsg = new JLabel("Administrator Mode Active");
            adminMsg.setBounds(0, y+40, 600, 30);
            adminMsg.setFont(new Font("Times New Roman", Font.BOLD, 22));
            adminMsg.setHorizontalAlignment(SwingConstants.CENTER);
            adminMsg.setForeground(Color.RED); 
            panel.add(adminMsg);
        }
    }
    
    private void addProfileLabel(JPanel panel, String title, String value, int x, int y) {
        JLabel t = new JLabel(title);
        t.setBounds(x, y, 120, 30);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        t.setForeground(Color.GRAY);
        panel.add(t);
        
        JLabel v = new JLabel(value);
        v.setBounds(x+130, y, 200, 30);
        v.setFont(new Font("Arial", Font.BOLD, 16));
        v.setForeground(new Color(2, 132, 130));
        panel.add(v);
    }

   private void addStudentButtons(JPanel sidebar) {
      
        
        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.setBounds(0, 100, 200, 40); 
        borrowButton.setBackground(Color.WHITE);
        borrowButton.setForeground(new Color(2, 132, 130));
        borrowButton.addActionListener(e -> new BorrowBookWindow(LibraryDashboard.this, loanHistory));
        sidebar.add(borrowButton);

        JButton returnButton = new JButton("Return Book");
        returnButton.setBounds(0, 150, 200, 40); 
        returnButton.setBackground(Color.WHITE);
        returnButton.setForeground(new Color(2, 132, 130));
        returnButton.addActionListener(e -> new ReturnBookWindow(LibraryDashboard.this, loanHistory));
        sidebar.add(returnButton);

        JButton historyButton = new JButton("My Loan History");
        historyButton.setBounds(0, 200, 200, 40); 
        historyButton.setBackground(Color.WHITE);
        historyButton.setForeground(new Color(2, 132, 130));
        historyButton.addActionListener(e -> new LoanHistoryWindow(LibraryDashboard.this, loanHistory));
        sidebar.add(historyButton);

        // Student Button
        JButton inboxButton = new JButton("Inbox (Messages)");
        inboxButton.setBounds(0, 250, 200, 40); 
        inboxButton.setBackground(Color.WHITE);
        inboxButton.setForeground(new Color(2, 132, 130));
        inboxButton.addActionListener(e -> new StudentInboxWindow(LibraryDashboard.this));
        sidebar.add(inboxButton);
    }
    
  private void addAdminButtons(JPanel sidebar) {
     
        
        JButton inventoryButton = new JButton("Manage Inventory");
        inventoryButton.setBounds(0, 100, 200, 40); 
        inventoryButton.setBackground(Color.WHITE);
        inventoryButton.setForeground(new Color(2, 132, 130));
        inventoryButton.addActionListener(e -> new AdminInventoryWindow(LibraryDashboard.this));
        sidebar.add(inventoryButton);
        
        JButton reportButton = new JButton("View All Loans");
        reportButton.setBounds(0, 150, 200, 40); 
        reportButton.setBackground(Color.WHITE);
        reportButton.setForeground(new Color(2, 132, 130));
        reportButton.addActionListener(e -> new AdminReportWindow(LibraryDashboard.this));
        sidebar.add(reportButton);

        JButton problemsButton = new JButton("View Problems");
        problemsButton.setBounds(0, 200, 200, 40); 
        problemsButton.setBackground(Color.WHITE);
        problemsButton.setForeground(new Color(2, 132, 130));
        problemsButton.addActionListener(e -> new AdminProblemsWindow(LibraryDashboard.this));
        sidebar.add(problemsButton);

        // Asmin Button
        JButton msgButton = new JButton("Send Message");
        msgButton.setBounds(0, 250, 200, 40); 
        msgButton.setBackground(Color.WHITE);
        msgButton.setForeground(new Color(2, 132, 130));
        msgButton.addActionListener(e -> new AdminSendMessageWindow(LibraryDashboard.this));
        sidebar.add(msgButton);
    }
    @Override
    public void actionPerformed(ActionEvent e) { }

    public LibraryManager getManager() { return manager; }
    public Member getCurrentMember() { return currentMember; }

    public void updateBookCount(boolean increment) {
        if (booksBorrowedField == null) return; 
        if (increment) booksCount++;
        else if(booksCount > 0) booksCount--;
        booksBorrowedField.setText(String.valueOf(booksCount));
    }
    
    public int getBooksCount() { return booksCount; }
}
