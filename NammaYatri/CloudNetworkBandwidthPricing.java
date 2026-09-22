import java.util.*;

public class CloudNetworkBandwidthPricing {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int arr[][] = new int[n][4];
        for(int i=0;i<n;i++) {
            for(int j=0;j<4;j++) {
                arr[i][j] = sc.nextInt();
            }
        }
        
        long total = 0L;
        for(int x[] : arr) {
            if(x[0] == 1) {
                update(x[1],x[2],x[3]);
            }else{
                total += find(x[1],x[2]);
            }
        }

        System.out.println(total);
        sc.close();
    }

    static HashMap<Integer,Long> map = new HashMap<>();

    static void update(int a, int b, int val) {

        while(a != b) {
            if(a > b) {
                map.merge(a,(long) val,Long::sum);
                a/=2;
            }else {
                map.merge(b,(long) val,Long::sum);
                b/=2;
            }
        }
    }

    static long find(int a, int b) {

        long ans = 0;
        while(a != b) {
            if(a > b) {
                ans += map.getOrDefault(a,0L);
                a/=2;
            }else {
                ans += map.getOrDefault(b,0L);
                b/=2;
            }
        }
        return ans;
    }

}