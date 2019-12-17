package rs2.client;

import java.math.BigInteger;



public class ByteBuffer2 {

	public byte[] buffer;
	public int position;
	public final JagexString readString2() {
		int from = position;
		while (( buffer[position++] & 0xFF) != 10) {
		}
		return Misc.create(buffer, 0, position - (from + 1), from);
	}

	public final JagexString readString() {
		int from = position;
		while (buffer[position++] != 0) {
		}
		return Misc.create(buffer, 0, position - (from + 1), from);
	}

	public final void putLEShortA(int i) {
		buffer[position++] = (byte) (i + 128);
		buffer[position++] = (byte) (i >> 8);
	}

	public final void putByteS(int i) {
		buffer[position++] = (byte) (128 + -i);
	}

	public final void putInt(int i) {
		buffer[position++] = (byte) (i >> 24);
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
	}

	public final void putLEShort(int i) {
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 8);
	}


	public final int getUnsignedSmart() {
		int i = 0xff & buffer[position];
		if (i >= 128) {
			return -32768 + getUnsignedShort();
		}
		return getUnsignedByte();
	}

	final byte getNegativeByte() {
		return (byte) -buffer[position++];
	}

	final byte getByteS() {
		return (byte) (-128 + buffer[position++]);
	}

	public final int getUnsignedByteS() {
		return -128 + buffer[position++] & 0xff;
	}

	public final void putLong(long l) {
		buffer[position++] = (byte) (int) (l >> 56);
		buffer[position++] = (byte) (int) (l >> 48);
		buffer[position++] = (byte) (int) (l >> 40);
		buffer[position++] = (byte) (int) (l >> 32);
		buffer[position++] = (byte) (int) (l >> 24);
		buffer[position++] = (byte) (int) (l >> 16);
		buffer[position++] = (byte) (int) (l >> 8);
		buffer[position++] = (byte) (int) l;
	}

	public final int getSomeShiftedInt() {
		int i = buffer[position++];
		int i_7_ = 0;
		for (/**/; (i ^ 0xffffffff) > -1; i = buffer[position++])
			i_7_ = (0x7f & i | i_7_) << 7;
		return i_7_ | i;
	}

	public final void putBytes(int offset, byte[] data, int length) {
		for (int i = offset; i < offset + length; i++) {
			buffer[position++] = data[i];
		}
	}

	public final void putLEInt(int i) {
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 24);
	}

	final byte getByteA() {
		return (byte) (-buffer[position++] + 128);
	}

	public final int getLEInt() {
		position += 4;
		return (buffer[-4 + position] & 0xff) + ((buffer[-2 + position] & 0xff) << 16) + ((0xff & buffer[position + -1]) << 24) - -(0xff00 & buffer[position + -3] << 8);
	}

	public final int getUnsignedLEShortA() {
		position += 2;
		return (0xff & buffer[-2 + position] - 128) + (0xff00 & buffer[position + -1] << 8);
	}

	public final void putShort(int i) {
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
	}

	public final void writeIntV1(int i) {
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 24);
		buffer[position++] = (byte) (i >> 16);
	}

	public final void putJagexString(JagexString jagexString) {
		position += jagexString.packTextToBuffer(0, position, jagexString.getSize(), buffer);
		buffer[position++] = (byte) 0;
	}

	public final int readSmart() {
		int value = 0;
		int signed = getUnsignedSmart();
		while ((signed ^ 0xffffffff) == -32768) {
			signed = getUnsignedSmart();
			value += 32767;
		}
		value += signed;
		return value;
	}

	public final void method448(int i, int i_13_, long l) {
		if (i <= 2) {
			getMediumInt();
		}
		if ((--i_13_ ^ 0xffffffff) > -1 || i_13_ > 7) {
			throw new IllegalArgumentException();
		}
		for (int i_14_ = i_13_ * 8; (i_14_ ^ 0xffffffff) <= -1; i_14_ -= 8)
			buffer[position++] = (byte) (int) (l >> i_14_);
	}

	final long readLong(int bit_size, int base_long) {
		if ((--bit_size ^ 0xffffffff) > -1 || bit_size > 7) {
			throw new IllegalArgumentException();
		}
		int byte_size = bit_size * 8;
		long res = base_long;
		for (/**/; byte_size >= 0; byte_size -= 8)
			res |= (buffer[position++] & 0xffL) << byte_size;
		return res;
	}

	public final void readBytes(int end, byte[] data, int start) {
		int index = start;
		for (/**/; (index ^ 0xffffffff) > (end + start ^ 0xffffffff); index++)
			data[index] = buffer[position++];
	}

	final JagexString getCheckedString(byte b) {
		if ((buffer[position] ^ 0xffffffff) == -1) {
			position++;
			return null;
		}
		return readString();
	}

	public final byte readSignedByte() {
		return buffer[position++];
	}

	public final int getShortLe() {
		position += 2;
		return (buffer[-2 + position] & 0xff) + (buffer[position + -1] << 8 & 0xff00);
	}

	public final void putLongA(long l) {
		putIntA((int) (l >> 32));
		putIntA((int) l);
	}


	public final int getLEShort() {
		position += 2;
		int value = ((0xff & buffer[position - 1]) << 8) + (0xff & buffer[-2 + position] - 128);
		if (value > 32767) {
			value -= 65536;
		}
		return value;
	}

	public final void putMediumInt(int v) {
		buffer[position++] = (byte) (v >> 16);
		buffer[position++] = (byte) (v >> 8);
		buffer[position++] = (byte) v;
	}

	public final int getUnsignedByteA() {
		return 0xff & -buffer[position++] + 128;
	}

	public final int getSmart() {
		int i = buffer[position] & 0xff;
		if (i < 128) {
			return getUnsignedByte() - 64;
		}
		return getUnsignedShort() - 49152;
	}

	public final void putByte(int i) {
		buffer[position++] = (byte) i;
	}

	public final int getUnsignedByte() {
		return buffer[position++] & 0xff;
	}

	public final void encodeRSA(BigInteger exponent, BigInteger modulus) {
		int initPos = position;
		position = 0;
		byte[] bs = new byte[initPos];
		readBytes(initPos, bs, 0);
		BigInteger biginteger_25_ = new BigInteger(bs);
		BigInteger rsa = biginteger_25_.modPow(exponent, modulus);
		byte[] rsaBuffer = rsa.toByteArray();
		position = 0;
		putByte(rsaBuffer.length);
		putBytes(0, rsaBuffer, rsaBuffer.length);
	}

	public final void putShortA(int v) {
		buffer[position++] = (byte) (v >> 8);
		buffer[position++] = (byte) (128 + v);
	}


	public byte[] readBytes() {
		int i = position;
		while (buffer[position++] != 10)
			;
		byte abyte0[] = new byte[position - i - 1];
		System.arraycopy(buffer, i, abyte0, i - i, position - 1 - i);
		return abyte0;
	}

	public final void getBytesA(int off, byte[] data, int i_31_) {
		for (int i = off; i < i_31_ + off; i++)
			data[i] = (byte) (buffer[position++] - 128);
	}

	public final int readIntV2() {
		position += 4;
		return (0xff & buffer[-2 + position]) + (0xff0000 & buffer[-4 + position] << 16) + ((0xff & buffer[-3 + position]) << 24) + (0xff00 & buffer[-1 + position] << 8);
	}

	public final void putSizeInt(int i) {
		buffer[-4 + -i + position] = (byte) (i >> 24);
		buffer[position + -i + -3] = (byte) (i >> 16);
		buffer[position - i + -2] = (byte) (i >> 8);
		buffer[-1 + -i + position] = (byte) i;
	}

	public final void putSmart(int i) {
		if (i >= 0 && i < 128) {
			putByte(i);
		} else if (i >= 0 && (i ^ 0xffffffff) > -32769) {
			putShort(i + 32768);
		} else {
			throw new IllegalArgumentException();
		}
	}

	final long getLong() {
		long l = 0xffffffffL & getInt();
		long i = getInt() & 0xffffffffL;
		return i + (l << 32);
	}

	public final void decodeXTEA(int start, int end, byte b, int[] keys) {
		int origPos = position;
		int segments = (end - start) / 8;
		position = start;
		for (int i_39_ = 0; (i_39_ ^ 0xffffffff) > (segments ^ 0xffffffff); i_39_++) {
			int i_40_ = -957401312;
			int i_41_ = -1640531527;
			int sum = getInt();
			int delta = getInt();
			int num_rounds = 32;
			while ((num_rounds-- ^ 0xffffffff) < -1) {
				delta -= keys[(i_40_ & 0x19cf) >>> 11] + i_40_ ^ (sum << 4 ^ sum >>> 5) - -sum;
				i_40_ -= i_41_;
				sum -= keys[0x3 & i_40_] + i_40_ ^ delta + (delta << 4 ^ delta >>> 5);
			}
			position -= 8;
			putInt(sum);
			putInt(delta);
		}
		position = origPos;
	}


	public final int getMediumInt() {
		position += 3;
		return (buffer[-2 + position] << 8 & 0xff00) + ((0xff & buffer[-3 + position]) << 16) - -(0xff & buffer[-1 + position]);
	}

	public final int getInt() {
		position += 4;
		return (~0xffffff & buffer[-4 + position] << 24) + (buffer[-3 + position] << 16 & 0xff0000) - -((buffer[position - 2] & 0xff) << 8) - -(0xff & buffer[-1 + position]);
	}

	public final int getUnsignedNegativeByte() {
		return 0xff & -buffer[position++];
	}

	public final int getShort() {
		position += 2;
		int val = ((buffer[-2 + position] & 0xff) << 8) + (buffer[position + -1] & 0xff);
		if ((val ^ 0xffffffff) < -32768) {
			val -= 65536;
		}
		return val;
	}

	public final int read3Bytes() {
		position += 3;
		return (buffer[position - 2] & 0xff) + ((0xff & buffer[position - 3]) << 16) + (0xff00 & buffer[-1 + position] << 8);
	}

	public final int getUnsignedShort() {
		position += 2;
		return (0xff & buffer[position - 1]) + (buffer[-2 + position] << 8 & 0xff00);
	}

	public final void putSizeByte(int i) {
		buffer[-i + position + -1] = (byte) i;
	}

	public final void getBytesReverseA(int off, int len, byte[] data) {
		for (int i_48_ = off + len - 1; len <= i_48_; i_48_--)  {
			data[i_48_] = (byte) (-128 + buffer[position++]);
		}
	}

	public final void method479(int i, int i_49_) {
		if ((i & ~0x7f ^ 0xffffffff) != -1) {
			if ((i & ~0x3fff ^ 0xffffffff) != -1) {
				if ((~0x1fffff & i ^ 0xffffffff) != -1) {
					if ((i & ~0xfffffff) != 0) {
						putByte(i >>> 28 | 0x80);
					}
					putByte(0x80 | i >>> 21);
				}
				putByte((0x201f5b | i) >>> 14);
			}
			putByte(0x80 | i >>> 7);
		}
		if (i_49_ != 128) {
			readIntV1();
		}
		putByte(i & 0x7f);
	}

	public final int getUnsignedShortA() {
		position += 2;
		return (buffer[position - 2] << 8 & 0xff00) + (-128 + buffer[-1 + position] & 0xff);
	}

	public final void putIntA(int i) {
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 24);
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 8);
	}

	public final void putByteA(int i) {
		buffer[position++] = (byte) (128 + i);
	}

	ByteBuffer2(int i) {
		buffer =  new byte[i];
		position = 0;
	}

	public ByteBuffer2(byte[] bs) {
		position = 0;
		buffer = bs;
	}

	public boolean canRead() {
		return position < buffer.length;
	}

	public final int readIntV1() {
		position += 4;
		return (buffer[-3 + position] & 0xff) + ((buffer[position + -4] & 0xff) << 8) + ((buffer[position + -2] & 0xff) << 24) + (buffer[position - 1] << 16 & 0xff0000);
	}
	public final int readIntV3() {
		position += 4;
		return (buffer[-2 + position] << -1902911192 & 0xff) + ((buffer[position + -3] & 0xff) << 1798325296) +
				((buffer[position + -4] & 0xff) << 1056208216) + buffer[position - 1] & 0xff;
	}
	static {

	}

	public int method437() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void printRemaining() {
		while(position < buffer.length){
			int byt = getUnsignedByte();
			System.out.println("val: " + byt + " pos: " +position);
		}
	}


}

