import java.util.*;

public class Koshary {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();

        while(tc-- > 0){
            int a = sc.nextInt();
            int b = sc.nextInt();

            if(a % 2 != 0 && b % 2 != 0){
                System.out.println("NO");
            } else {
                System.out.println("YES");
            }
        }
    }
}