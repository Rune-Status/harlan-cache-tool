package rs2.client;


class Node
{


	final boolean hasNext() {
		return next != null;
	}

	final void unlink() {
		if (next != null) {
			next.prev = prev;
			prev.next = next;
			next = null;
			prev = null;
		}
	}

	protected Node prev;
	protected long fileRequestID;
	protected Node next;
}
