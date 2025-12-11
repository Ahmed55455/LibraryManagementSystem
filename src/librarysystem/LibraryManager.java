package librarysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel; 

public class LibraryManager {

    // Add Book
    public void addBook(Book book) {
        String sql = "INSERT INTO books(isbn, title, category, is_available) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getCategory());
            pstmt.setInt(4, 1);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    // Remove Book
    public void removeBook(String isbn) {
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE isbn = ?")) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    // Get All Books
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book(rs.getString("isbn"), rs.getString("title"), rs.getString("category"));
                b.setAvailable(rs.getInt("is_available") == 1);
                books.add(b);
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return books;
    }
    
    // Get Categories
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT category FROM books ORDER BY category")) {
            while (rs.next()) { categories.add(rs.getString("category")); }
        } catch (SQLException e) { }
        return categories;
    }

    //  Find Book
    public Book findBook(String keyword) {
        for(Book b : getBooks()) {
            if(b.search(keyword) || b.getIsbn().equals(keyword)) return b;
        }
        return null;
    }

    // Borrow Book
    public boolean borrowBook(String title, String username) {
        Book availableBook = null;
        for (Book b : getBooks()) {
            if (b.getTitle().equalsIgnoreCase(title) && b.isAvailable()) {
                availableBook = b;
                break; 
            }
        }
        if (availableBook == null) return false; 

        try (Connection conn = DatabaseHandler.connect()) {
            PreparedStatement p1 = conn.prepareStatement("UPDATE books SET is_available = 0 WHERE isbn = ?");
            p1.setString(1, availableBook.getIsbn());
            p1.executeUpdate();

            PreparedStatement p2 = conn.prepareStatement("INSERT INTO loans(book_isbn, member_username, loan_date) VALUES(?,?, CURDATE())");
            p2.setString(1, availableBook.getIsbn());
            p2.setString(2, username);
            p2.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    // Return Book
    public void returnBook(String title) {
        Book b = null;
        for(Book book : getBooks()) {
            if(book.getTitle().equalsIgnoreCase(title) && !book.isAvailable()) {
                b = book;
                break;
            }
        }
        if (b == null) return;

        try (Connection conn = DatabaseHandler.connect()) {
            PreparedStatement p1 = conn.prepareStatement("UPDATE books SET is_available = 1 WHERE isbn = ?");
            p1.setString(1, b.getIsbn());
            p1.executeUpdate();

            PreparedStatement p2 = conn.prepareStatement("UPDATE loans SET return_date = CURDATE() WHERE book_isbn = ? AND return_date IS NULL");
            p2.setString(1, b.getIsbn());
            p2.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    // Get Borrowed Books (Dropdown List)
    public List<String> getBorrowedBooksByMember(String username) {
        List<String> titles = new ArrayList<>();
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT b.title FROM loans l JOIN books b ON l.book_isbn = b.isbn WHERE l.member_username = ? AND l.return_date IS NULL")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { titles.add(rs.getString("title")); }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return titles;
    }

    // Get ALL Loans (For Admin Report)
    public List<Loan> getAllLoans() {
        List<Loan> report = new ArrayList<>();
        String sql = "SELECT l.*, b.title, b.category, b.is_available FROM loans l JOIN books b ON l.book_isbn = b.isbn";
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book(rs.getString("book_isbn"), rs.getString("title"), rs.getString("category"));
                b.setAvailable(rs.getInt("is_available") == 1);
                Member m = new StudentMember(rs.getString("member_username"), rs.getString("member_username"), "", "", "", 0);
                report.add(new Loan(b, m));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return report;
    }

    //  Change Password
    public boolean changePassword(String username, String newPassword) {
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    //  Delete Account
    public boolean deleteAccount(String username) {
        try (Connection conn = DatabaseHandler.connect()) {
            conn.prepareStatement("DELETE FROM feedback WHERE username = '" + username + "'").executeUpdate();
            conn.prepareStatement("DELETE FROM loans WHERE member_username = '" + username + "'").executeUpdate();
            conn.prepareStatement("DELETE FROM admin_messages WHERE receiver = '" + username + "'").executeUpdate();
            return conn.prepareStatement("DELETE FROM users WHERE username = '" + username + "'").executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Send Feedback (Problem/Report)
    public void sendFeedback(String username, String message, String type) {
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO feedback(username, message, type, date) VALUES(?,?,?, CURDATE())")) {
            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }
    
    // Send Admin Message
    public void sendAdminMessage(String student, String msg) {
        try (Connection conn = DatabaseHandler.connect(); 
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admin_messages(sender, receiver, message, date_sent) VALUES('Admin', ?, ?, CURDATE())")) {
            pstmt.setString(1, student); 
            pstmt.setString(2, msg); 
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    // Get Active Loan Count
    public int getActiveLoanCount(String username) {
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM loans WHERE member_username = ? AND return_date IS NULL")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }
    
    // Get Student History
    public List<String> getStudentLoanHistory(String username) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT b.title, l.loan_date, l.return_date FROM loans l JOIN books b ON l.book_isbn = b.isbn WHERE l.member_username = ? ORDER BY l.id DESC";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String loanDate = rs.getString("loan_date");
                String returnDate = rs.getString("return_date");
                if (returnDate == null) history.add("Borrowed: " + title + " (" + loanDate + ")");
                else history.add("Returned: " + title + " (on " + returnDate + ")");
            }
        } catch (SQLException e) { }
        return history;
    }

    // Get Member Details (Profile) - WITH EMAIL FIX
    public Member getMemberDetails(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("first_name"); if (firstName == null) firstName = username;
                String lastName = rs.getString("last_name"); if (lastName == null) lastName = "";
                String studentId = rs.getString("student_id"); if (studentId == null) studentId = "N/A";
                String email = rs.getString("email"); if (email == null) email = "N/A";
                
                return new StudentMember(rs.getString("username"), firstName, lastName, studentId, email, rs.getInt("age"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; 
    }

    //  Load Student Messages (Inbox)
    public void loadStudentMessages(DefaultTableModel model, String username) {
        model.setRowCount(0);
        try (Connection conn = DatabaseHandler.connect(); 
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, date_sent, message FROM admin_messages WHERE receiver = ? ORDER BY id DESC")) {
            pstmt.setString(1, username); 
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getInt("id"), rs.getString("date_sent"), rs.getString("message") });
            }
        } catch (SQLException e) { }
    }

    // Load User Problems (History)
    public void loadUserProblems(DefaultTableModel model, String username) {
        model.setRowCount(0);
        String sql = "SELECT id, date, message FROM feedback WHERE username = ? AND type = 'Problem'";
        try (Connection conn = DatabaseHandler.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); 
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getInt("id"), rs.getString("date"), rs.getString("message") });
            }
        } catch (SQLException e) { }
    }

    // Load All Problems (Admin)
    public void loadAllProblems(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT id, username, date, message FROM feedback WHERE type = 'Problem'";
        try (Connection conn = DatabaseHandler.connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getInt("id"), rs.getString("username"), rs.getString("date"), rs.getString("message") });
            }
        } catch (SQLException e) { }
    }

    //  Delete Message
    public void deleteMessage(int id) {
        try (Connection conn = DatabaseHandler.connect(); 
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM admin_messages WHERE id = ?")) {
            pstmt.setInt(1, id); 
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    //  Delete Feedback/Problem
    public void deleteFeedback(int id) {
        try (Connection conn = DatabaseHandler.connect(); 
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM feedback WHERE id = ?")) {
            pstmt.setInt(1, id); 
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }
    //  Clear Loan History (Only deletes returned books)
    public void clearLoanHistory(String username) {
        String sql = "DELETE FROM loans WHERE member_username = ? AND return_date IS NOT NULL";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Clear Problem History
    public void clearProblemHistory(String username) {
        String sql = "DELETE FROM feedback WHERE username = ? AND type = 'Problem'";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Load Loans into Table (New helper for the window upgrade)
    public void loadLoanHistoryTable(DefaultTableModel model, String username) {
        model.setRowCount(0);
        String sql = "SELECT b.title, l.loan_date, l.return_date FROM loans l JOIN books b ON l.book_isbn = b.isbn WHERE l.member_username = ? ORDER BY l.id DESC";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                String status = (returnDate == null) ? "Active (Borrowed)" : "Returned: " + returnDate;
                model.addRow(new Object[]{ rs.getString("title"), rs.getString("loan_date"), status });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    // Load Loans into Table (With IDs for Deleting)
    public void loadLoanReportTable(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT l.id, b.title, l.member_username, l.loan_date, l.return_date " +
                     "FROM loans l " +
                     "JOIN books b ON l.book_isbn = b.isbn " +
                     "ORDER BY l.id DESC";
                     
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                String status = (returnDate == null) ? "Active (Not Returned)" : "Returned: " + returnDate;
                
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("member_username"),
                    rs.getString("loan_date"),
                    status
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Delete Loan Record (And Fix Book Status)
    public void deleteLoan(int id) {
        String getIsbnSql = "SELECT book_isbn FROM loans WHERE id = ?";
        String updateBookSql = "UPDATE books SET is_available = 1 WHERE isbn = ?";
        String deleteLoanSql = "DELETE FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseHandler.connect()) {
            // Find which book this loan was for
            String isbn = null;
            try (PreparedStatement p1 = conn.prepareStatement(getIsbnSql)) {
                p1.setInt(1, id);
                ResultSet rs = p1.executeQuery();
                if (rs.next()) isbn = rs.getString("book_isbn");
            }
            
            if (isbn != null) {
                // Make sure that book is marked Available (Just in case it was active)
                try (PreparedStatement p2 = conn.prepareStatement(updateBookSql)) {
                    p2.setString(1, isbn);
                    p2.executeUpdate();
                }
            }

            // Delete the loan record
            try (PreparedStatement p3 = conn.prepareStatement(deleteLoanSql)) {
                p3.setInt(1, id);
                p3.executeUpdate();
            }
            
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
