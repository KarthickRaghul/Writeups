import java.util.*;

public class ICPCBallons {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        
        int tc = sc.nextInt();
        while (tc-->0){
            boolean freq [] = new boolean[26];
            int n = sc.nextInt();
            sc.nextLine();
            String s = sc.nextLine();

            int count = 0;
            for(char ch : s.toCharArray()){
                if(freq[ch-'A']) count ++;
                else {
                    freq[ch-'A'] = true;
                    count+=2;
                }
            }

            System.out.println(count);
        }
    }
}