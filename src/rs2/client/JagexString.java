package rs2.client;

/*
 * Class16 - Decompiled by JODE Visit http://jode.sourceforge.net/
 */
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class JagexString {

	protected int textSize;
	private final boolean noStringIdentifier = true;
	protected byte[] charArray;
	private int stringIdentifier;

	/*synthetic*/static Class aClass1953;

	public String toRealString() {
		return new String(charArray, 0, textSize);
	}

	final int packTextToBuffer(int offset, int pos, int len, byte[] data) {
		ArrayUtility.copy(charArray, offset, data, pos, -offset + len);
		return -offset + len;
	}

	final JagexString substring(int i) {
		return substring(textSize, i);
	}

	public final boolean contains(JagexString string) {
		if (string == null) {
			return false;
		}
		if (string.textSize != textSize) {
			return false;
		}
		for (int charInd = 0; (charInd ^ 0xffffffff) > (textSize ^ 0xffffffff); charInd++) {
			byte char1 = string.charArray[charInd];
			if (char1 >= 65 && char1 <= 90 || char1 >= -64 && char1 <= -34 && char1 != -41) {
				char1 += 32;
			}
			byte char2 = charArray[charInd];
			if (char2 >= 65 && char2 <= 90 || char2 >= -64 && char2 <= -34 && char2 != -41) {
				char2 += 32;
			}
			if ((char2 ^ 0xffffffff) != (char1 ^ 0xffffffff)) {
				return false;
			}
		}
		return true;
	}

	final int getWidth(FontMetrics fontmetrics, int i) {
		String string;
		try {
			string = new String(charArray, 0, textSize, "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
			string = new String(charArray, 0, textSize);
		}
		return fontmetrics.stringWidth(string);
	}

	final int method142(boolean bool) {
		if (bool != true) {
		}
		return method159((byte) -22, 10);
	}



	final int getIdentifier() {
		int identifier = 0;
		for (int charPtr = 0; (charPtr ^ 0xffffffff) > (textSize ^ 0xffffffff); charPtr++)
			identifier = (charArray[charPtr] & 0xff) + -identifier + (identifier << 5);
		return identifier;
	}

	final byte[] getBytes() {
		byte[] bs = new byte[textSize];
		ArrayUtility.copy(charArray, 0, bs, 0, textSize);
		return bs;
	}

	final JagexString substring(int end, int start) {
		JagexString class16_19_ = new JagexString();
		class16_19_.textSize = end + -start;
		class16_19_.charArray = new byte[-start + end];
		ArrayUtility.copy(charArray, start, class16_19_.charArray, 0, class16_19_.textSize);
		return class16_19_;
	}

	final JagexString correctCapitilization(int i) {
		JagexString class16_20_ = new JagexString();
		class16_20_.textSize = textSize;
		if (i != 1) {
			return null;
		}
		class16_20_.charArray = new byte[textSize];
		int i_21_ = 2;
		for (int i_22_ = 0; (textSize ^ 0xffffffff) < (i_22_ ^ 0xffffffff); i_22_++) {
			byte b = charArray[i_22_];
			if (b >= 97 && b <= 122 || b >= -32 && b <= -2 && b != -9) {
				if (i_21_ == 2) {
					b -= 32;
				}
				i_21_ = 0;
			} else if (b >= 65 && b <= 90 || b >= -64 && b <= -34 && b != -41) {
				if (i_21_ == 0) {
					b += 32;
				}
				i_21_ = 0;
			} else if (b != 46 && b != 33 && b != 63) {
				if (b == 32) {
					if (i_21_ != 2) {
						i_21_ = 1;
					}
				} else {
					i_21_ = 1;
				}
			} else {
				i_21_ = 2;
			}
			class16_20_.charArray[i_22_] = b;
		}
		return class16_20_;
	}

	final boolean method150(JagexString class16_23_, int i) {
		if ((class16_23_.textSize ^ 0xffffffff) < (textSize ^ 0xffffffff)) {
			return false;
		}
		if (i != -22215) {
			equalsString(null);
		}
		for (int i_24_ = 0; class16_23_.textSize > i_24_; i_24_++) {
			byte b = class16_23_.charArray[i_24_];
			byte b_25_ = charArray[i_24_];
			if (b >= 65 && b <= 90 || b >= -64 && b <= -34 && b != -41) {
				b += 32;
			}
			if (b_25_ >= 65 && b_25_ <= 90 || b_25_ >= -64 && b_25_ <= -34 && b_25_ != -41) {
				b_25_ += 32;
			}
			if ((b_25_ ^ 0xffffffff) != (b ^ 0xffffffff)) {
				return false;
			}
		}
		return true;
	}

	final int compare(JagexString toCompare) {
		int largestSize;
		if ((textSize ^ 0xffffffff) >= (toCompare.textSize ^ 0xffffffff)) {
			largestSize = textSize;
		} else {
			largestSize = toCompare.textSize;
		}
		for (int c = 0; (c ^ 0xffffffff) > (largestSize ^ 0xffffffff); c++) {
			if ((charArray[c] & 0xff) < (toCompare.charArray[c] & 0xff)) {
				return -1;
			}
			if ((toCompare.charArray[c] & 0xff) < (charArray[c] & 0xff)) {
				return 1;
			}
		}
		if ((toCompare.textSize ^ 0xffffffff) < (textSize ^ 0xffffffff)) {
			return -1;
		}
		if ((textSize ^ 0xffffffff) < (toCompare.textSize ^ 0xffffffff)) {
			return 1;
		}
		return 0;
	}

	final JagexString toUpperCase() {
		JagexString string = new JagexString();
		string.textSize = textSize;
		string.charArray = new byte[textSize];
		for (int textPtr = 0; (textPtr ^ 0xffffffff) > (textSize ^ 0xffffffff); textPtr++) {
			byte b = charArray[textPtr];
			if (b >= 65 && b <= 90 || b >= -64 && b <= -34 && b != -41) {
				b += 32;
			}
			string.charArray[textPtr] = b;
		}
		return string;
	}

	final URL toURL() throws MalformedURLException {
		URL url = new URL(new String(charArray, 0, textSize));
		return url;
	}

	final JagexString trim() {
		JagexString string = new JagexString();
		string.textSize = textSize;
		boolean bool = true;
		string.charArray = new byte[textSize];
		for (int charPtr = 0; charPtr < textSize; charPtr++) {
			byte b_31_ = charArray[charPtr];
			if (b_31_ == 95) {
				string.charArray[charPtr] = (byte) 32;
				bool = true;
			} else if (b_31_ >= 97 && b_31_ <= 122 && bool) {
				string.charArray[charPtr] = (byte) (-32 + b_31_);
				bool = false;
			} else {
				bool = false;
				string.charArray[charPtr] = b_31_;
			}
		}
		return string;
	}

	final JagexString method155(byte b) {
		JagexString class16_32_ = new JagexString();
		if (b != 59) {
			startsWith(null);
		}
		class16_32_.textSize = textSize;
		class16_32_.charArray = new byte[textSize];
		for (int i = 0; (textSize ^ 0xffffffff) < (i ^ 0xffffffff); i++)
			class16_32_.charArray[i] = (byte) 42;
		return class16_32_;
	}

	final JagexString append(int charId) {
		if (charId <= 0 || charId > 255) {
			throw new IllegalArgumentException("invalid char:" + charId);
		}
		if (!noStringIdentifier) {
			throw new IllegalArgumentException();
		}
		stringIdentifier = 0;
		if (charArray.length == textSize) {
			int i_34_;
			for (i_34_ = 1; (textSize ^ 0xffffffff) <= (i_34_ ^ 0xffffffff); i_34_ += i_34_) {
				/* empty */
			}
			byte[] bs = new byte[i_34_];
			ArrayUtility.copy(charArray, 0, bs, 0, textSize);
			charArray = bs;
		}
		charArray[textSize++] = (byte) charId;
		return this;
	}

	final JagexString append(JagexString toInsert) {
		if (!noStringIdentifier) {
			throw new IllegalArgumentException();
		}
		stringIdentifier = 0;
		if (charArray.length < textSize - -toInsert.textSize) {
			int i;
			for (i = 1; textSize + toInsert.textSize > i; i += i) {
				/* empty */
			}
			byte[] bs = new byte[i];
			ArrayUtility.copy(charArray, 0, bs, 0, textSize);
			charArray = bs;
		}
		ArrayUtility.copy(toInsert.charArray, 0, charArray, textSize, toInsert.textSize);
		textSize += toInsert.textSize;
		return this;
	}

	final void outprintString(int i) {
		String string;
		try {
			string = new String(charArray, 0, textSize, "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
			string = new String(charArray, 0, textSize);
		}
	}

	final int method159(byte b, int i) {
		if (b != -22) {
			method142(false);
		}
		if (i < 1 || i > 36) {
			i = 10;
		}
		boolean bool = false;
		boolean bool_36_ = false;
		int i_37_ = 0;
		for (int i_38_ = 0; i_38_ < textSize; i_38_++) {
			int i_39_ = 0xff & charArray[i_38_];
			if ((i_38_ ^ 0xffffffff) == -1) {
				if (i_39_ == 45) {
					bool = true;
					continue;
				}
				if (i_39_ == 43) {
					continue;
				}
			}
			if (i_39_ < 48 || i_39_ > 57) {
				if (i_39_ >= 65 && i_39_ <= 90) {
					i_39_ -= 55;
				} else if (i_39_ >= 97 && i_39_ <= 122) {
					i_39_ -= 87;
				} else {
					throw new NumberFormatException();
				}
			} else {
				i_39_ -= 48;
			}
			if (i <= i_39_) {
				throw new NumberFormatException();
			}
			if (bool) {
				i_39_ = -i_39_;
			}
			int i_40_ = i_39_ + i_37_ * i;
			if (i_37_ != i_40_ / i) {
				throw new NumberFormatException();
			}
			i_37_ = i_40_;
			bool_36_ = true;
		}
		if (!bool_36_) {
			throw new NumberFormatException();
		}
		return i_37_;
	}



	final int method161(int i, int i_41_, JagexString class16_42_) {
		int[] is = new int[class16_42_.textSize];
		int[] is_43_ = new int[256];
		int[] is_44_ = new int[class16_42_.textSize];
		for (int i_45_ = 0; is_43_.length > i_45_; i_45_++)
			is_43_[i_45_] = class16_42_.textSize;
		for (int i_46_ = 1; (class16_42_.textSize ^ 0xffffffff) <= (i_46_ ^ 0xffffffff); i_46_++) {
			is[i_46_ + -1] = -i_46_ + (class16_42_.textSize << 1);
			is_43_[ class16_42_.charArray[-1 + i_46_] & 255] = class16_42_.textSize + -i_46_;
		}
		int i_47_ = class16_42_.textSize + 1;
		int i_48_ = class16_42_.textSize;
		while ((i_48_ ^ 0xffffffff) < -1) {
			is_44_[-1 + i_48_] = i_47_;
			for (/**/; i_47_ <= class16_42_.textSize
					&& class16_42_.charArray[-1 + i_47_] != class16_42_.charArray[i_48_ - 1]; i_47_ = is_44_[-1 + i_47_]) {
				if ((-i_48_ + class16_42_.textSize ^ 0xffffffff) >= (is[i_47_ + -1] ^ 0xffffffff)) {
					is[-1 + i_47_] = class16_42_.textSize - i_48_;
				}
			}
			i_48_--;
			i_47_--;
		}
		int i_49_ = 1;
		int i_50_ = i_47_;
		i_47_ = -i_50_ + class16_42_.textSize + 1;
		int i_51_ = 0;
		for (int i_52_ = 1; i_52_ <= i_47_; i_52_++) {
			is_44_[i_52_ + -1] = i_51_;
			for (/**/; i_51_ >= 1
					&& (class16_42_.charArray[-1 + i_52_] ^ 0xffffffff) != (class16_42_.charArray[i_51_ - 1] ^ 0xffffffff); i_51_ = is_44_[i_51_
					                                                                                                                       + -1]) {
				/* empty */
			}
			i_51_++;
		}
		if (i > -5) {
			return 78;
		}
		while (i_50_ < class16_42_.textSize) {
			for (int i_53_ = i_49_; (i_53_ ^ 0xffffffff) >= (i_50_ ^ 0xffffffff); i_53_++) {
				if ((-i_53_ + class16_42_.textSize - -i_50_ ^ 0xffffffff) >= (is[-1 + i_53_] ^ 0xffffffff)) {
					is[i_53_ + -1] = -i_53_ + i_50_ + class16_42_.textSize;
				}
			}
			i_49_ = i_50_ - -1;
			i_50_ = i_47_ + i_50_ + -is_44_[i_47_ - 1];
			i_47_ = is_44_[-1 + i_47_];
		}
		int i_54_;
		for (int i_55_ = -1 + class16_42_.textSize + i_41_; i_55_ < textSize; i_55_ += Math.max(
				is_43_[charArray[i_55_] & 0xff], is[i_54_])) {
			for (i_54_ = -1 + class16_42_.textSize; (i_54_ ^ 0xffffffff) <= -1
					&& class16_42_.charArray[i_54_] == charArray[i_55_]; i_54_--)
				i_55_--;
			if (i_54_ == -1) {
				return 1 + i_55_;
			}
		}
		return -1;
	}

	final boolean startsWith(JagexString s, int offset) {
		if (textSize < s.textSize) {
			return false;
		}
		int i_57_ = -s.textSize + textSize;
		for (int charPtr = offset; charPtr < s.textSize; charPtr++) {
			if ((s.charArray[charPtr] ^ 0xffffffff) != (charArray[i_57_ + charPtr] ^ 0xffffffff)) {
				return false;
			}
		}
		return true;
	}

	private final boolean method163(int i, int i_59_) {
		if (i < 1 || i > 36) {
			i = 10;
		}
		int i_60_ = 40 / ((i_59_ - 59) / 46);
		boolean bool = false;
		boolean bool_61_ = false;
		int i_62_ = 0;
		for (int i_63_ = 0; (textSize ^ 0xffffffff) < (i_63_ ^ 0xffffffff); i_63_++) {
			int i_64_ = 0xff & charArray[i_63_];
			if (i_63_ == 0) {
				if (i_64_ == 45) {
					bool_61_ = true;
					continue;
				}
				if (i_64_ == 43) {
					continue;
				}
			}
			if (i_64_ >= 48 && i_64_ <= 57) {
				i_64_ -= 48;
			} else if (i_64_ < 65 || i_64_ > 90) {
				if (i_64_ >= 97 && i_64_ <= 122) {
					i_64_ -= 87;
				} else {
					return false;
				}
			} else {
				i_64_ -= 55;
			}
			if ((i ^ 0xffffffff) >= (i_64_ ^ 0xffffffff)) {
				return false;
			}
			if (bool_61_) {
				i_64_ = -i_64_;
			}
			int i_65_ = i_64_ + i * i_62_;
			if (i_65_ / i != i_62_) {
				return false;
			}
			bool = true;
			i_62_ = i_65_;
		}
		return bool;
	}

	final void method164(int i, int i_66_, Graphics graphics, int i_67_) {
		String string;
		try {
			string = new String(charArray, 0, textSize, "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
			string = new String(charArray, 0, textSize);
		}
		graphics.drawString(string, i_67_, i);

	}

	final JagexString method165(int i, int i_68_) {
		if ((i_68_ ^ 0xffffffff) >= -1 || i_68_ > 255) {
			throw new IllegalArgumentException("invalid char");
		}
		JagexString class16_69_ = new JagexString();
		class16_69_.charArray = new byte[1 + textSize];
		class16_69_.textSize = textSize - -1;
		ArrayUtility.copy(charArray, 0, class16_69_.charArray, 0, textSize);
		int i_70_ = 21 % ((i - -69) / 39);
		class16_69_.charArray[textSize] = (byte) i_68_;
		return class16_69_;
	}

	final int getChar(int i) {
		return 0xff & charArray[i];
	}

	JagexString() {
		/* empty */
	}

	final JagexString[] readJagexStrings(boolean bool, int i) {
		int i_74_ = 0;
		int i_75_ = 0;
		if (bool != false) {
		}
		for (/**/; (textSize ^ 0xffffffff) < (i_75_ ^ 0xffffffff); i_75_++) {
			if (charArray[i_75_] == i) {
				i_74_++;
			}
		}
		JagexString[] class16s = new JagexString[i_74_ + 1];
		if ((i_74_ ^ 0xffffffff) == -1) {
			class16s[0] = this;
			return class16s;
		}
		int i_76_ = 0;
		int i_77_ = 0;
		for (int i_78_ = 0; (i_78_ ^ 0xffffffff) > (i_74_ ^ 0xffffffff); i_78_++) {
			int i_79_;
			for (i_79_ = 0; charArray[i_77_ + i_79_] != i; i_79_++) {
				/* empty */
			}
			class16s[i_76_++] = substring(i_77_ - -i_79_, i_77_);
			i_77_ += i_79_ + 1;
		}
		class16s[i_74_] = substring(textSize, i_77_);
		return class16s;
	}

	final JagexString format() {
		int charPtr = 0;
		int origSize = textSize;
		for (/**/; textSize > charPtr; charPtr++) {
			if ((charArray[charPtr] < 0 || charArray[charPtr] > 32) && (0xff & charArray[charPtr]) != 160) {
				break;
			}
		}
		for (/**/; charPtr < origSize
				&& (charArray[-1 + origSize] >= 0 && charArray[origSize - 1] <= 32 || (0xff & charArray[origSize + -1]) == 160); origSize--) {
			/* empty */
		}
		if (charPtr == 0 && (textSize ^ 0xffffffff) == (origSize ^ 0xffffffff)) {
			return this;
		}
		JagexString newString = new JagexString();
		newString.textSize = origSize + -charPtr;
		newString.charArray = new byte[newString.textSize];
		for (int i_83_ = 0; (i_83_ ^ 0xffffffff) > (newString.textSize ^ 0xffffffff); i_83_++)
			newString.charArray[i_83_] = charArray[charPtr + i_83_];
		return newString;
	}

	final boolean method170(boolean bool) {
		return method163(10, -59);
	}

	final int getSize() {
		return textSize;
	}



	final int method173(JagexString class16_85_) {
		return method161(-108, 0, class16_85_);
	}

	private final long method174(int i) {
		if (i != 17005) {
			charArray = null;
		}
		long l = 0L;
		for (int i_86_ = 0; i_86_ < textSize; i_86_++)
			l = (0xff & charArray[i_86_]) + -l + (l << 5);
		return l;
	}



	final boolean startsWith(JagexString toCompare) {
		if ((toCompare.textSize ^ 0xffffffff) < (textSize ^ 0xffffffff)) {
			return false;
		}
		for (int i = 0; toCompare.textSize > i; i++) {
			if ((charArray[i] ^ 0xffffffff) != (toCompare.charArray[i] ^ 0xffffffff)) {
				return false;
			}
		}
		return true;
	}

	final JagexString method177(int i, int i_88_, int i_89_) {
		byte b = (byte) i_88_;
		byte b_90_ = (byte) i;
		JagexString class16_91_ = new JagexString();
		class16_91_.textSize = textSize;
		class16_91_.charArray = new byte[textSize];
		for (int i_92_ = i_89_; i_92_ < textSize; i_92_++) {
			byte b_93_ = charArray[i_92_];
			if ((b_93_ ^ 0xffffffff) == (b ^ 0xffffffff)) {
				class16_91_.charArray[i_92_] = b_90_;
			} else {
				class16_91_.charArray[i_92_] = b_93_;
			}
		}
		return class16_91_;
	}

	@Override
	public final int hashCode() {
		return getIdentifier();
	}

	final JagexString setCharArray(byte b) {
		if (!noStringIdentifier) {
			throw new IllegalArgumentException();
		}
		stringIdentifier = 0;
		if (textSize != charArray.length) {
			byte[] bs = new byte[textSize];
			ArrayUtility.copy(charArray, 0, bs, 0, textSize);
			charArray = bs;
		}
		return this;
	}

	@Override
	public final boolean equals(Object object) {
		if (object instanceof JagexString) {
			return equalsString((JagexString) object);
		}
		throw new IllegalArgumentException();
	}

	final int method179(byte b, int i, int i_94_) {
		byte b_95_ = (byte) i_94_;
		for (int i_96_ = i; i_96_ < textSize; i_96_++) {
			if (charArray[i_96_] == b_95_) {
				return i_96_;
			}
		}
		int i_97_ = -74 / ((b - 75) / 48);
		return -1;
	}



	final URL createURL(URL url, int i) throws MalformedURLException {
		if (i > -19) {
			return null;
		}
		return new URL(url, new String(charArray, 0, textSize));
	}



	final boolean equalsString(JagexString compareTo) {
		if (compareTo == null) {
			return false;
		}
		if ((textSize ^ 0xffffffff) != (compareTo.textSize ^ 0xffffffff)) {
			return false;
		}
		if (!noStringIdentifier || !compareTo.noStringIdentifier) {
			if ((stringIdentifier ^ 0xffffffff) == -1) {
				stringIdentifier = getIdentifier();
				if (stringIdentifier == 0) {
					stringIdentifier = 1;
				}
			}
			if ((compareTo.stringIdentifier ^ 0xffffffff) == -1) {
				compareTo.stringIdentifier = compareTo.getIdentifier();
				if ((compareTo.stringIdentifier ^ 0xffffffff) == -1) {
					compareTo.stringIdentifier = 1;
				}
			}
			if ((compareTo.stringIdentifier ^ 0xffffffff) != (stringIdentifier ^ 0xffffffff)) {
				return false;
			}
		}
		for (int i = 0; i < textSize; i++) {
			if ((compareTo.charArray[i] ^ 0xffffffff) != (charArray[i] ^ 0xffffffff)) {
				return false;
			}
		}
		return true;
	}

	final long longForName() {
		long l = 0L;
		for (int i_100_ = 0; i_100_ < textSize; i_100_++) {
			if (i_100_ >= 12) {
				break;
			}
			l *= 37L;
			int i_101_ = charArray[i_100_];
			if (i_101_ >= 65 && i_101_ <= 90) {
				l += -65 + i_101_ + 1;
			} else if (i_101_ >= 97 && i_101_ <= 122) {
				l += -97 + 1 + i_101_;
			} else if (i_101_ >= 48 && i_101_ <= 57) {
				l += -21 - -i_101_;
			}
		}
		for (/**/; l % 37L == 0L && (l ^ 0xffffffffffffffffL) != -1L; l /= 37L) {
			/* empty */
		}
		return l;
	}

	@Override
	public final String toString() {
		return toRealString();
	}



	static final JagexString intToString(boolean bool, int radix, int forString) {
		if (radix < 2 || radix > 36) {
			throw new IllegalArgumentException("Invalid radix:" + radix);
		}
		int i_11_ = forString / radix;
		int i_12_ = 1;
		for (/**/; i_11_ != 0; i_11_ /= radix)
			i_12_++;
		int i_13_ = i_12_;
		if (forString < 0 || bool) {
			i_13_++;
		}
		byte[] bs = new byte[i_13_];
		if ((forString ^ 0xffffffff) <= -1) {
			if (bool) {
				bs[0] = (byte) 43;
			}
		} else {
			bs[0] = (byte) 45;
		}
		for (int i_14_ = 0; (i_14_ ^ 0xffffffff) > (i_12_ ^ 0xffffffff); i_14_++) {
			int i_15_ = forString % radix;
			if (i_15_ < 0) {
				i_15_ = -i_15_;
			}
			if (i_15_ > 9) {
				i_15_ += 39;
			}
			forString /= radix;
			bs[-1 + i_13_ + -i_14_] = (byte) (48 + i_15_);
		}
		JagexString jagexString = new JagexString();
		jagexString.textSize = i_13_;
		jagexString.charArray = bs;
		return jagexString;
	}

	static final JagexString parseInteger(int i) {
		return JagexString.intToString(false, 10, i);
	}




	/*synthetic*/
	static Class getClassForName(String string) {
		Class var_class;
		try {
			var_class = Class.forName(string);
		} catch (ClassNotFoundException classnotfoundexception) {
			throw (NoClassDefFoundError) new NoClassDefFoundError().initCause(classnotfoundexception);
		}
		return var_class;
	}

	public static JagexString empty() {
		return createString("");
	}

	public static final JagexString createString(String string) {
		byte[] bytes = string.getBytes();
		int length = bytes.length;
		int charIdx = 0;
		JagexString jagexString = new JagexString();
		jagexString.charArray = new byte[length];
		while (charIdx < length) {
			int charPtr = bytes[charIdx++] & 0xff;
			if (charPtr <= 45 && charPtr >= 40) {
				if (length <= charIdx) {
					break;
				}
				int i_3_ = bytes[charIdx++] & 0xff;
				jagexString.charArray[jagexString.textSize++] = (byte) (-1720 + charPtr * 43 + i_3_ + -48);
			} else if (charPtr != 0) {
				jagexString.charArray[jagexString.textSize++] = (byte) charPtr;
			}
		}
		jagexString.setCharArray((byte) -11);
		return jagexString;
	}

	public void writeToBuffer(ByteBuffer buf) {
		for (byte c : charArray) {
			buf.put(c);
		}
		buf.put((byte)10);
	}
}
