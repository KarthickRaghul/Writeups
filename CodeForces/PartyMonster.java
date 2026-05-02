import java.util.*;

public class PartyMonster {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();

        while(tc-- > 0){
            int n  = sc.nextInt();
            String s = sc.next();

    
            int open = 0, close = 0;   
            for(char ch : s.toCharArray()){
                if(ch == '('){
                    open++;
                } else if(ch == ')'){
                    close++;
                }
            }

            if(open == close){
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }
}