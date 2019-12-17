package rs2.client;


public class Misc {

	public static final JagexString create(byte[] data, int size, int charLength, int charOffset) {
		JagexString jagexString = new JagexString();
		jagexString.charArray = new byte[charLength];
		jagexString.textSize = size;
		for (int i_25_ = charOffset; (i_25_ ^ 0xffffffff) > (charLength + charOffset ^ 0xffffffff); i_25_++) {
			if (data[i_25_] != 0) {
				jagexString.charArray[jagexString.textSize++] = data[i_25_];
			}
		}
		return jagexString;
	}

	public static String objectArrayToString(Object[] array) {
		StringBuilder builder = new StringBuilder();
		for (Object  o :array) {
			builder.append(o.toString()+", ");
		}
		return builder.substring(0, builder.length()-2);
	}

	static final int encodeForHashTable(int val) {
		val = --val | val >>> 1;
		val |= val >>> 2;
		val |= val >>> 4;
		val |= val >>> 8;
		val |= val >>> 16;
		return val + 1;
	}
}
