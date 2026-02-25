
## Strings 

- `Character.isAlphabetic()`   or   `Character.isLetter()**`
- `givenstring.toLowerCase()` 
- `givenstring.replaceAll(regex,towhat)`
- `givenstring.startsWith(string)` --> returns boolean
- `Integer.parseInt(number,base)` --> Integer to some other base
- `Integer.toBinaryString(val)`

## Array

- `System.out.println(Arrays.deepToString(arr))`  -> to print 2d arrays  
- `int r = Arrays.stream(piles).max().getAsInt();`
- `Arrays.sort(matrix, (a, b) -> Integer.compare(a[0], b[0]))`

### Priority Queue



### HashMap

- ` map.computeIfAbsent(key, k -> new ArrayList<>()).add(frequency);` 

### General
- `Integer.bitCount()` - to find the number of ones in the binary value of that integer
- `Integer.numberOfLeadingZeros(int i)`:  Returns the number of zero bits preceding the highest-order ("leftmost") one-bit.  
- `Integer.numberOfTrailingZeros(int i)`:  Returns the number of zero bits following the lowest-order ("rightmost") one-bit  