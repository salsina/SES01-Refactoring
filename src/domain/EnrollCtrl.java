package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    private EnrollmentRequestParams enrollmentRequestParams;

	public void enroll(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        enrollmentRequestParams = new EnrollmentRequestParams(s, courses);
        checkIfPreviouslyPassed();
        checkPrerequisiteCoursesPassingStatus();
        checkDuplicateEnrollments();
        checkExamConflicts();
        checkGPALimit();
        takeCourses();
    }

    private void takeCourses() {
        for (CSE o : enrollmentRequestParams.getCourses())
            enrollmentRequestParams.getStudent().takeCourse(o.getCourse(), o.getSection());
    }

    private void checkPrerequisiteCoursesPassingStatus() throws EnrollmentRulesViolationException {
        for (CSE o : enrollmentRequestParams.getCourses()) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if(!enrollmentRequestParams.getStudent().hasPassedCourse(pre))
                    throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
            }
        }
    }

    private void checkIfPreviouslyPassed() throws EnrollmentRulesViolationException {
        for (CSE o : enrollmentRequestParams.getCourses()) {
            if (enrollmentRequestParams.getStudent().hasPassedCourse(o.getCourse()))
                throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
        }
    }

    private void checkDuplicateEnrollments() throws EnrollmentRulesViolationException {
        for (CSE o : enrollmentRequestParams.getCourses()) {
            for (CSE o2 : enrollmentRequestParams.getCourses()) {
                if (o == o2)
                    continue;
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }
    }

    private void checkExamConflicts() throws EnrollmentRulesViolationException {
        for (CSE o : enrollmentRequestParams.getCourses()) {
            for (CSE o2 : enrollmentRequestParams.getCourses()) {
                if (o == o2)
                    continue;
                if (o.examTimeConflicts(o2))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
            }
		}
    }

    private void checkGPALimit() throws EnrollmentRulesViolationException {
        int unitsRequested = enrollmentRequestParams.getCourses().stream().mapToInt(c -> c.getCourse().getUnits()).sum();
        double gpa = enrollmentRequestParams.getStudent().getGpa();

        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }

}
