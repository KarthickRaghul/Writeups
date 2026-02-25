import java.util.*;

public class BeautifulYear {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        
        int year = sc.nextInt(); 
        int copy = year , len = 0;
        while(copy>0){
            len++;
            copy/=10;
        }
        while (true) { 
            year++;
            copy = year;
            HashSet<Integer> set = new HashSet<>();
            while(copy > 0){
                set.add(copy%10);
                copy/=10;
            }
            if(set.size() >= len) break;
        }
        System.out.println(year);
    }
}