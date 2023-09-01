package com.atguigu.common.al.dp;

import java.util.ArrayList;

public class 最大报酬 {
}
import java.util.*;

public class Main {
    static ArrayList<Integer[]> nodes;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int T = sc.nextInt();
        int n = sc.nextInt();

        Integer[][] tws = new Integer[n][2];
        for (int i = 0; i < n; i++) {
            tws[i][0] = sc.nextInt();
            tws[i][1] = sc.nextInt();
        }

        System.out.println(getResult(T, tws));
    }

    /**
     *
     * @param T 工作时长
     * @param tws 数组，元素是tw，也是数组，含义为[该工作消耗的时长, 该项工作的报酬]
     * @return 最大报酬
     */
    public static int getResult(int T, Integer[][] tws) {
        int maxI = tws.length + 1;
        int maxJ = T + 1;

        int[][] dp = new int[maxI][maxJ];

        for (int i = 0; i < maxI; i++) {
            for (int j = 0; j < maxJ; j++) {
                if (i == 0 || j == 0) continue; // 第0行或第0列最大报酬保持0

                int t = tws[i - 1][0];// 要选择的工作的[权重]
                int w = tws[i - 1][1];// 要选择的工作的[价值]

                if (t > j) {
                    // 如果要选择的工作的权重 > 当前背包权重，则无法放入背包，最大价值继承自上一行该列值
                    dp[i][j] = dp[i - 1][j];
                } else {
                    // 如果要选择的工作的权重 <= 当前背包权重
                    // 则我们有两种选择
                    // 1、不进行该工作，则最大价值继承自上一行该列值
                    // 2、进行该工作，则纳入该工作的价值w，加上+ 剩余权重，在不进行该工作的范围内，可得的最大价值dp[i - 1][j - t]
                    // 比较两种选择下的最大价值，取最大的
                    dp[i][j] = Math.max(dp[i - 1][j], w + dp[i - 1][j - t]);
                }
            }
        }

        return dp[maxI - 1][maxJ - 1];
    }
}

// 优化后
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int T = sc.nextInt();
        int n = sc.nextInt();

        int[][] tws = new int[n + 1][2];
        for (int i = 1; i <= n; i++) {
            tws[i][0] = sc.nextInt();
            tws[i][1] = sc.nextInt();
        }

        System.out.println(getResult(n, T, tws));
    }

    /**
     * @param T 工作时长
     * @param tws 数组，元素是tw，也是数组，含义为[该工作消耗的时长, 该项工作的报酬]
     * @return 最大报酬
     */
    public static int getResult(int n, int T, int[][] tws) {
        int[][] dp = new int[n + 1][T + 1];

        for (int i = 1; i <= n; i++) {
            int t = tws[i][0]; // 要选择的工作的[时长]
            int w = tws[i][1]; // 要选择的工作的[价值]
            for (int j = 1; j <= T; j++) {
                // 注意这里j不能从t开始，会遗漏处理j < t的情况
                if (j < t) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], w + dp[i - 1][j - t]);
                }
            }
        }

        return dp[n][T];
    }
}