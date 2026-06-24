import java.util.*;

public class AIProjectDevelopment {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();        
        while(tc-- > 0){
            int n = sc.nextInt();
            int x = sc.nextInt();
            int y = sc.nextInt();
            int z = sc.nextInt();

            long timeWithoutAI = (n + x + y - 1) / (x + y);
            long timeWithAI;
            
            
            long rem = n - (z * x);
            long combined = x + 10 * y;
            timeWithAI = z + (rem + combined - 1) / combined;
            
            System.out.println(Math.min(timeWithoutAI, timeWithAI));
        }
    }
}