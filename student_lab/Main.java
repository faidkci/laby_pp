import java.util.Scanner;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nМеню операций:");
            System.out.println("1 - Объединение");
            System.out.println("2 - Пересечение");
            System.out.println("3 - Разность");
            System.out.println("0 - Выход");
            System.out.print("Выберите операцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 0) break;

            System.out.print("Введите имя первого файла: ");
            String file1 = scanner.nextLine();
            System.out.print("Введите имя второго файла: ");
            String file2 = scanner.nextLine();
            System.out.print("Введите имя выходного файла: ");
            String outputFile = scanner.nextLine();

            try {
                StudentSet set1 = new StudentSet();
                StudentSet set2 = new StudentSet();
                set1.loadFromFile(file1);
                set2.loadFromFile(file2);

                StudentSet result;
                switch (choice) {
                    case 1:
                        result = StudentSet.union(set1, set2);
                        System.out.println("Объединение: " + result.students.size() + " студентов");
                        break;
                    case 2:
                        result = StudentSet.intersection(set1, set2);
                        System.out.println("Пересечение: " + result.students.size() + " студентов");
                        break;
                    case 3:
                        result = StudentSet.difference(set1, set2);
                        System.out.println("Разность: " + result.students.size() + " студентов");
                        break;
                    default:
                        System.out.println("Неверный выбор!");
                        continue;
                }

                result.saveToFile(outputFile);
                System.out.println("Операция выполнена успешно! Результат сохранен в " + outputFile);

            } catch (FileNotFoundException e) {
                System.out.println("Ошибка: файл не найден - " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Ошибка работы с файлами: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Множество студентов в ответе пустое");
            }
        }
        scanner.close();
    }
}