package domain;

import java.util.List;

public class EnrollmentRequestParams {
    private Student student;
    private List<CSE> courses;
    public EnrollmentRequestParams(Student s,List<CSE> cs){
        student = s;
        courses = cs;
    }

    public Student getStudent(){ return student;}

    public List<CSE> getCourses() {
        return courses;
    }
}
