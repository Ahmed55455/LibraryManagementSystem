package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterPage extends JFrame implements ActionListener {
  
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheck;
    private JTextField firstNameField, lastNameField, userIdField, emailField, ageField; // Renamed variable
    private JComboBox<String> roleSelector; 
    private JButton registerButton;
    private JButton backButton;

    public RegisterPage() {
        setTitle("Register New Account");
        setSize(400, 700); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setBounds(100, 10, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        int y = 50; 

        // 1. Username
        addLabelAndField("Username:", usernameField = new JTextField(), y); y+=40;
        
        // 2. Password
        addLabelAndField("Password:", passwordField = new JPasswordField(), y); y+=40;
        
        // 3. Confirm Password
        addLabelAndField("Confirm Pass:", confirmPasswordField = new JPasswordField(), y); y+=30;

        // 4. Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setBounds(130, y, 150, 25);
        showPasswordCheck.addActionListener(this);
        add(showPasswordCheck);
        y+=40;

        // 5. Personal Details
        addLabelAndField("First Name:", firstNameField = new JTextField(), y); y+=40;
        addLabelAndField("Last Name:", lastNameField = new JTextField(), y); y+=40;
        
      
        addLabelAndField("User ID:", userIdField = new JTextField(), y); y+=40;
        
        addLabelAndField("Email:", emailField = new JTextField(), y); y+=40;
        addLabelAndField("Age:", ageField = new JTextField(), y); y+=40;

        // 6. Role Selection
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(40, y, 80, 25);
        add(roleLabel);

        String[] roles = {"Student", "Admin"};
        roleSelector = new JComboBox<>(roles);
        roleSelector.setBounds(130, y, 200, 30);
        roleSelector.setBackground(Color.WHITE);
        add(roleSelector);
        y+=50;

        // Buttons
        registerButton = new JButton("Register");
        registerButton.setBounds(50, y, 130, 40);
        registerButton.setBackground(new Color(2, 132, 130));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(this);
        add(registerButton);

        backButton = new JButton("Back");
        backButton.setBounds(200, y, 150, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    private void addLabelAndField(String text, JComponent field, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(40, y, 100, 25);
        add(label);
        field.setBounds(130, y, 200, 30);
        add(field);
    }

    private void saveUser() {
        // 1. Check for Empty Fields
        if (usernameField.getText().isEmpty() || 
            new String(passwordField.getPassword()).isEmpty() ||
            firstNameField.getText().isEmpty() ||
            lastNameField.getText().isEmpty() ||
            userIdField.getText().isEmpty() ||
            emailField.getText().isEmpty() || 
            ageField.getText().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in ALL fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Check Passwords Match
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validate Age
        int age = 0;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age < 10 || age > 100) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age (10-100).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a number (e.g., 20).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Validate Email
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) { 
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 5. Save to Database
        String sql = "INSERT INTO users(username, password, role, first_name, last_name, student_id, email, age) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usernameField.getText().trim());
            pstmt.setString(2, pass);
            pstmt.setString(3, (String) roleSelector.getSelectedItem());
            pstmt.setString(4, firstNameField.getText().trim());
            pstmt.setString(5, lastNameField.getText().trim());
            pstmt.setString(6, userIdField.getText().trim()); // Using the updated field
            pstmt.setString(7, email);
            pstmt.setInt(8, age);

            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new LoginPage();
            dispose();
            
        } catch (SQLException e) {
             if(e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showPasswordCheck) {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*'); 
                confirmPasswordField.setEchoChar('*');
            }
        } 
        else if (e.getSource() == registerButton) {
            saveUser();
        } 
        else if (e.getSource() == backButton) {
            new LoginPage();
            dispose();
        }
    }
}
