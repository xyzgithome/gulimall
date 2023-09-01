package com.atguigu.common.al.dp;

public class 称砝码 {
}
import java.util.Scanner;
        import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[] m = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            m[i] = sc.nextInt();
        }

        int[] x = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            x[i] = sc.nextInt();
        }

        System.out.println(getResult(n, m, x));
    }

    public static int getResult(int n, int[] m, int[] x) {
        int bag = 0;
        for (int i = 1; i <= n; i++) bag += m[i] * x[i];

        boolean[] dp = new boolean[bag + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = bag; j >= m[i]; j--) {
                for (int k = 1; k <= x[i]; k++) {
                    if (j >= m[i] * k) {
                        if (dp[j - m[i] * k]) dp[j] = true;
                    }
                }
            }
        }

        int count = 0;
        for (boolean flag : dp) {
            if (flag) count++;
        }

        return count;
    }
}