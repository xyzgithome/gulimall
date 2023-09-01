package com.atguigu.common.al.dp;

public class 查找充电设备组合 {
}
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = sc.nextInt();
        }

        int p_max = sc.nextInt();

        System.out.println(getResult(n, p, p_max));
    }

    public static int getResult(int n, int[] p, int p_max) {
        int[][] dp = new int[n + 1][p_max + 1];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= p_max; j++) {
                if (i == 0 || j == 0) continue;

                // 注意这里p[i-1]中i-1不会越界，因为i=0时不会走到这一步，另外i-1是为了防止尾越界，因为dp矩阵的行数是n+1，因此p[i]可能会尾越界，而p[i-1]就不会越界了
                if (p[i - 1] > j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], p[i - 1] + dp[i - 1][j - p[i - 1]]);
                }
            }
        }

        return dp[n][p_max];
    }
}