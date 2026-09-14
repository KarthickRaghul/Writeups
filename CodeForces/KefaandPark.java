import java.util.*;

public class KefaandPark{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt() , m = sc.nextInt();
        int cats[] = new int[n];

        ArrayList<Integer>[] graph = new ArrayList[n];
        for(int i=0;i<n;i++) {
            graph[i] = new ArrayList<>();
        }

        for(int i=0;i<n;i++) 
            cats[i] = sc.nextInt();
        
        for(int i=0;i<n-1;i++) {
            int u = sc.nextInt() - 1;
            int v = sc.nextInt() - 1;

            graph[u].add(v);
            graph[v].add(u);
        }

        boolean visited[] = new boolean[n]; 
        System.out.println(dfs(graph, cats, visited, m, 0, 0));
        sc.close();
                
    }

    static int dfs (ArrayList<Integer>[] graph,int[] cats, boolean[] visited, int m, int node, int count) {

        int c = cats[node];
        if(visited[node] || count + c > m)
            return 0;

        visited[node] = true;

        boolean isLeaf = true;

        int returnVal = 0;
        for(int adj : graph[node]) {
            if(!visited[adj]) {
                isLeaf = false;
                int continuous = c == 0 ? 0 : count + 1; 
                returnVal += dfs(graph, cats, visited, m, adj, continuous);
            }
        }

        if(isLeaf)
            return 1;

        return returnVal;
    }
}

