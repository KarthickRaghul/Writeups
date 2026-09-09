import java.util.*;

class Solution {
    public long largesSumCycle(int n, int edge[]) {
        
        long sum[] = new long[n];
        int visited[] = new int[n];
        
        int t = 1;
        long ans = -1;
        for(int i=0;i<n;i++) {
            
            if(visited[i] != 0 || edge[i] == -1)
                continue;
            
            int node = i;
            int startingVal = t;
            long currSum = 0;
            
            while(node != -1 && visited[node] == 0) {
                sum[node] = currSum;
                currSum += node;
                
                visited[node] = t++ ;
                node = edge[node];
            }
            
            if(node != -1 && visited[node] >= startingVal){
                ans = Math.max(ans, currSum - sum[node]);
            }
        }
        
        return ans;
    }
}

class largestSumCycle_ {
    public static void main (String[] args) {
        Solution soln = new Solution();
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int arr[] = new int[n];
        for(int i=0;i<n;i++) 
            arr[i] = sc.nextInt();

        System.out.println(soln.largesSumCycle(n,arr));
    }
}