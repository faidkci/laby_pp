import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");

        System.out.println("Введите текст построчно. Пустая строка — конец ввода:");
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = sc.nextLine();
            if (line.isEmpty()) break;
            sb.append(line).append('\n');
        }

        String text = sb.toString();
        List<String[]> pairs = processWord.findReversePairs(text);

        if (pairs.isEmpty()) {
            System.out.println("Пар-обращений не найдено.");
        } else {
            System.out.println("Найденные пары слов (обращения друг друга):");
            for (String[] p : pairs) {
                System.out.println(p[0] + " <-> " + p[1]);
            }
        }
    }
}
