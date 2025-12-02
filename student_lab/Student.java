import java.io.*;
import java.util.*;

class Student implements Comparable<Student> {
    long num;
    String name;
    int group;
    double grade;

    public Student(long num, String name, int group, double grade) {
        this.num = num;
        this.name = name;
        this.group = group;
        this.grade = grade;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return num == student.num &&
                group == student.group &&
                Double.compare(student.grade, grade) == 0 &&
                Objects.equals(name, student.name);
    }

    public int hashCode() {
        return Objects.hash(num, name, group, grade);
    }

    public String toString() {
        return String.format("%d;%s;%d;%.2f", num, name, group, grade);
    }

    public int compareTo(Student other) {
        return Long.compare(this.num, other.num);
    }

    public static Student fromString(String s) {
        String[] parts = s.split(";");
        return new Student(
                Long.parseLong(parts[0]),
                parts[1],
                Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3])
        );
    }
}

class StudentSet {
    public Set<Student> students = new HashSet<>();

    public void add(Student s) {
        students.add(s);
    }

    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                students.add(Student.fromString(line));
            }
        }
    }

    public void saveToFile(String filename) throws IOException {
        List<Student> sortedStudents = new ArrayList<>(students);
        Collections.sort(sortedStudents);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student s : sortedStudents) {
                writer.write(s.toString());
                writer.newLine();
            }
        }
    }

    public static StudentSet union(StudentSet set1, StudentSet set2) {
        StudentSet result = new StudentSet();
        result.students.addAll(set1.students);
        result.students.addAll(set2.students);
        return result;
    }

    public static StudentSet intersection(StudentSet set1, StudentSet set2) {
        StudentSet result = new StudentSet();
        result.students.addAll(set1.students);
        result.students.retainAll(set2.students);
        return result;
    }

    public static StudentSet difference(StudentSet set1, StudentSet set2) {
        StudentSet result = new StudentSet();
        result.students.addAll(set1.students);
        result.students.removeAll(set2.students);
        return result;
    }
}