import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long num = scanner.nextLong();

        int n = 0;
        long tmp = num;
        while (tmp > 0) {
            n++;
            tmp /= 10;
        }

        long[] powers = new long[n];
        if (n > 0) {
            powers[n - 1] = 1;
            for (int i = n - 2; i >= 0; i--) {
                powers[i] = powers[i + 1] * 10;
            }
        }

        int sum_old = 0;
        tmp = num;
        for (int i = 0; i < n; i++) {
            int digit = (int) ((num / powers[i]) % 10);
            sum_old += digit;
        }

        for (int i = 0; i < n; i++) {
            int old_digit = (int) ((num / powers[i]) % 10);
            long multiplier = powers[i];

            for (int alt_digit = 0; alt_digit <= 9; alt_digit++) {

                if (alt_digit == old_digit) continue;
                if (i == 0 && alt_digit == 0) continue;

                int sum_new = sum_old - old_digit + alt_digit;

                if (sum_new % 9 == 0) {
                    long newNumber = num - (long) old_digit * multiplier + (long) alt_digit * multiplier;
                    System.out.println(newNumber);
                }
            }
        }
    }
}