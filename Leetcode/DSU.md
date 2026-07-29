
```java
public class DSU{

    int parent[] ;
    int rank[] ;
    int size[] ;

    DSU(int n){
        parent = new int[n];
        rank = new int[n];
        size = new int[n];

        for(int i=0;i<n;i++){
            parent[i] = i;
            rank[i] = 0;
            size[i] = 1;
        }
    }

    int find (int i) {
        if( i == parent[i] )
            return i;

        return parent[i] = find (parent[i]);
    } 

    void unionR (int u , int v) {
        
        int pu = find(u) , pv = find(v);

        if(pu == pv )  return ;

        if(rank[pu] < rank[pv]) 
            parent[pu] = pv ;
        else if ( rank[pv] < rank[pu])
            parent[pv] = pu ;
        else {
            parent [pv] = pu;
            rank[pu] ++ ;
        }
    }

    void unionS (int u,  int v) {
        int pu = find(u) , pv = find(v);

        if(pu == pv )  return ;

        if(size[pu] < size[pv]) {
            parent[pu] = pv ;
            size[pv] += size[pu];
        }
        else {
            parent[pv] = pu ;
            size[pu] += size[pv];
        }
    }
}
```

