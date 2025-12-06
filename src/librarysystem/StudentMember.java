package librarysystem;

public class StudentMember extends Member {
    
    public StudentMember(String memberId, String name, String lastName, String studentId, String email, int age) {
        super(memberId, name, lastName, studentId, email, age);
    }

    @Override
    public double calculateFee(int daysLate) {
      
        return daysLate * 0.50; 
    }
}
