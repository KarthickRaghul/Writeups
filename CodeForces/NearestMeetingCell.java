import java.util.*;

class NearestMeetingCell {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int edges[] = new int[n];
        for(int i=0;i<n;i++) {
            edges[i] = sc.nextInt();
        }
        int a = sc.nextInt(), b = sc.nextInt();
        System.out.println(func(edges, a, b));

    }

    private static int func(int[] edges, int a , int b) {

        int dist1[] = findDist(edges, a);
        int dist2[] = findDist(edges, b);

        int idx = -1, max = Integer.MAX_VALUE ;
        for(int i=0;i<edges.length;i++) {
            
            if(dist1[i] != -1 && dist2[i] != -1) {
                int currMax = Math.max(dist1[i], dist2[i]);

                if(currMax < max) {
                    max = currMax;
                    idx = i;
                } 
            }
        }

        return idx;
    }

    private static int[] findDist(int[] edges, int a) {

        int node = a;
        int count = 0;

        int[] dist = new int[edges.length];
        Arrays.fill(dist, -1);
        while(node != -1 && dist[node] == -1) {
            dist[node] = count++;
            node = edges[node];
        }

        return dist;
    }
}
