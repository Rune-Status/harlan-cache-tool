package net.openrs.cache;

public class ArchiveIdentifier {
	private final int[] ids;

	final public int getFileID(int id) {
		int len = -1 + (ids.length >> 1);
		int uid = len & id;
		for (;;) {
			int res = ids[uid + uid + 1];
			if ((res ^ 0xffffffff) == 0)
				return -1;
			if (id == ids[uid + uid])
				return res;
			uid = 1 + uid & len;
		}
	}

	ArchiveIdentifier(int[] string) {
		int length;
		for (length = 1; length <= string.length + (string.length >> 1); length <<= 1);
		ids = new int[length - -length];
		for (int textPtr = 0; textPtr < length + length; textPtr++)
			ids[textPtr] = -1;
		for (int stringIndex = 0; (string.length ^ 0xffffffff) < (stringIndex ^ 0xffffffff); stringIndex++) {
			int textArrayPtr;
			for (textArrayPtr = length + -1 & string[stringIndex]; ids[1 + textArrayPtr + textArrayPtr] != -1; textArrayPtr = length - 1 & textArrayPtr - -1);

			ids[textArrayPtr + textArrayPtr] = string[stringIndex];
			ids[textArrayPtr - -textArrayPtr - -1] = stringIndex;
		}
	}
}
