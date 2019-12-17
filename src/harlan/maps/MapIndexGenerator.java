package harlan.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import net.openrs.cache.Cache;
import net.openrs.cache.Container;
import net.openrs.cache.ReferenceTable;

/**
 * Just a quick class I wrote up to generate map_index.dat and the
 * corresponding files.
 * @author `Discardedx2
 */
public class MapIndexGenerator {

	public static class MapIndex {
		public int squareId, mapFile, landscapeFile;
	}

	/**
	 * Represents the loaded squares.
	 */
	private static Map<Integer, Square> squares = new HashMap<Integer, Square>();

	public static Map<Integer, MapIndex> loadMapIndicies(Cache cache) {
		Map<Integer, MapIndex> indicies = new HashMap<Integer, MapIndex>();
		ReferenceTable table = null;
		try {
			table = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 5)).getData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("Generating map_index.dat...\t");
		int total = 0;
		int totalToCheck = 256*256;
		int checked = 0;
		for (int squareX = 0; squareX < 256; squareX++) {// Could do it all in one loop but i want to keep it seperate
			for (int squareY = 0; squareY < 256; squareY++) {//just to keep it neat.
				checked++;
				int squareId = squareX << 8 | squareY;
				int mapFile = cache.findName(table, "m"+squareX+"_"+squareY);
				int landscapeFile = cache.findName(table, "l"+squareX+"_"+squareY);
				if (mapFile == -1 || landscapeFile == -1) {
					continue;
				}
				MapIndex ind = new MapIndex();
				ind.squareId = squareId;
				ind.mapFile = mapFile;
				ind.landscapeFile = landscapeFile;
				indicies.put(squareId, ind);
				total++;
				System.out.println("checked: "+checked +" / "+totalToCheck);

			}
			checked++;

		}
		System.out.println("loaded map index");
		return indicies;

	}

	/**
	 * The main method.
	 * @param args the arguments of this program.
	 */
	public static void dumpMapIndex(File fileLoc, Cache cache) {
		try {
			//			loadCompressed();
			//loadUncompressed(fileLoc);
			//			System.out.println("Loaded "+squares.size()+" XTEA files.");
			ReferenceTable table = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 5)).getData());
			RandomAccessFile raf = new RandomAccessFile(fileLoc.getAbsolutePath()+"/map_index.dat", "rw");
			System.out.print("Generating map_index.dat...\t");
			int total = 0;
			raf.seek(2);
			int totalToCheck = 256*256;
			int checked = 0;
			for (int squareX = 0; squareX < 256; squareX++) {// Could do it all in one loop but i want to keep it seperate
				for (int squareY = 0; squareY < 256; squareY++) {//just to keep it neat.
					int squareId = squareX << 8 | squareY;
					int mapFile = cache.findName(table, "m"+squareX+"_"+squareY);
					int landscapeFile = cache.findName(table, "l"+squareX+"_"+squareY);
					if (mapFile == -1 || landscapeFile == -1) {
						continue;
					}
					raf.writeShort(squareId);
					raf.writeShort(mapFile);
					raf.writeShort(landscapeFile);
					total++;
					checked++;
					System.out.println("checked: "+checked +" / "+totalToCheck);

				}
				checked++;

			}
			int end = (int) raf.getFilePointer();
			raf.seek(0);
			raf.writeShort(total);
			raf.seek(end);
			raf.close();
			cache.close();
			System.out.println("generated map index.dat! Total indicies: "+total);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to load compressed xtea files into memory.
	 */
	public static void loadCompressed() {
		try {
			RandomAccessFile raf = new RandomAccessFile("./compressed.dat", "rw");
			final ByteBuffer in = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.getChannel().size());
			while(in.remaining() > 0) {
				int square = in.getShort() & 0xFFFF;
				int[] keys = new int[4];
				for (int key = 0; key < 4; key++) {
					keys[key]= in.getInt();
				}
				squares.put(square, new Square(square, keys));
			}
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to load uncompressed xtea files into memory.
	 */
	public static void loadUncompressed(File loc) {
		String[] files = new File(loc.getAbsolutePath() + "/xteas").list();
		if (files != null) {
			for (String s : files) {
				s = s.replace(".txt", "");
				String file = loc.getAbsolutePath() + "/xteas"+s;
				int square = Integer.parseInt(s);
				String line;
				int[] keys = new int[4];
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file+".txt"));
					int key = 0;
					while ((line = reader.readLine()) != null) {
						if (!line.equals("")) {
							keys[key++] = Integer.parseInt(line);
						}
					}
					reader.close();
					squares.put(square, new Square(square, keys));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Decodes the specified landscape file (if possible).
	 * @param x The region x of the landscape file name.
	 * @param y The region y of the landscape file name.
	 * @return The decoded (?) landscape file.
	 * @throws Exception
	 */
	private static ByteBuffer decodeLandscape(Cache cache, int file, int squareId) throws Exception {
		ByteBuffer landData = cache.getStore().read(5, file);
		if (landData == null) {
			return null;
		}
		Square def = squares.get(squareId);
		int[] keys = def != null ? def.getKeys() : new int[4];
		byte[] array = landData.array();
		ByteBuffer data = null;//ByteBuffer.wrap(isKeysEncrypted(keys) ? Xtea.decipher(landData, keys) : array);
		try {
			return Container.decode(data).getData();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Checks if the set xtea keys are encrypted or not.
	 * @param keys The keys to check.
	 * @return {@code true} if the keys are encrypted.
	 */
	private static boolean isKeysEncrypted(int[] keys) {
		for (int key : keys) {
			if (key == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Decodes the specified map file.
	 * @param x The region x of the map file name.
	 * @param y The region y of the map file name.
	 * @return The decoded (?) landscape file.
	 * @throws Exception
	 */
	private static ByteBuffer decodeMap(Cache cache, int file) throws Exception {
		return Container.decode(cache.getStore().read(5, file)).getData();
	}

}