package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton aboutButton; // New Button

    public LoginPage() {
        setTitle("Login");
        setSize(400, 350); // Increased height slightly to fit the new button
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Library Login");
        titleLabel.setBounds(100, 30, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 90, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(130, 90, 200, 30);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 140, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 140, 200, 30);
        add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(60, 200, 120, 40);
        loginButton.setBackground(new Color(2, 132, 130));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this);
        add(loginButton);

         // Register Button
        registerButton = new JButton("Register");
        registerButton.setBounds(200, 200, 120, 40);
        registerButton.setBackground(Color.GRAY);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(this);
        add(registerButton);

        // About Button
        aboutButton = new JButton("About Developer");
        aboutButton.setBounds(110, 260, 160, 30); 
        aboutButton.setBackground(new Color(240, 240, 240)); 
        aboutButton.setForeground(Color.BLACK);
        aboutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        aboutButton.addActionListener(this);
        add(aboutButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            verifyLogin(user, pass);
            
        } else if (e.getSource() == registerButton) {
            new RegisterPage();
            dispose();

        } else if (e.getSource() == aboutButton) {
            JOptionPane.showMessageDialog(this, 
                "Library Management System v1.0\n\n" +
                "Created by: Ahmed Ehab Hassan Ali\n" +
                "Student ID: 220303975\n" + 
                "Arel University", 
                "About Developer", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verifyLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                new LibraryDashboard(username, role);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
    
 
    public static void main(String[] args) {
       
        DatabaseHandler.createNewTables(); 
        
    
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
      
        }

       
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
