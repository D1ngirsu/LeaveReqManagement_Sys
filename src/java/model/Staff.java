package model;

public class Staff extends User {
    private int id;
    private String division; // IT, QA, Sale
    private String role;     // Division Leader, Trưởng nhóm, Nhân viên
    
    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}