package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SettingsWindow extends JDialog implements ActionListener {
    private JButton passButton;
    private JButton problemButton;
    private JButton historyButton; // "My Problem History" button
    private JButton deleteButton;
    private JButton backButton;
    private LibraryDashboard parentFrame;

    public SettingsWindow(LibraryDashboard parentFrame) {
        //  Make it Modal (Blocks background)
        super(parentFrame, "Account Settings", true);
        
        this.parentFrame = parentFrame;
        setSize(400, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        
        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Settings & Options");
        titleLabel.setBounds(100, 20, 200, 30);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(2, 132, 130));
        add(titleLabel);

        //  Change Password
        passButton = new JButton("Change Password");
        passButton.setBounds(50, 80, 300, 40);
        passButton.setBackground(Color.WHITE);
        passButton.setForeground(new Color(2, 132, 130));
        passButton.addActionListener(this);
        add(passButton);

        //  Report a Problem
        problemButton = new JButton("Report a Problem");
        problemButton.setBounds(50, 140, 300, 40);
        problemButton.setBackground(Color.WHITE);
        problemButton.setForeground(new Color(2, 132, 130));
        problemButton.addActionListener(this);
        add(problemButton);

        //  My Problem History
        historyButton = new JButton("My Problem History");
        historyButton.setBounds(50, 200, 300, 40);
        historyButton.setBackground(Color.WHITE);
        historyButton.setForeground(new Color(2, 132, 130));
        historyButton.addActionListener(this);
        add(historyButton);

        //  Delete Account
        deleteButton = new JButton("Delete My Account");
        deleteButton.setBounds(50, 280, 300, 40);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(this);
        add(deleteButton);

        // Close
        backButton = new JButton("Close Settings");
        backButton.setBounds(120, 350, 160, 35);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
    }

    private boolean checkOldPassword(String username, String oldPass) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("password").equals(oldPass);
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = parentFrame.getCurrentMember().getMemberId(); 
        LibraryManager manager = parentFrame.getManager();

        if (e.getSource() == passButton) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JPasswordField oldPassField = new JPasswordField();
            JPasswordField newPassField = new JPasswordField();
            JPasswordField confirmPassField = new JPasswordField();
            panel.add(new JLabel("Old Password:")); panel.add(oldPassField);
            panel.add(new JLabel("New Password:")); panel.add(newPassField);
            panel.add(new JLabel("Confirm New Password:")); panel.add(confirmPassField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String oldPass = new String(oldPassField.getPassword());
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());
                
                if (!checkOldPassword(username, oldPass)) JOptionPane.showMessageDialog(this, "Incorrect Old Password!");
                else if (!newPass.equals(confirmPass)) JOptionPane.showMessageDialog(this, "Passwords do not match!");
                else if (newPass.isEmpty()) JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                else {
                    manager.changePassword(username, newPass);
                    JOptionPane.showMessageDialog(this, "Password updated successfully!");
                }
            }

        } else if (e.getSource() == problemButton) {
            JTextArea textArea = new JTextArea(10, 30);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);

            int result = JOptionPane.showConfirmDialog(null, scrollPane, "Describe your problem", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String msg = textArea.getText().trim();
                if (!msg.isEmpty()) {
                    manager.sendFeedback(username, msg, "Problem");
                    JOptionPane.showMessageDialog(this, "Problem Reported.");
                }
            }

        } else if (e.getSource() == historyButton) {
            new StudentProblemHistoryWindow(parentFrame);

        } else if (e.getSource() == deleteButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure? This cannot be undone!", "Delete Account", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.deleteAccount(username)) {
                    JOptionPane.showMessageDialog(this, "Account Deleted. Goodbye!");
                    parentFrame.dispose(); new LoginPage(); dispose(); 
                }
            }
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
