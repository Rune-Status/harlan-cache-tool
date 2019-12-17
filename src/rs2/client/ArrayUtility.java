package rs2.client;


/* Class3 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class ArrayUtility
{
	static final void setEntriesTo(int[] array, int pos, int length, int setTo) {
		length = pos + length - 7;
		while (pos < length) {
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
			array[pos++] = setTo;
		}
		length += 7;
		while (pos < length)
			array[pos++] = setTo;
	}

	static final void clearEntries(int[] array, int pos, int length) {
		length = pos + length - 7;
		while (pos < length) {
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
			array[pos++] = 0;
		}
		length += 7;
		while (pos < length)
			array[pos++] = 0;
	}

	static final void copy2(int[] src, int i, int[] bin, int i_4_, int i_5_) {
		if (src == bin) {
			if (i == i_4_) {
				return;
			}
			if (i_4_ > i && i_4_ < i + i_5_) {
				i_5_--;
				i += i_5_;
				i_4_ += i_5_;
				i_5_ = i - i_5_;
				i_5_ += 7;
				while (i >= i_5_) {
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
					bin[i_4_--] = src[i--];
				}
				i_5_ -= 7;
				while (i >= i_5_)
					bin[i_4_--] = src[i--];
				return;
			}
		}
		i_5_ += i;
		i_5_ -= 7;
		while (i < i_5_) {
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
			bin[i_4_++] = src[i++];
		}
		i_5_ += 7;
		while (i < i_5_)
			bin[i_4_++] = src[i++];
	}

	static final void copy(byte[] origData, int origOffset, byte[] copyTo, int copyToOffset, int length) {
		if (origData == copyTo) {
			if (origOffset == copyToOffset) {
				return;
			}
			if (copyToOffset > origOffset && copyToOffset < origOffset + length) {
				length--;
				origOffset += length;
				copyToOffset += length;
				length = origOffset - length;
				length += 7;
				while (origOffset >= length) {
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
					copyTo[copyToOffset--] = origData[origOffset--];
				}
				length -= 7;
				while (origOffset >= length)
					copyTo[copyToOffset--] = origData[origOffset--];
				return;
			}
		}
		length += origOffset;
		length -= 7;
		while (origOffset < length) {
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
			copyTo[copyToOffset++] = origData[origOffset++];
		}
		length += 7;
		while (origOffset < length)
			copyTo[copyToOffset++] = origData[origOffset++];
	}

	static final byte[] createNew(byte[] bs) {
		int length = bs.length;
		byte[] data = new byte[length];
		copy(bs, 0, data, 0, length);
		return data;
	}
}
