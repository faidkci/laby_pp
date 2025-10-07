import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("input.txt"));
            Random random = new Random();

            int n = scanner.nextInt();
            int m = scanner.nextInt();

            int[][] A = new int[n][m];

            if (scanner.hasNextInt()) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        A[i][j] = scanner.nextInt();
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        A[i][j] = random.nextInt(100);
                    }
                }
            }
            scanner.close();

            PrintWriter out = new PrintWriter(new File("output.txt"));

            out.println("Сгенерированная/прочитанная матрица:");
            MatrixProcessor.printMatrix(A, out);

            MatrixProcessor.findMostDistantRow(A, out);
            MatrixProcessor.findMinOfLocalMax(A, out);
            MatrixProcessor.removeRowColWithMaxAbs(A, out);

            out.close();
            System.out.println("Результаты записаны в output.txt");

        } catch (IOException e) {
            System.out.println("Ошибка работы с файлами: " + e.getMessage());
        }
    }
}
