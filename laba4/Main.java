import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<RecordBook> recordBooks = new ArrayList<>();

        // Чтение из файла
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            RecordBook currentStudent = null;
            RecordBook.Session currentSession = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Студент:")) {
                    String[] parts = line.substring(9).split(",");
                    String[] nameParts = parts[0].trim().split(" ");
                    String lastName = nameParts[0];
                    String firstName = nameParts[1];
                    String middleName = nameParts[2];
                    int course = Integer.parseInt(parts[1].trim());
                    String group = parts[2].trim();

                    currentStudent = new RecordBook(lastName, firstName, middleName, course, group);
                    recordBooks.add(currentStudent);

                } else if (line.startsWith("Сессия:")) {
                    int sessionNumber = Integer.parseInt(line.substring(8).trim());
                    currentSession = currentStudent.createSession(sessionNumber);

                } else if (line.startsWith("Экзамен:")) {
                    String[] parts = line.substring(9).split(",");
                    String subject = parts[0].trim();
                    int grade = Integer.parseInt(parts[1].trim());
                    currentSession.addExam(subject, grade);

                } else if (line.startsWith("Зачет:")) {
                    String[] parts = line.substring(7).split(",");
                    String subject = parts[0].trim();
                    boolean passed = Boolean.parseBoolean(parts[1].trim());
                    currentSession.addCredit(subject, passed);
                }
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        // Запись результатов в файл
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            writer.println("СТУДЕНТЫ-ОТЛИЧНИКИ:");
            writer.println("==================");

            boolean foundExcellentStudents = false;

            for (RecordBook student : recordBooks) {
                if (student.isExcellentStudent()) {
                    foundExcellentStudents = true;
                    writer.println("\n" + student.toString() + ":");

                    for (RecordBook.Session session : student.getSessions()) {
                        // Используем новый метод для получения информации об экзаменах
                        for (String examInfo : session.getExamInfo()) {
                            String[] parts = examInfo.split(",");
                            String subject = parts[0];
                            int grade = Integer.parseInt(parts[1]);
                            writer.printf("  Сессия %d: %s - %d%n",
                                    session.getSessionNumber(), subject, grade);
                        }
                    }
                }
            }

            if (!foundExcellentStudents) {
                writer.println("Отличники не найдены.");
            }

        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }

        // Вывод в консоль для отладки
        System.out.println("Обработанные данные:");
        for (RecordBook student : recordBooks) {
            System.out.println(student + " - " +
                    (student.isExcellentStudent() ? "ОТЛИЧНИК" : "не отличник"));
        }
    }
}