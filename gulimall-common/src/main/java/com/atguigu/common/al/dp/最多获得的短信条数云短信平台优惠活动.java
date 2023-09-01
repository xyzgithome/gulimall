package com.atguigu.common.al.dp;

import java.util.Arrays;

public class 最多获得的短信条数云短信平台优惠活动 {
}
import java.util.Arrays;
        import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int m = Integer.parseInt(sc.nextLine());
        Integer[] p =
                Arrays.stream(sc.nextLine().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);

        System.out.println(getResult(m, p));
    }

    public static int getResult(int m, Integer[] p) {
        int[] dp = new int[m + 1];

        for (int i = 0; i <= p.length; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 || j == 0 || j < i) continue;
                dp[j] = Math.max(dp[j], dp[j - i] + p[i - 1]);
            }
        }

        return dp[m];
    }
}