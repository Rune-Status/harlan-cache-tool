package harlan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.openrs.util.FileChannelUtils;

public class FileUtility {

	public static ByteBuffer readFully(File f) {
		ByteBuffer buf = null;
		try {
			FileInputStream in = new FileInputStream(f);
			FileChannel channel = in.getChannel();
			buf = ByteBuffer.allocate((int)channel.size());
			FileChannelUtils.readFully(channel, buf, 0);
			in.close();
			buf.rewind();
		} catch (IOException io) {
			io.printStackTrace();
		}
		return buf;
	}

	public static void writeFully(File f, ByteBuffer buf) {
		try {
			FileOutputStream out = new FileOutputStream(f);
			FileChannel channel = out.getChannel();
			FileChannelUtils.writeFully(channel, buf);
			out.close();
			channel.close();
		} catch(IOException io) {
			io.printStackTrace();
		}

	}

}
