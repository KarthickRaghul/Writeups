```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Solution {

    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        String nextLine() {
            String str = "";
            try {
                if (st != null && st.hasMoreTokens()) {
                    str = st.nextToken("\n"); 
                } else {
                    str = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }

        int nextInt() { 
            return Integer.parseInt(next()); 
        }
        
        long nextLong() { 
            return Long.parseLong(next()); 
        }
        
        double nextDouble() { 
            return Double.parseDouble(next()); 
        }
        
        float nextFloat() { 
            return Float.parseFloat(next()); 
        }
        
        boolean nextBoolean() { 
            return Boolean.parseBoolean(next()); 
        }
        
        short nextShort() { 
            return Short.parseShort(next()); 
        }
        
        byte nextByte() { 
            return Byte.parseByte(next()); 
        }

        char nextChar() {
            return next().charAt(0);
        }
    }

    public static void main(String[] args) {
        FastReader sc = new FastReader();

        // Example usage:
        // int n = sc.nextInt();
        // double d = sc.nextDouble();
        // String line = sc.nextLine();
    }
}
```