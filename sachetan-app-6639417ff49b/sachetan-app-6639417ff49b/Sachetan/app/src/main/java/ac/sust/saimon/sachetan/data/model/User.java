package ac.sust.saimon.sachetan.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class User {
    private String id;
    protected String email;
    private String firstName;
    private String lastName;
    protected String password;

    private List<Report> reports;

    private Set<Role> roles;

    public User() {
    }

    public User(String email, String password, String firstName, String lastName) {
        setEmail(email);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
    }

    public User(User user) {
        setEmail(user.email);
        setPassword(user.password);
        setRoles(user.roles);
    }

    public String getId(){
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    };

    public String getPassword() {
        return password;
    };

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String fname) {
        firstName = fname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lname) {
        lastName = lname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report){
        if (this.reports == null){
            this.reports = new ArrayList<Report>();
        }

        this.reports.add(report);
    }

    public void updateReport(String reportId, Report report){

    }

    public void removeReport(Report report){

    }

    public List<Report> getReports() {
        return reports;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "[" + id + "," + email + ", " + firstName + ", " + lastName + "," + roles.size() + "]";
    }
}
