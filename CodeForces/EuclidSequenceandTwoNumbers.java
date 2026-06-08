import java.util.*;

public class EuclidSequenceandTwoNumbers {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();
        while(tc-- > 0){
            int n = sc.nextInt();
            int arr[] = new int[n];
            for(int i = 0; i < n; i++){
                arr[i] = sc.nextInt();
            }
            Arrays.sort(arr);
            boolean valid = true;
            for(int i=n-3 ;i>=0; i--){
                if(arr[i] != arr[i+2] % arr[i+1]){
                    valid = false;
                    break;
                }
            }
            if(valid){
                System.out.println(arr[n-1]+" "+arr[n-2]);
            } else {
                System.out.println("-1");
            }
        }
    }
}