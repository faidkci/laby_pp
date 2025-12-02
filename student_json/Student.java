package org.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

class Student implements Comparable<Student> {
    private long num;
    private String name;
    private int group;
    private double grade;

    // Конструктор по умолчанию для Gson
    public Student() {}

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
        return String.format("Student{num=%d, name='%s', group=%d, grade=%.2f}", num, name, group, grade);
    }

    public int compareTo(Student other) {
        return Long.compare(this.num, other.num);
    }

    // Геттеры и сеттеры для Gson
    public long getNum() { return num; }
    public void setNum(long num) { this.num = num; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getGroup() { return group; }
    public void setGroup(int group) { this.group = group; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
}

class StudentSet {
    public Set<Student> students = new HashSet<>();
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public void add(Student s) {
        students.add(s);
    }

    public void loadFromJsonFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Файл " + filename + " не найден");
        }

        try (FileReader reader = new FileReader(file)) {
            Type studentListType = new TypeToken<List<Student>>(){}.getType();
            List<Student> studentList = gson.fromJson(reader, studentListType);
            students.clear();
            if (studentList != null) {
                students.addAll(studentList);
            }
        }
    }

    public void saveToJsonFile(String filename) throws IOException {
        List<Student> sortedStudents = new ArrayList<>(students);
        Collections.sort(sortedStudents);

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(sortedStudents, writer);
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