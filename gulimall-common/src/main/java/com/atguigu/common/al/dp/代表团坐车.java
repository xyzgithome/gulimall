package com.atguigu.common.al.dp;

import java.util.Arrays;

public class 代表团坐车 {
}
import java.util.Arrays;
        import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Integer[] nums =
                Arrays.stream(sc.nextLine().split(",")).map(Integer::parseInt).toArray(Integer[]::new);

        int bag = Integer.parseInt(sc.nextLine());

        System.out.println(getResult(nums, bag));
    }

    private static int getResult(Integer[] nums, int bag) {
        int n = nums.length;

        int[][] dp = new int[n + 1][bag + 1];
        dp[0][0] = 1;

        for (int i = 1; i <= n; i++) {
            int num = nums[i - 1];
            for (int j = 0; j <= bag; j++) {
                if (j < num) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i - 1][j - num];
                }
            }
        }

        return dp[n][bag];
    }
}