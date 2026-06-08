import java.util.*;

public class PalindromeTwelveandTwoTerms {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();        
        while(tc-- > 0){
            long n = sc.nextLong();
            
            int rem = (int)(n % 12);
            if(rem == 0){
                System.out.println("0 " + n);
            } else if(rem == 1){
                System.out.println("1 " + (n - 1));
            } else if(rem == 2){
                System.out.println("2 " + (n - 2));
            } else if(rem == 3){
                System.out.println("3 " + (n - 3));
            } else if(rem == 4){
                System.out.println("4 " + (n - 4));
            } else if(rem == 5){
                System.out.println("5 " + (n - 5));
            } else if(rem == 6){
                System.out.println("6 " + (n - 6));
            } else if(rem == 7){
                System.out.println("7 " + (n - 7));
            } else if(rem == 8){
                System.out.println("8 " + (n - 8));
            } else if(rem == 9){
                System.out.println("9 " + (n - 9));
            } else if(rem == 10){
                System.out.println(n >= 22?"22 " + (n - 22):"-1");
            } else {
                System.out.println("11 " + (n - 11));
            }
        }
    }
}