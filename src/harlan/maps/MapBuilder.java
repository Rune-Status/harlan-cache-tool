package harlan.maps;

import harlan.CacheViewer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

import net.openrs.cache.util.CompressionUtils;
import net.openrs.util.FileChannelUtils;

public class MapBuilder {




	/**
	 * @wbp.parser.entryPoint
	 */
	public static void build(CacheViewer frame) {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
			try {
				File f = chooser.getSelectedFile();
				System.out.println("attempting to build map: "+f.toString());
				FileInputStream in = new FileInputStream(f);
				FileChannel channel = in.getChannel();
				ByteBuffer buf = ByteBuffer.allocate((int)channel.size());
				FileChannelUtils.readFully(channel, buf, 0);
				in.close();
				buf = ByteBuffer.wrap(CompressionUtils.gunzip(buf.array()));
				for (int z = 0; z < 4; z++) {
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++)
							readTileInfo(x , y , z, buf);
					}
				}
				parsed = true;
			} catch(Exception e) {
				parsed = false;
				e.printStackTrace();
			}



	}
	public static boolean parsed = false;
	public static final byte[][][] tileSettings = new byte[4][104][104];
	public static final int[][][] heightMap = new int[4][104][104];
	public static final byte[][][] tileShapeRotation = new byte[4][104][104];
	public static final byte[][][] tileShape = new byte[4][104][104];
	public static final byte[][][] overlay  = new byte[4][104][104];
	public static final byte[][][] underlay = new byte[4][104][104];


	public static void readTileInfo(int x, int y, int z, ByteBuffer mapBuffer) {
		if (x >= 0 && x < 104 && y >= 0 && y < 104) {
			tileSettings[z][x][y] = 0;
			do {
				int shift = mapBuffer.get();
				if (shift == 0)
					if (z == 0) {
						heightMap[0][x][y] = heightMap[0][x][y] - 240;
						return;
					} else {
						heightMap[z][x][y] = heightMap[z - 1][x][y] - 240;
						return;
					}
				if (shift == 1) {
					int pos = mapBuffer.get();
					if (pos == 1)
						pos = 0;
					if (z == 0) {
						heightMap[0][x][y] = -pos * 8;
						return;
					} else {
						heightMap[z][x][y] = heightMap[z - 1][x][y] - pos * 8;
						return;
					}
				}
				if (shift <= 49) {
					overlay[z][x][y] = mapBuffer.get();
					tileShape[z][x][y] = (byte) ((shift - 2) / 4);
					tileShapeRotation[z][x][y] = (byte) (shift - 2 + 0 & 3);
				} else if (shift <= 81)
					tileSettings[z][x][y] = (byte) (shift - 49);
				else
					underlay[z][x][y] = (byte) (shift - 81);
			} while (true);
		}
		do {
			int shift = mapBuffer.get();
			if (shift == 0)
				break;
			if (shift == 1) {
				mapBuffer.get();
				return;
			}
			if (shift <= 49)
				mapBuffer.get();
		} while (true);
	}

	/*public void drawMinimapTile(int pixels[], int index, int z, int x, int y)
	{
		int j = 512;//was parameter
		Tile class30_sub3 = tileArray[z][x][y];
		if(class30_sub3 == null)
			return;
		PlainTile class43 = class30_sub3.myPlainTile;
		if(class43 != null)
		{
			int colour = class43.minimapColour;
			if(colour == 0)
				return;
			for(int k1 = 0; k1 < 4; k1++)
			{
				pixels[index] = colour;
				pixels[index + 1] = colour;
				pixels[index + 2] = colour;
				pixels[index + 3] = colour;
				index += j;
			}

			return;
		}
		ShapedTile shapedTile = class30_sub3.myShapedTile;
		if(shapedTile == null)
			return;
		int l1 = shapedTile.tileShapePoint;
		int i2 = shapedTile.tileShapeIndice;
		int colour1 = shapedTile.nonOccludMinimapColour;
		int colour2 = shapedTile.occludMinimapColour;
		int ai1[] = tileShapePoints[l1];
		int ai2[] = tileShapeIndices[i2];
		int l2 = 0;
		if(colour1 != 0)
		{
			for(int i3 = 0; i3 < 4; i3++)
			{
				pixels[index] = ai1[ai2[l2++]] != 0 ? colour2 : colour1;
				pixels[index + 1] = ai1[ai2[l2++]] != 0 ? colour2 : colour1;
				pixels[index + 2] = ai1[ai2[l2++]] != 0 ? colour2 : colour1;
				pixels[index + 3] = ai1[ai2[l2++]] != 0 ? colour2 : colour1;
				index += j;
			}

			return;
		}
		for(int j3 = 0; j3 < 4; j3++)
		{
			if(ai1[ai2[l2++]] != 0)
				pixels[index] = colour2;
			if(ai1[ai2[l2++]] != 0)
				pixels[index + 1] = colour2;
			if(ai1[ai2[l2++]] != 0)
				pixels[index + 2] = colour2;
			if(ai1[ai2[l2++]] != 0)
				pixels[index + 3] = colour2;
			index += j;
		}

	}

	public void renderMapScene(int z)
	{
		int pixels[] = minimap.myPixels;
		int pixelAmount = pixels.length;
		for(int k = 0; k < pixelAmount; k++)
			pixels[k] = 0;
		int sizeXY= 103;
		for(int y = 1; y < sizeXY; y++)
		{
			int pixelPointer = 24628 + (sizeXY - y) * 512 * 4;
			for(int x = 1; x < sizeXY; x++)
			{
				if((tileSettingBits[z][x][y] & 0x18) == 0)
					worldController.drawMinimapTile(pixels, pixelPointer, z, x, y);
				if(z < 3 && (tileSettingBits[z + 1][x][y] & 8) != 0)
					worldController.drawMinimapTile(pixels, pixelPointer, z + 1, x, y);
				pixelPointer += 4;
			}

		}
		minimap.initDrawingArea();
		for(int y = 1; y < sizeXY; y++)
		{
			for(int x = 1; x < sizeXY; x++)
			{
				if((tileSettingBits[z][x][y] & 0x18) == 0)
					drawMapScenes(y, x, z);
				if(z < 3 && (tileSettingBits[z + 1][x][y] & 8) != 0)
					drawMapScenes(y, x, z + 1);
			}

		}

		gameScreenCanvas.initDrawingArea();
		numOfMapMarkers = 0;
		for(int x = 0; x < sizeXY+1; x++)
		{
			for(int y = 0; y < sizeXY+1; y++)
			{
				int objectID = worldController.getGroundDecorationID(plane, x, y);
				if(objectID != 0) {
					int objectMapFunctionID = ObjectDef.forID(objectID).mapFunctionID;
					if(objectMapFunctionID >= 0) {
						int posX = x;
						int posY = y;
						markGraphic[numOfMapMarkers] = mapFunctions[objectMapFunctionID];
						markPosX[numOfMapMarkers] = posX;
						markPosY[numOfMapMarkers] = posY;
						numOfMapMarkers++;
					}
				}
			}
		}
	}*/

}
