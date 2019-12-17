package rs2.client;


/* NodeCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class HashTable
{
	public Node node_1;
	public final Node[] cache;
	public long id;
	public final int size;
	public Node nextNode;
	public int cachedNodes = 0;

	public final Node takeNext() {
		cachedNodes = 0;
		if ((cachedNodes ^ 0xffffffff) < -1 && cache[-1 + cachedNodes] != nextNode) {
			Node node = nextNode;
			nextNode = node.prev;
			return node;
		}
		while ((size ^ 0xffffffff) < (cachedNodes ^ 0xffffffff)) {
			Node node = cache[cachedNodes++].prev;
			if (node != cache[cachedNodes + -1]) {
				nextNode = node.prev;
				return node;
			}
		}
		return null;
	}


	public final void set(Node node, long l) {
		if (node.next != null) {
			node.unlink();
		}
		Node n = cache[(int) (-1 + size & l)];
		node.fileRequestID = l;
		node.next = n.next;
		node.prev = n;
		node.next.prev = node;
		node.prev.next = node;
	}

	public final Node a(int i) {
		if (node_1 == null) {
			return null;
		}
		Node node = cache[(int) (size + -1 & id)];
		for (/**/; node_1 != node; node_1 = node_1.prev) {
			if (id == node_1.fileRequestID) {
				Node class23_15_ = node_1;
				node_1 = node_1.prev;
				return class23_15_;
			}
		}
		node_1 = null;
		return null;
	}

	public final Node findNodeByID(long l) {
		id = l;
		Node node = cache[(int) (l & -1 + size)];
		node_1 = node.prev;
		for (; node_1 != node; node_1 = node_1.prev) {
			if (l == node_1.fileRequestID) {
				Node node1 = node_1;
				node_1 = node_1.prev;
				return node1;
			}
		}
		node_1 = null;
		return null;
	}

	public HashTable(int i) {
		cache = new Node[i];
		size = i;
		for (int k = 0; k < i; k++) {
			Node node = cache[k] = new Node();
			node.prev = node;
			node.next = node;
		}
	}
}
