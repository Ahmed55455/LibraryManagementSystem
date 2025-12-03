package librarysystem;

public abstract class Member implements Searchable {
    private String memberId; 
    private String name;     
    private String lastName;
    private String studentId;
    private String email; 
    private int age;

    public Member(String memberId, String name, String lastName, String studentId, String email, int age) {
        this.memberId = memberId;
        this.name = name;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email; 
        this.age = age;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getStudentId() { return studentId; }
    public int getAge() { return age; }

    public String getEmail() { 
        return email; 
    }

    public abstract double calculateFee(int daysLate);

    @Override
    public boolean search(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase());
    }
}
