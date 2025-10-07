import java.util.*;

public class RecordBook {
    private String lastName;
    private String firstName;
    private String middleName;
    private int course;
    private String group;
    private List<Session> sessions;

    public RecordBook(String lastName, String firstName, String middleName, int course, String group) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.course = course;
        this.group = group;
        this.sessions = new ArrayList<>();
    }

    // Внутренний класс Сессия
    public class Session {
        private int sessionNumber;
        private List<Exam> exams;
        private List<Credit> credits;

        public Session(int sessionNumber) {
            this.sessionNumber = sessionNumber;
            this.exams = new ArrayList<>();
            this.credits = new ArrayList<>();
        }

        public void addExam(String subject, int grade) {
            exams.add(new Exam(subject, grade));
        }

        public void addCredit(String subject, boolean passed) {
            credits.add(new Credit(subject, passed));
        }

        public boolean isExcellentSession() {
            for (Exam exam : exams) {
                if (exam.getGrade() < 9) {
                    return false;
                }
            }

            for (Credit credit : credits) {
                if (!credit.isPassed()) {
                    return false;
                }
            }

            return true;
        }

        public List<Exam> getExams() {
            return exams;
        }

        public int getSessionNumber() {
            return sessionNumber;
        }

        // Новый метод для получения информации об экзаменах
        public List<String> getExamInfo() {
            List<String> examInfo = new ArrayList<>();
            for (Exam exam : exams) {
                examInfo.add(exam.getSubject() + "," + exam.getGrade());
            }
            return examInfo;
        }
    }

    // Внутренний класс Экзамен
    private class Exam {
        private String subject;
        private int grade;

        public Exam(String subject, int grade) {
            this.subject = subject;
            this.grade = grade;
        }

        public String getSubject() {
            return subject;
        }

        public int getGrade() {
            return grade;
        }
    }

    // Внутренний класс Зачет
    private class Credit {
        private String subject;
        private boolean passed;

        public Credit(String subject, boolean passed) {
            this.subject = subject;
            this.passed = passed;
        }

        public boolean isPassed() {
            return passed;
        }
    }

    // Методы внешнего класса
    public Session createSession(int sessionNumber) {
        Session session = new Session(sessionNumber);
        sessions.add(session);
        return session;
    }

    public boolean isExcellentStudent() {
        for (Session session : sessions) {
            if (!session.isExcellentSession()) {
                return false;
            }
        }
        return !sessions.isEmpty();
    }

    public List<Session> getSessions() {
        return sessions;
    }

    // Геттеры
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public int getCourse() { return course; }
    public String getGroup() { return group; }

    @Override
    public String toString() {
        return lastName + " " + firstName + " " + middleName + ", курс " + course + ", группа " + group;
    }
}