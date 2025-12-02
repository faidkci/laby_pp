package org.example;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GradeBook {
    private String surname;
    private String name;
    private String patronymic;
    private int course;
    private int group;
    private List<Session> sessions = new ArrayList<>();

    public GradeBook(String surname, String name, String patronymic, int course, int group) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.course = course;
        this.group = group;
    }

    public static class Session {
        public int number;
        public List<Record> records = new ArrayList<>();

        public Session(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public List<Record> getRecords() {
            return records;
        }

        public void addRecord(Record r) {
            if (r != null) records.add(r);
        }
    }

    public static class Record {
        public enum Type {exam, zachet}
        public Type type;
        public String subject;
        public Integer grade;
        public Boolean passed;

        public static Record exam(String subject, int grade) {
            Record r = new Record();
            r.type = Type.exam;
            r.subject = subject;
            r.grade = grade;
            r.passed = grade >= 1;
            return r;
        }

        public static Record zachet(String subject, boolean passed) {
            Record r = new Record();
            r.type = Type.zachet;
            r.subject = subject;
            r.passed = passed;
            r.grade = null;
            return r;
        }
    }

    public void addSession(Session s) {
        if (s != null) sessions.add(s);
    }

    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    public String getSurname() { return surname; }
    public String getName() { return name; }
    public String getPatronymic() { return patronymic; }
    public int getCourse() { return course; }
    public int getGroup() { return group; }

    public boolean allExamsAtLeast9() {
        boolean hasExam = false;
        for (Session s : sessions) {
            for (Record r : s.getRecords()) {
                if (r != null && r.type == Record.Type.exam) {
                    hasExam = true;
                    if (r.grade == null || r.grade < 9) return false;
                }
            }
        }
        return hasExam;
    }

    public double AverageMark() {
        double sum = 0;
        int count = 0;
        for (Session s : sessions) {
            for (Record r : s.getRecords()) {
                if (r != null && r.type == Record.Type.exam && r.grade != null) {
                    sum += r.grade;
                    count++;
                }
            }
        }
        if (count == 0) return 0.0;
        return sum / count;
    }

    public double GetAverageMark() {
        return AverageMark();
    }

    public boolean allZachetsPassed() {
        boolean hasZachet = false;
        for (Session s : sessions) {
            for (Record r : s.getRecords()) {
                if (r != null && r.type == Record.Type.zachet) {
                    hasZachet = true;
                    if (r.passed == null || !r.passed) return false;
                }
            }
        }
        return hasZachet;
    }

    public static String toJsonReport(List<GradeBook> students) {
        List<StudentDetailedInfo> detailedInfos = new ArrayList<>();

        for (GradeBook gb : students) {
            if (gb.allExamsAtLeast9() && gb.allZachetsPassed()) {
                double average = roundToHundredths(gb.GetAverageMark());
                detailedInfos.add(new StudentDetailedInfo(
                        gb.surname, gb.name, gb.patronymic,
                        gb.course, gb.group, average, gb.sessions
                ));
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(detailedInfos);
    }

    public static String toTextReport(List<GradeBook> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("Средние баллы отличников\n\n");

        for (GradeBook gb : students) {
            if (gb.allExamsAtLeast9() && gb.allZachetsPassed()) {
                double avg = roundToHundredths(gb.GetAverageMark());
                sb.append(String.format("Студент: %s %s %s\n",
                        gb.getSurname(), gb.getName(), gb.getPatronymic()));
                sb.append(String.format("Курс: %d, Группа: %d, Средний балл: %.2f\n",
                        gb.getCourse(), gb.getGroup(), avg));
                sb.append("Сессии:\n");

                for (Session session : gb.getSessions()) {
                    sb.append(String.format("  Сессия %d:\n", session.getNumber()));
                    for (Record record : session.getRecords()) {
                        if (record.type == Record.Type.exam) {
                            sb.append(String.format("    %s (экзамен): %d\n",
                                    record.subject, record.grade));
                        } else if (record.type == Record.Type.zachet) {
                            String status = record.passed ? "сдан" : "не сдан";
                            sb.append(String.format("    %s (зачет): %s\n",
                                    record.subject, status));
                        }
                    }
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public static String jsonToTextReport(String json) {
        Gson gson = new Gson();
        StudentDetailedInfo[] detailedInfos = gson.fromJson(json, StudentDetailedInfo[].class);

        StringBuilder sb = new StringBuilder();
        sb.append("Средние баллы отличников (из JSON)\n\n");

        for (StudentDetailedInfo info : detailedInfos) {
            sb.append(String.format("Студент: %s %s %s\n",
                    info.surname, info.name, info.patronymic));
            sb.append(String.format("Курс: %d, Группа: %d, Средний балл: %.2f\n",
                    info.course, info.group, info.average));
            sb.append("Сессии:\n");

            for (Session session : info.sessions) {
                sb.append(String.format("  Сессия %d:\n", session.getNumber()));
                for (Record record : session.getRecords()) {
                    if (record.type == Record.Type.exam) {
                        sb.append(String.format("    %s (экзамен): %d\n",
                                record.subject, record.grade));
                    } else if (record.type == Record.Type.zachet) {
                        String status = record.passed ? "сдан" : "не сдан";
                        sb.append(String.format("    %s (зачет): %s\n",
                                record.subject, status));
                    }
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public static double roundToHundredths(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static class AverageInfo {
        public String surname, name, patronymic;
        public int course, group;
        public double average;

        public AverageInfo(String surname, String name, String patronymic,
                           int course, int group, double average) {
            this.surname = surname;
            this.name = name;
            this.patronymic = patronymic;
            this.course = course;
            this.group = group;
            this.average = average;
        }
    }

    public static class StudentDetailedInfo {
        public String surname, name, patronymic;
        public int course, group;
        public double average;
        public List<Session> sessions;

        public StudentDetailedInfo(String surname, String name, String patronymic,
                                   int course, int group, double average, List<Session> sessions) {
            this.surname = surname;
            this.name = name;
            this.patronymic = patronymic;
            this.course = course;
            this.group = group;
            this.average = average;
            this.sessions = sessions;
        }
    }
}