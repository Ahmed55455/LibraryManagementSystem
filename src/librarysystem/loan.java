package librarysystem;

import java.util.Date;

public class Loan {
    private Book book;
    private Member member;
    private Date loanDate;

    public Loan(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.loanDate = new Date(); 
    }

  
    public Member getMember() { 
        return member; 
    }
    
    public Date getLoanDate() {
        return loanDate;
    }


    public Book getBook() { 
        return book; 
    }
}
