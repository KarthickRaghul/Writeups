import java.util.*;

public class LargestSumCycle {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int edge[] = new int[n];
        for(int i=0;i<n;i++) {
            edge[i] = sc.nextInt();
        }

        System.out.println(func(n, edge));
    }
    
    private static long func(int n, int[] edges) {
        
        int visited[] = new int[n];
        long sum[] = new long[n];
        
        int time = 1;
        long ans = -1;

        for(int i=0;i<n;i++) {

            int node = edges[i];
            if(node == -1 || visited[node] != 0)
                continue;
            
            long startTime = time;
            long currCost = 0;

            while(node != -1 && visited[node] == 0) {

                sum[node] = currCost;
                currCost += node;

                visited[node] = time++;
                node = edges[node];
            }

            if(node != -1 && visited[node] >= startTime) {
                ans = Math.max(ans, currCost - sum[node]);
            }
        }

        return ans;
    } 
}
