package harlan.maps;

/**
 * Represents one square on the RuneScape map.
 * @author `Discardedx2
 */
public final class Square {

	/**
	 * This square's id.
	 */
	private final int id;
	/**
	 * This sqaure's set of xtea keys.
	 */
	private final int[] keys;

	/**
	 * Constructs a new Square.
	 * @param squareId This square's id.
	 * @param keys This square's keys.
	 */
	public Square(int squareId, int[] keys) {
		id = squareId;
		this.keys = keys;
	}

	/**
	 * Gets this square's id.
	 * @return The square's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets this square's xtea-encrypted keys.
	 * @return The keys.
	 */
	public int[] getKeys() {
		return keys;
	}

}