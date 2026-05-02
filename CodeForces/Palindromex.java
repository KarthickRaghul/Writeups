import java.util.*;

public class Palindromex {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();

        while(tc-- > 0){
            int n  = sc.nextInt();

            int[] arr =new int[2 * n];
            int lZero = -1, rZero = -1;

            for (int i = 0; i<2 * n; i++) {
                arr[i] = sc.nextInt();
                if (arr[i] == 0) {
                    if (lZero == -1) {
                        lZero = i;
                    } else {
                        rZero = i;
                    }
                }
            }

            int maxMex = 0;

            maxMex = Math.max(maxMex,help(lZero, lZero, arr, n));
            maxMex = Math.max(maxMex, help(rZero, rZero, arr, n));
            int sum = lZero + rZero;
            maxMex = Math.max(maxMex, help(sum / 2, sum - (sum / 2), arr, n));
            System.out.println(maxMex);
        }
    }

    static int help(int l, int r, int[] arr, int n) {
        if (arr[l] != arr[r]) {
            return 0;
        }

        while (l - 1 >= 0 && r + 1 < 2 * n && arr[l - 1] == arr[r + 1]) {
            l--;
            r++;
        }

        boolean[] present = new boolean[n + 1];
        for (int i = l; i <= r; i++) {
            if (arr[i] <= n) {
                present[arr[i]] = true;
            }
        }

        int mex = 0;
        while (present[mex]) {
            mex++;
        }
        
        return mex;
    }
}