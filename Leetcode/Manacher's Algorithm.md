
```java
public int[] manacher(String s) {

    StringBuilder T = new StringBuilder("^#");
    for (char c : s.toCharArray()) {
        T.append(c).append("#");
    }
    T.append("$");

    int[] P = new int[T.length()];
    int C = 0, R = 0;

    for (int i = 1; i < T.length() - 1; i++) {

        int mirr = 2 * C - i;

        if (i < R) {
            P[i] = Math.min(R - i, P[mirr]);
        }

        while (T.charAt(i + (1 + P[i])) == T.charAt(i - (1 + P[i]))) {
            P[i]++;
        }

        if (i + P[i] > R) {
            C = i;
            R = i + P[i];
        }
    }

    return P;
}


```