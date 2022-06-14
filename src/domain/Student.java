package domain;
import domain.exceptions.EnrollmentRulesViolationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
	private String id;
	private String name;

	public double getGpa() {
		double points = 0;
		int totalUnits = 0;
		Map<Term, Map<Course, Double>> transcript = getTranscript();

		for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
			points += tr.getValue().entrySet().stream().mapToDouble(r -> r.getValue() * r.getKey().getUnits()).sum();
			totalUnits += tr.getValue().keySet().stream().mapToInt(Course::getUnits).sum();
		}
		double gpa = points / totalUnits;
		return gpa;
	}

	static class CourseSection {
        CourseSection(Course course, int section) {
            this.course = course;
            this.section = section;
        }
        Course course;
	    int section;
    }
	private Map<Term, Map<Course, Double>> transcript;
	private List<CourseSection> currentTerm;

	public Student(String id, String name) {
		this.id = id;
		this.name = name;
		this.transcript = new HashMap<>();
		this.currentTerm = new ArrayList<>();
	}
	
	public void takeCourse(Course c, int section) {
		currentTerm.add(new CourseSection(c, section));
	}

	public Map<Term, Map<Course, Double>> getTranscript() {
		return transcript;
	}

	public void addTranscriptRecord(Course course, Term term, double grade) {
	    if (!transcript.containsKey(term))
	        transcript.put(term, new HashMap<>());
	    transcript.get(term).put(course, grade);
    }

    public List<CourseSection> getCurrentTerm() {
        return currentTerm;
    }

    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	public boolean hasPassedCourse(Course course){
		for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
			for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
				if (r.getKey().equals(course) && r.getValue() >= 10)
					return true;
			}
		}
		return false;
	}
}
