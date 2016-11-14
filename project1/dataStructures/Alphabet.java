package dataStructures;
/**
*  A data type for alphabets, for use with string-processing code
*  that must convert between an alphabet of size R and the integers
*  0 through R-1.
*
*  The class is used in pw_check.java and RWay.java to declare
*  valid characters to be used in a passwords
*
*  Class provided by Algorithms, 4th Ed. by Robert Sedgewick and Kevin Wayne
*  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
*/
public class Alphabet {
    /**
     *  The lowercase alphabet { a, b, c, ..., z }.
     */
    public static final Alphabet PASSWORD = new Alphabet("abcdefghijklmnopqrstuvwxyz0123456789!@$^_*");

    private char[] alphabet;     // the characters in the alphabet
    private int[] inverse;       // indices
    private final int R;         // the radix of the alphabet

    /**
     * Initializes a new alphabet from the given set of characters.
     *
     * @param alpha the set of characters
     */
    public Alphabet(String alpha) {
        // check that alphabet contains no duplicate chars
        boolean[] unicode = new boolean[Character.MAX_VALUE];
        for (int i = 0; i < alpha.length(); i++) {
            char c = alpha.charAt(i);
            if (unicode[c])
                throw new IllegalArgumentException("Illegal alphabet: repeated character = '" + c + "'");
            unicode[c] = true;
        }

        alphabet = alpha.toCharArray();
        R = alpha.length();
        inverse = new int[Character.MAX_VALUE];
        for (int i = 0; i < inverse.length; i++)
            inverse[i] = -1;

        // can't use char since R can be as big as 65,536
        for (int c = 0; c < R; c++)
            inverse[alphabet[c]] = c;
    }

    /**
     * Returns true if the argument is a character in this alphabet.
     *
     * @param  c the character
     * @return {@code true} if {@code c} is a character in this alphabet;
     *         {@code false} otherwise
     */
    public boolean contains(char c) {
        return inverse[c] != -1;
    }

    /**
     * Returns the number of characters in this alphabet (the radix).
     *
     * @return the number of characters in this alphabet
     */
    public int radix() {
        return R;
    }

    /**
     * Returns the binary logarithm of the number of characters in this alphabet.
     *
     * @return the binary logarithm (rounded up) of the number of characters in this alphabet
     */
    public int lgR() {
        int lgR = 0;
        for (int t = R-1; t >= 1; t /= 2)
            lgR++;
        return lgR;
    }

    /**
     * Returns the index corresponding to the argument character.
     *
     * @param  c the character
     * @return the index corresponding to the character {@code c}
     * @throws IllegalArgumentException unless {@code c} is a character in this alphabet
     */
    public int toIndex(char c) {
        if (c >= inverse.length || inverse[c] == -1) {
            throw new IllegalArgumentException("Character " + c + " not in alphabet");
        }
        return inverse[c];
    }

    /**
     * Returns the indices corresponding to the argument characters.
     *
     * @param  s the characters
     * @return the indices corresponding to the characters {@code s}
     * @throws IllegalArgumentException unless every character in {@code s}
     *         is a character in this alphabet
     */
    public int[] toIndices(String s) {
        char[] source = s.toCharArray();
        int[] target  = new int[s.length()];
        for (int i = 0; i < source.length; i++)
            target[i] = toIndex(source[i]);
        return target;
    }

    /**
     * Returns the character corresponding to the argument index.
     *
     * @param  index the index
     * @return the character corresponding to the index {@code index}
     * @throws IllegalArgumentException unless {@code 0 <= index < R}
     */
    public char toChar(int index) {
        if (index < 0 || index >= R) {
            throw new IndexOutOfBoundsException("Alphabet index out of bounds");
        }
        return alphabet[index];
    }

    /**
     * Returns the characters corresponding to the argument indices.
     *
     * @param  indices the indices
     * @return the characters corresponding to the indices {@code indices}
     * @throws IllegalArgumentException unless {@code 0 < indices[i] < R}
     *         for every {@code i}
     */
    public String toChars(int[] indices) {
        StringBuilder s = new StringBuilder(indices.length);
        for (int i = 0; i < indices.length; i++)
            s.append(toChar(indices[i]));
        return s.toString();
    }
}
