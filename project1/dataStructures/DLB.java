/**
* This is an implementation of the De Le Briandais trie to store the words from
* dictionary.txt. The end of a valid key is marked by a child node with the character "#".
* Methods include add, search, getChild, getSibling, addSibling, and addChild.
*
* @author Craig Mazzotta
*/
package dataStructures;
import java.util.*;

public class DLB {

	private final char TERMINATOR = '#'; // character to indicate a valid key
	Node rootNode;	// points to the 1st node on the 1st level. It does not have any siblings, and only 1 child node

	/**
	 * Constructor
	 */
	public DLB() {
		rootNode = new Node();
	}

	/**
	* Add a key to the trie.
	*
	* @param s the key to be added
	* @return added True if the key was successfully addded, false otherwise
	*/
	public boolean add(String s) {
		s = s + TERMINATOR; 		// append the terminator to the end of string
		Node currentNode = rootNode;
		boolean added = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			NodeAdded result = addChild(currentNode, c);
			currentNode = result.node;
			added = result.added; // the node is added if it did not exist before
		}
		return added;
	}

	/**
	* Search the DLB trie for the specific key
	*
	* @param key The key being searched for
	* @return 0-3 0 if not a word or prefix, 1 for just a prefix, 2 for just a word,
	*			and 3 for a word and a prefix
	*/
	public int search(StringBuilder key) {
		Node currentNode = rootNode;
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			currentNode = getChild(currentNode, c);

			if (currentNode == null) { // Not a word or a prefix
				return 0;
			}
		}

		// currentNode is at the last chararacter of the string, not the terminator chararacter
		// Find the node with terminator in currentNode's child nodes
		Node terminatorNode = getChild(currentNode, TERMINATOR);
		if (terminatorNode == null) { // The last char in the string does not have a terminator
			return 1;	// Not a word, but a prefix
		}
		else if (terminatorNode.siblingNode == null) { // There is a terminator node, but it does not have siblings
			return 2; // A word, but not a prefix
		}
		else { // There is a terminator node and it has siblings
			return 3; // A word, and a prefix
		}
	}

	/**
	 * Searches for char c in the sibling nodes of this node.
	 * @param siblingStart The first sibling node on the level of search.
	 * @param c The containing char of the node to search for.
	 * @return The sibling node that contains the char. Returns null if not found.
	 */
	private Node getSibling(Node siblingStart, char c) {
		Node nextSibling = siblingStart;
		while (nextSibling != null) {
			if (nextSibling.value == c) { // break if data matches, so the node will be retained

				break;
			}
			nextSibling = nextSibling.siblingNode; 	// go to next node if it ain't the char you are looking for
		}
		return nextSibling;
	}

	/**
	 * Searches for char c in the child nodes of this node
	 * @param parentNode The parent node of the child nodes
	 * @param c The containing char of the node to search for.
	 * @return The child node that contains the char. Returns null if not found.
	 */
	private Node getChild(Node parentNode, char c) {
		return getSibling(parentNode.childNode, c);
	}

	/**
	 * Adds a sibling node on this level.
	 * @param siblingStart The first sibling node on the level.
	 * @param c The char to be added
	 * @return The node added. If a node containing the char already exists, that node will be returned.
	 */
	private NodeAdded addSibling(Node siblingStart, char c) {
		if (siblingStart == null) {
			siblingStart = new Node(c);
			return new NodeAdded(siblingStart, true);
		}
		else {
			Node nextSibling = siblingStart;
			while (nextSibling.siblingNode != null) {
				if (nextSibling.value == c) {
					break;
				}
				nextSibling = nextSibling.siblingNode;
			}

			if (nextSibling.value == c) { // This node has the right data, no need to create a new one
				return new NodeAdded(nextSibling, false);
			}
			else { 	// Did not find a node with specified value, create one in the end of the chain
				nextSibling.siblingNode = new Node(c);
				return new NodeAdded(nextSibling.siblingNode, true);
			}
		}
	}

	/**
	 * Adds a child node to this parent node.
	 * @param parentNode The parent node where the child node should be added to
	 * @param c The char to be added
	 * @return The node added. If a node containing the char already exists, that node will be returned.
	 */
	private NodeAdded addChild(Node parentNode, char c) {
		if (parentNode.childNode == null) {	// Parent node does not have any children
			parentNode.childNode = new Node(c);
			return new NodeAdded(parentNode.childNode, true);
		}
		else {// Parent node has a child node
			return addSibling(parentNode.childNode, c); // Call addSibling() using the child node
		}
	}
}

/**
 * Represents a node in DLB trie
 */
class Node {
	Node siblingNode;
	Node childNode;
	char value;

	public Node() { }

	public Node(char value) {
		this(value, null, null);
	}

	public Node(char value, Node siblingNode, Node childNode) {
		this.value = value;
		this.siblingNode = siblingNode;
		this.childNode = childNode;
	}
}

/**
 * The result of an add node operation.
 * node is the node the method added, or an existing node that contains the value we are looking for
 * added is true if the method added this node, false if the node already exists and method did not add the node.
 */
class NodeAdded {
	Node node;
	boolean added;

	public NodeAdded(Node node, boolean added) {
		this.node = node;
		this.added = added;
	}
}
