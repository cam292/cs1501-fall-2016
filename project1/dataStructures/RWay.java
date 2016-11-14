package dataStructures;
import dataStructures.Queue;
import dataStructures.Alphabet;
/**
 *  The RWay class represents an symbol table of key-value
 *  pairs, with string keys and generic values.
 *  It supports the usual put,get,contains,delete,size, and is-empty methods.
 *  It also provides character-based methods for finding the string
 *  in the symbol table that is the longest prefix of a given prefix,
 *  finding all strings in the symbol table that start with a given prefix,
 *  and finding all strings in the symbol table that match a given pattern.
 *  A symbol table implements the associative array abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  This class uses the convention that values cannot be null—setting. The
 *  value associated with a key to null is equivalent to deleting the key
 *  from the symbol table.
 *
 * The RWay class is used to store valid passwords(keys) and their time
 *    to find(value) from pw_check.java
 *
 *  This is a modified version of TrieST.java supplied by the textbook:
 *  Algorithms, 4th Ed. by Robert Sedgewick and Kevin Wayne
 *
 * @author Craig Mazzotta
 */

public class RWay<Value> {

    public static final Alphabet validChars = new Alphabet("abcdefghijklmnopqrstuvwxyz0123456789!@$^_*");
    private static int R = validChars.radix();

    private Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    /**
      * Initializes an empty string symbol table.
      */
     public RWay() {}

    /**
     * Returns the value associated with the given key.
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     *     and null if the key is not in the symbol table
     * @throws NullPointerException if key is null
     */
    public Value get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    /**
     * Does this symbol table contain the given key?
     * @param key the key
     * @return true if this symbol table contains key and
     *      false otherwise
     * @throws NullPointerException if key is null
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        int index = validChars.toIndex(c);
        return get(x.next[index], key, d+1);
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is null, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws NullPointerException if key is null
     */
    public void put(String key, Value val) {
        if (val == null) delete(key);
        else root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (x.val == null) n++;
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        int index = validChars.toIndex(c);
        x.next[index] = put(x.next[index], key, val, d+1);
        return x;
    }

    /**
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Is this symbol table empty?
     * @return true if this symbol table is empty and false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns all keys in the symbol table as an {@code Iterable}.
     * To iterate over all of the keys in the symbol table named {@code st},
     * use the foreach notation: {@code for (Key key : st.keys())}.
     * @return all keys in the symbol table as an {@code Iterable}
     */
    public Queue<String> keys() {
        return keysWithPrefix("");
    }

    /**
     * Returns all of the keys in the set that start with the supplied prefix.
     * @param prefix the prefix
     * @return all of the keys in the set that start with prefix,
     *     as an iterable
     */
    public Queue<String> keysWithPrefix(String prefix) {
        Queue<String> results = new Queue<String>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) {
          return;
        }
        if (x.val != null) {
          results.enqueue(prefix.toString());
        }
        for (int c = 0; c < R; c++) {
            char ch = validChars.toChar(c);
            prefix.append(ch);
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Returns all of the keys in the symbol table that match the pattern,
     * where . symbol is treated as a wildcard character.
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match pattern,
     *     as an iterable, where . is treated as a wildcard character.
     */
    public Queue<String> keysThatMatch(String pattern) {
        Queue<String> results = new Queue<String>();
        collect(root, new StringBuilder(), pattern, results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, String pattern, Queue<String> results) {
        if (x == null) return;
        int d = prefix.length();
        if (d == pattern.length() && x.val != null)
            results.enqueue(prefix.toString());
        if (d == pattern.length())
            return;
        char c = pattern.charAt(d);
        if (c == '.') {
            for (char ch = 0; ch < R; ch++) {
                prefix.append(ch);
                int index = validChars.toIndex(ch);
                collect(x.next[index], prefix, pattern, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        else {
            prefix.append(c);
            int index = validChars.toIndex(c);
            collect(x.next[index], prefix, pattern, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Returns the string in the symbol table that is the longest prefix of query,
     * or null, if no such string.
     * @param query the query string
     * @return the string in the symbol table that is the longest prefix of query,
     *     or null if no such string
     * @throws NullPointerException if query is null
     */
     public String longestPrefixOf(String query) {
         int length = longestPrefixOf(root, query, 0, -1);
         if (length == -1) return null;
         else return query.substring(0, length);
     }

    // returns the length of the longest string key in the subtrie
    // rooted at x that is a prefix of the query string,
    // assuming the first d character match and we have already
    // found a prefix match of given length (-1 if no such match)
    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null) {
          return length;
        }
        if (x.val == null) {
          length = d;
        }
        if (d == query.length()){
          return length;
        }
        char c = query.charAt(d);
        int index = validChars.toIndex(c);
        return longestPrefixOf(x.next[index], query, d+1, length);
    }

    /**
     * Removes the key from the set if the key is present.
     * @param key the key
     * @throws NullPointerException if key is null
     */
    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            if (x.val != null) n--;
            x.val = null;
        }
        else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d+1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.val != null) return x;
        for (int c = 0; c < R; c++)
            if (x.next[c] != null)
                return x;
        return null;
    }

}
