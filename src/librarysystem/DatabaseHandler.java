package librarysystem;

import java.sql.*;

public class DatabaseHandler {
    // MySQL Connection Settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    
    private static final String PASS = "the password of mysql"; 

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Failed! " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public static void createNewTables() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + " id INT AUTO_INCREMENT PRIMARY KEY,"
                + " username VARCHAR(50) NOT NULL UNIQUE,"
                + " password VARCHAR(50) NOT NULL,"
                + " role VARCHAR(20) NOT NULL,"
                + " first_name VARCHAR(50),"
                + " last_name VARCHAR(50),"
                + " student_id VARCHAR(20),"
                + " email VARCHAR(100),"
                + " age INT"
                + ");";

        String sqlBooks = "CREATE TABLE IF NOT EXISTS books ("
                + " isbn VARCHAR(20) PRIMARY KEY,"
                + " title VARCHAR(100) NOT NULL,"
                + " is_available TINYINT(1) DEFAULT 1" 
                + ");";

        String sqlLoans = "CREATE TABLE IF NOT EXISTS loans ("
                + " id INT AUTO_INCREMENT PRIMARY KEY,"
                + " book_isbn VARCHAR(20) NOT NULL,"
                + " member_username VARCHAR(50) NOT NULL,"
                + " loan_date DATE NOT NULL,"
                + " return_date DATE"
                + ");";

        String sqlFeedback = "CREATE TABLE IF NOT EXISTS feedback ("
                + " id INT AUTO_INCREMENT PRIMARY KEY,"
                + " username VARCHAR(50) NOT NULL,"
                + " message TEXT NOT NULL,"
                + " type VARCHAR(20) NOT NULL," 
                + " date DATE NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            if (conn != null) {
                stmt.execute(sqlUsers);
                stmt.execute(sqlBooks);
                stmt.execute(sqlLoans);
                stmt.execute(sqlFeedback);
                System.out.println("All Tables Checked/Created successfully.");
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
