import java.util.*;

public class Snowfall {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int tc = sc.nextInt();

        while(tc-- > 0){
            int n  = sc.nextInt();

            List<Integer> twos = new ArrayList<>();
            List<Integer> threes = new ArrayList<>();
            List<Integer> sixes = new ArrayList<>();
            List<Integer> others = new ArrayList<>();
            
            for(int i = 0; i < n; i++){
                int num = sc.nextInt();
                if(num % 6 == 0){
                    sixes.add(num);
                }
                else if(num % 2 == 0){
                    twos.add(num);
                } else if(num % 3 == 0){
                    threes.add(num);
                } else {
                    others.add(num);
                }
            }

            for(int x : sixes){
                System.out.print(x + " ");
            }
            for(int x : twos){    
                System.out.print(x + " ");
            }
            for(int x : others){
                System.out.print(x + " ");
            }
            for(int x : threes){
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }
}