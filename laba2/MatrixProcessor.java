import java.io.PrintWriter;
import java.util.*;

public class MatrixProcessor {
    public static void printMatrix(int[][] A, PrintWriter out) {
        for (int[] row : A) {
            for (int val : row) {
                out.printf("%3d ", val);
            }
            out.println();
        }
    }

    public static void findMostDistantRow(int[][] A, PrintWriter out) {
        int n = A.length;
        int m = A[0].length;
        double maxDist = -1;
        int maxRow = 1;
        for (int i = 1; i < n; i++) {
            double sumSq = 0;
            for (int j = 0; j < m; j++) {
                double diff = A[i][j] - A[0][j];
                sumSq += diff * diff;
            }
            double dist = Math.sqrt(sumSq);
            if (dist > maxDist) {
                maxDist = dist;
                maxRow = i + 1;
            }
        }
        out.println("Номер строки, максимально удалённой от первой: " + maxRow);
    }

    public static void findMinOfLocalMax(int[][] A, PrintWriter out) {
        int n = A.length;
        int m = A[0].length;
        List<Integer> localMax = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (isLocalMax(A, i, j)) {
                    localMax.add(A[i][j]);
                }
            }
        }
        if (localMax.isEmpty()) {
            out.println("Локальных максимумов нет.");
            return;
        }
        int min = Collections.min(localMax);
        out.println("Минимум среди локальных максимумов: " + min);
    }

    private static boolean isLocalMax(int[][] A, int i, int j) {
        int n = A.length;
        int m = A[0].length;
        int val = A[i][j];
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = i + di, nj = j + dj;
                if (ni >= 0 && ni < n && nj >= 0 && nj < m) {
                    if (A[ni][nj] >= val) return false;
                }
            }
        }
        return true;
    }

    public static void removeRowColWithMaxAbs(int[][] A, PrintWriter out) {
        int n = A.length;
        int maxRow = 0, maxCol = 0;
        int maxAbs = Math.abs(A[0][0]);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < A[i].length; j++) {
                if (Math.abs(A[i][j]) > maxAbs) {
                    maxAbs = Math.abs(A[i][j]);
                    maxRow = i;
                    maxCol = j;
                }
            }
        }
        out.println("Максимальный по модулю элемент: " + A[maxRow][maxCol] +
                " на позиции (" + (maxRow + 1) + ", " + (maxCol + 1) + ")");
        int[][] B = new int[n - 1][A[0].length - 1];
        int bi = 0;
        for (int i = 0; i < n; i++) {
            if (i == maxRow) continue;
            int bj = 0;
            for (int j = 0; j < A[i].length; j++) {
                if (j == maxCol) continue;
                B[bi][bj++] = A[i][j];
            }
            bi++;
        }
        out.println("Новая матрица порядка " + (n - 1) + ":");
        printMatrix(B, out);
    }
}
