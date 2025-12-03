package librarysystem;

public class Book implements Searchable {
    private String isbn;
    private String title;
    private String category; 
    private boolean isAvailable;

    public Book(String isbn, String title, String category) { 
        this.isbn = isbn;
        this.title = title;
        this.category = category;
        this.isAvailable = true; 
    }

    Book(String bn, String title) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; } 
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean status) { this.isAvailable = status; }

    @Override
    public boolean search(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase());
    }
    
    @Override
    public String toString() {
        return title; 
    }
}
