import java.util.*;

class Solution {
    int func(int n, int[] edges) {
        
        long iw[] = new long[n];
        long max = 0;
        int idx = 0;

        for(int i=0;i<n;i++) {
            int node = edges[i];
            if(node == -1)
                continue;
            iw[node] += i;
            if(iw[node] >= max) {
                max = iw[node];
                idx = node;
            }
        }

        return idx;
    }
}
class MaximumWeightNode {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int edge[] = new int[n];
        for(int i=0;i<n;i++) {
            edge[i] = sc.nextInt();
        }

        Solution s = new Solution();
        System.out.println(s.func(n,edge));
        sc.close();
    }    
}