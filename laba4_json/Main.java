package org.example;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void  main(String[] args) {
        List<GradeBook> students = new ArrayList<>();
        Path input = Paths.get("input.txt");
        Path outputJson = Paths.get("output.json");
        Path outputText = Paths.get("output.txt");
        Path outputFromJson = Paths.get("output_from_json.txt");

        // Чтение данных из input.txt
        try (Scanner sc = new Scanner(input, "UTF-8")) {
            if (!sc.hasNextInt()) return;
            int n = sc.nextInt();
            for (int i = 0; i < n; i++) {
                String surname = sc.next();
                String name = sc.next();
                String patronymic = sc.next();
                int course = sc.nextInt();
                int group = sc.nextInt();
                int numSessions = sc.nextInt();

                GradeBook gb = new GradeBook(surname, name, patronymic, course, group);
                for (int s = 0; s < numSessions; s++) {
                    int sessionNumber = sc.nextInt();
                    int numRecords = sc.nextInt();
                    GradeBook.Session session = new GradeBook.Session(sessionNumber); // Изменено здесь
                    for (int r = 0; r < numRecords; r++) {
                        String type = sc.next();
                        if ("exam".equalsIgnoreCase(type)) {
                            String subject = sc.next();
                            int grade = sc.nextInt();
                            session.addRecord(GradeBook.Record.exam(subject, grade));
                        } else if ("zachet".equalsIgnoreCase(type)) {
                            String subject = sc.next();
                            boolean passed = sc.nextBoolean();
                            session.addRecord(GradeBook.Record.zachet(subject, passed));
                        } else {
                            if (sc.hasNextLine()) sc.nextLine();
                        }
                    }
                    gb.addSession(session);
                }
                students.add(gb);
            }
        } catch (IOException | NoSuchElementException e) {
            System.out.println("Ошибка чтения input.txt: " + e.getMessage());
            return;
        }

        // 1. Запись JSON
        String json = GradeBook.toJsonReport(students);
        try {
            Files.writeString(outputJson, json);
            System.out.println("JSON записан в output.json");
        } catch (IOException e) {
            System.out.println("Ошибка записи JSON: " + e.getMessage());
        }

        // 2. Запись текстового отчёта напрямую из данных
        String textReport = GradeBook.toTextReport(students);
        try {
            Files.writeString(outputText, textReport);
            System.out.println("Текстовый отчёт записан в output.txt");
        } catch (IOException e) {
            System.out.println("Ошибка записи текстового отчёта: " + e.getMessage());
        }

        // 3. Чтение JSON и преобразование в текстовый отчёт
        try {
            String jsonContent = Files.readString(outputJson);
            String textFromJson = GradeBook.jsonToTextReport(jsonContent);
            Files.writeString(outputFromJson, textFromJson);
            System.out.println("Текстовый отчёт из JSON записан в output_from_json.txt");
        } catch (IOException e) {
            System.out.println("Ошибка чтения JSON или записи отчёта: " + e.getMessage());
        }

        System.out.println("\nСравните файлы output.txt и output_from_json.txt - они должны быть идентичны");
    }
}