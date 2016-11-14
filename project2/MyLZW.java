/*************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    (compress)
 *                java MyLZW - n < input.txt > input.lzw (do nothing mode)
 *                java MyLZW - r < input.txt > input.lzw (reset mode)
 *                java MyLZW - m < input.txt > input.lzw (monitor mode)
 *
 *  Execution:    java MyLZW + < input.lzw > input2.txt (expand)
 *
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *  Depending on the command, compression may continue to use a full codebook(n),
 *  reset when it is full(r), or reset when the compression ratio degrades(m).
 *
 * This is a modified version of LZW.java
 * @author Craig Mazzotta
 *************************************************************************/

 import java.util.*;

 public class MyLZW {

     private static int mode = 0;
     private static final int MIN_CODE_WIDTH = 9;
     private static final int MAX_CODE_WIDTH = 16;
     private static final int R = 256; // number of input chars
     private static int L = 512; // number of codewords
     private static int W = MIN_CODE_WIDTH; // codeword width

     public static void compress() {
         int inBits = 0;
         int outBits = 0;
         float lastRatio = 1;
         float currentRatio = 0;
         boolean monitor = false;

         BinaryStdOut.write(mode, 2); // Write mode to front of compressed file
         String input = BinaryStdIn.readString();
         TST<Integer> st = resetCodebookCompress(); // Initialize table
         int code = R + 1;  // R is codeword for EOF

         while (input.length() > 0) {
             String s = st.longestPrefixOf(input);  // Find max prefix match s.
             BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
             int t = s.length();
             inBits += t * 8; //increment bits read in
             outBits += W; //increment bits

             if (t < input.length()) { // Add s to symbol table.
                 if (code >= L) {
                     boolean full = !resizeCodeWidth(W + 1); // resize  width
                     if (full) {// if W >= MAX_CODE_WIDTH
                         boolean clearBook = false;
                         if (mode == 1) { // Reset mode
                             clearBook = true;
                         } else if (mode == 2) { // Monitor mode
                             currentRatio = inBits / (float) outBits;
                             if (!monitor) { // Start monitor
                                 lastRatio = currentRatio;
                                 monitor = true;
                             } else if (lastRatio / currentRatio > 1.1) {
                                 clearBook = true;
                                 monitor = false;
                             }
                         }
                         if (clearBook) { // Clear the codebook and reset codewidth
                             st = resetCodebookCompress();
                             code = R + 1;
                             resizeCodeWidth(MIN_CODE_WIDTH);
                         }
                     }
                 }
                 if (code < L) {
                     st.put(input.substring(0, t + 1), code++); // Add s
                 }
             }
             input = input.substring(t);            // Scan past s in input.
         }
         BinaryStdOut.write(R, W);
         BinaryStdOut.close();
     }

     public static void expand() {
         int inBits = 0;
         int outBits = 0;
         float currentRatio = 0;
         float lastRatio = 1;
         boolean monitor = false;

         mode = BinaryStdIn.readInt(2); // Read mode
         ArrayList<String> st = resetCodebookExpand(); // Initialize table
         int i = R + 1;

         int codeword = BinaryStdIn.readInt(W);
         if (codeword == R) {
             return;
         }
         String val = st.get(codeword);

         while (true) {
             inBits += val.length() * 8;
             outBits += W;
             if (i >= L) {
                 boolean codebookFull = !resizeCodeWidth(W + 1);	// check if codeword needs resized
                 if (codebookFull) {
                     boolean clearCodeBook = false;
                     if (mode == 1) { // Reset Mode
                         clearCodeBook = true;
                     } else if (mode == 2) { // Monitor mode
                         currentRatio = inBits / (float) outBits;
                         if (!monitor) { // Start monitoring
                             lastRatio = currentRatio;
                             monitor = true;
                         } else if (lastRatio / currentRatio > 1.1) {
                             clearCodeBook = true;
                             monitor = false;
                         }
                     }
                     if (clearCodeBook) {
                         st = resetCodebookExpand();
                         i = R + 1;
                         resizeCodeWidth(MIN_CODE_WIDTH);
                     }
                 }
             }
             BinaryStdOut.write(val);
             codeword = BinaryStdIn.readInt(W);
             if (codeword == R) {
                 break;
             }
             String s = null;
             if (i == codeword) {
                 s = val + val.charAt(0);
             } else {
                 s = st.get(codeword);
             }

             if (i < L) {
                 st.add(val + s.charAt(0));
                 i++;
             }
             val = s;
         }
         BinaryStdOut.close();
     }

     /*
     * @return table The codebook TST containing all one character prefixes for compress()
     */
     public static TST<Integer> resetCodebookCompress() {
         int i = 0;
         TST<Integer> table = new TST<Integer>();
         while (i < R) {
             table.put("" + (char) i, i);
             i++;
         }
         return table;
     }

     /*
     * @return list The codebook list containing all one character prefixes for expand()
     */
     public static ArrayList<String> resetCodebookExpand() {
         ArrayList<String> list = new ArrayList<String>();
         int i = 0;
         while (i < R) {
             list.add("" + (char) i);
             i++;
         }
         list.add("");	// (unused) lookahead for EOF
         return list;
     }

     /*
     * Determines if codeword can be resized and calculates new codebook size
     * based on larger codeword width
     *
     * @param newWidth Current codeword width(in bits) + 1
     * @return boolean True if codebook is expandable; false if newWidth > MAX_CODE_WIDTH
     */
     public static boolean resizeCodeWidth(int newWidth) {
         if (newWidth <= MAX_CODE_WIDTH) {
             W = newWidth;
             L = (int) Math.pow(2, W); // recalculate L=2^W
             return true;
         } else {
             return false;
         }
     }

     /*
     * Main method that handles user input to compress or expand the file.
     * See lines 1-15 for compilation/execution instructions.
     *
     * @param args User entered arguments
     */
     public static void main(String[] args) {
         switch (args[0]) {
             case "-":
                 String modeChoice = args[1].toLowerCase();
                 switch (modeChoice) {
                     case "n": // Do nothing mode
                         mode = 0;
                         break;
                     case "r": // Reset mode
                         mode = 1;
                         break;
                     case "m": // Monitor mode
                         mode = 2;
                         break;
                 }
                 compress();
                 break;
             case "+":
                 expand();
                 break;
             default:
                 throw new IllegalArgumentException("Illegal command line argument");
         }
     }

 }
