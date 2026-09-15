import java.util.*;

public class GetMaximumAmount {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int m = sc.nextInt();

        int[] q = new int[n];

        int max = 0;

        for (int i = 0; i < n; i++) {
            q[i] = sc.nextInt();
            max = Math.max(max, q[i]);
        }

        int low = 1;
        int high = max;

        // Find the largest threshold x
        // such that at least m items have price >= x
        while (low <= high) {

            int mid = low + (high - low) / 2;

            long count = 0;

            for (int x : q) {
                if (x >= mid) {
                    count += x - mid + 1;
                }
            }

            if (count >= m) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        int threshold = high;

        long revenue = 0;
        long count = 0;

        for (int x : q) {

            if (x >= threshold + 1) {
                long k = x - threshold;

                // x + (x-1) + ... + (threshold+1)
                revenue += k * (x + threshold + 1) / 2;
                count += k;
            }
        }

        // We may have taken more than m items.
        // Fill the remaining items at 'threshold'.
        revenue += (m - count) * threshold;

        System.out.println(revenue);
    }
}

// import java.util.*;

// public class GetMaximumAmount {
//     public static void main(String[] args) {
//       Scanner sc = new Scanner(System.in);
//       long n = sc.nextLong(), m = sc.nextLong();
//       long tot = 0;
//       PriorityQueue<Long> pq = new PriorityQueue<>(Collections.reverseOrder());
//       for(int i=0;i<n;i++) {
//         pq.offer(sc.nextLong());
//       }
//       while(m-->0) {
//         long a = pq.poll();
//         tot += a;
//         pq.offer(--a);
//       }

//       System.out.println(tot);
//     }
// }