package harlan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.openrs.cache.util.CompressionUtils;
import net.openrs.util.FileChannelUtils;

public class GZiper extends JFrame {

	private static final long serialVersionUID = -2712649424108196168L;

	public static void main(String args[]) {
		new GZiper();
	}

	GZiper() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			try {
				for (File file : chooser.getSelectedFiles()) {
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					int magicId = raf.read();
					int magicId2 = raf.read();
					if (magicId != 31 || magicId2 != -117) {
						FileInputStream in = new FileInputStream(file);
						FileChannel channel = in.getChannel();
						ByteBuffer uncompressed = ByteBuffer.allocate((int)channel.size());
						FileChannelUtils.readFully(channel, uncompressed, 0);
						System.out.println("read file: "+file.getName()+" size: "+uncompressed.capacity());
						in.close();
						ByteBuffer compressed = ByteBuffer.wrap(CompressionUtils.gzip(uncompressed.array()));
						FileOutputStream out = new FileOutputStream(file);
						channel = out.getChannel();
						FileChannelUtils.writeFully(channel, compressed);
						out.close();
					}
					raf.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		System.out.println("finished gziping!");
	}

}
