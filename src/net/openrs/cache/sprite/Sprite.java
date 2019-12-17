package net.openrs.cache.sprite;

import harlan.util.FileSystems;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.openrs.cache.Cache;
import net.openrs.cache.Container;
import net.openrs.util.ByteBufferUtils;

/**
 * The sprite works by having a buffer of each colour used, when it reads the colour info for each
 * pixel it will use the index from the colour buffer already generated.
 *
 * Using this techneque allows colours up to 65k being used in a 256 environment
 */
public final class Sprite {

	public static HashMap<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
	public Sprite[] childrenSprites;
	public Container container;
	private int maxWidth;
	private int maxHeight;
	public ArrayList<BufferedImage> images;
	protected int width;
	public int[] pallet;
	protected byte[] pixels;
	public int yStart;
	public int offsetX;
	protected int height;
	protected byte[] transparencyBuffer;
	public int offsetY;
	public int readType = 1;
	public int xStart;
	public int id = -1;
	public boolean hasTransperency() {
		return transparencyBuffer != null;
	}
	public int getMaxWidth() {
		return maxWidth;
	}
	public int getMaxHeight() {
		return maxHeight;
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public int getId() {
		return id;
	}
	public Sprite(Container data, int spriteId) {
		id = spriteId;
		container = data;
		pallet = new int[] { 0 };
		try {
			readDataBlock(data.getData(), id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Sprite() {
		images = new ArrayList<BufferedImage>();
		pallet = new int[] { 0 };
	}

	public int[] convertPixelData() {
		int[] rgbArray = new int[width * height];
		int position = 0;
		int index = 0;
		if (transparencyBuffer != null)
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++) {
					rgbArray[index++] = transparencyBuffer[position] << 24 | pallet[pixels[position] & 0xff];
					position++;
				}
		else
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++) {
					int rgb = pallet[pixels[position++] & 0xff];
					rgbArray[index++] = rgb != 0 ? ~0xffffff | rgb : 0;
				}
		return rgbArray;
	}

	public final int TRANSPERENCY_MASK = 0x2;
	private void readDataBlock(ByteBuffer buffer, int spriteId) throws IOException {
		int limit = buffer.limit();
		buffer.position(limit - 2);
		int childrenCount = buffer.getShort();
		childrenSprites = new Sprite[childrenCount];
		for (int i_93_ = 0; i_93_ < childrenCount; i_93_++)
			childrenSprites[i_93_] = new Sprite();
		buffer.position(limit - 7 - childrenCount * 8);
		maxWidth = buffer.getShort();
		maxHeight = buffer.getShort();
		int palletCount = (buffer.get() & 0xff) + 1;
		for (int i_97_ = 0; i_97_ < childrenCount; i_97_++)
			childrenSprites[i_97_].offsetX = buffer.getShort();
		for (int i_98_ = 0; i_98_ < childrenCount; i_98_++)
			childrenSprites[i_98_].offsetY = buffer.getShort();
		for (int i_99_ = 0; i_99_ < childrenCount; i_99_++)
			childrenSprites[i_99_].width = buffer.getShort();
		for (int i_100_ = 0; i_100_ < childrenCount; i_100_++)
			childrenSprites[i_100_].height = buffer.getShort();
		for (int childIdx = 0; childIdx < childrenCount; childIdx++) {
			Sprite child = childrenSprites[childIdx];
			child.xStart = maxWidth - child.width - child.offsetX;
			child.yStart = maxHeight - child.height - child.offsetY;
		}
		buffer.position(limit - 7 - childrenCount * 8 - (palletCount - 1) * 3);
		int[] palletArray = new int[palletCount];
		for (int i_102_ = 1; i_102_ < palletCount; i_102_++) {
			palletArray[i_102_] = ByteBufferUtils.getTriByte(buffer);
			if (palletArray[i_102_] == 0)
				palletArray[i_102_] = 1;
		}
		for (int i_103_ = 0; i_103_ < childrenCount; i_103_++)
			childrenSprites[i_103_].pallet = palletArray;
		buffer.position(0);
		for (int ptr = 0; ptr < childrenCount; ptr++) {
			Sprite sprite = childrenSprites[ptr];
			int pixelCount = sprite.width * sprite.height;
			sprite.pixels = new byte[pixelCount];
			readType = buffer.get();
			if ((readType & 0x2) == 0) {
				//no transp
				if ((readType & 0x1) == 0)
					for (int pixelPtr = 0; pixelPtr < pixelCount; pixelPtr++)
						sprite.pixels[pixelPtr] = buffer.get();
				else
					for (int widthPtr = 0; widthPtr < sprite.width; widthPtr++)
						for (int heightPtr = 0; heightPtr < sprite.height; heightPtr++)
							sprite.pixels[widthPtr + heightPtr * sprite.width] = buffer.get();
			} else {
				//has transparency
				boolean bool = false;
				sprite.transparencyBuffer = new byte[pixelCount];
				if ((readType & 0x1) == 0) {
					for (int i_110_ = 0; i_110_ < pixelCount; i_110_++)
						sprite.pixels[i_110_] = buffer.get();
					for (int i_111_ = 0; i_111_ < pixelCount; i_111_++) {
						byte b = sprite.transparencyBuffer[i_111_] = buffer.get();
						bool = bool | b != -1;
					}
				} else {
					for (int i_112_ = 0; i_112_ < sprite.width; i_112_++)
						for (int i_113_ = 0; i_113_ < sprite.height; i_113_++)
							sprite.pixels[i_112_ + i_113_ * sprite.width] = buffer.get();
					for (int i_114_ = 0; i_114_ < sprite.width; i_114_++)
						for (int i_115_ = 0; i_115_ < sprite.height; i_115_++) {
							byte b = sprite.transparencyBuffer[i_114_ + i_115_
							                                   * sprite.width] = buffer.get();
							bool = bool | b != -1;
						}
				}
				if (!bool)
					sprite.transparencyBuffer = null;
			}
		}
		convertImages(childrenSprites);
	}

	private void convertImages(Sprite[] children) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		for (int i = 0; i < children.length; i++) {
			if (children[i].width <= 0 || 0 >= children[i].height) {
				images.add(i, null);
				continue;
			}
			BufferedImage image = new BufferedImage(children[i].width,
					children[i].height, BufferedImage.TYPE_INT_RGB);
			image.setRGB(0, 0, children[i].width, children[i].height,
					children[i].convertPixelData(), 0, children[i].width);
			images.add(i, image);
			image.flush();
		}
		this.images = images;
	}

	public ByteBuffer encode() throws IOException {
		checkMaxDimensions();
		convertBufferedImages();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream buffer = new DataOutputStream(bos);
		for (int imageId = 0; imageId < images.size(); imageId++) {
			int pixelsMask = 0x1;
			//if (childrenSprites[imageId].transparencyBuffer != null)
			//	pixelsMask |= 0x2;
			buffer.write((byte) pixelsMask);
			if ((pixelsMask & 0x1) == 0)
				for (byte pixel : childrenSprites[imageId].pixels)
					buffer.write(pixel);
			else
				for (int widthPtr = 0; widthPtr < images.get(imageId).getWidth(); widthPtr++)
					for (int heightPtr = 0; heightPtr < images.get(imageId).getHeight(); heightPtr++)
						buffer.write(childrenSprites[imageId].pixels[widthPtr + heightPtr * images.get(imageId).getWidth()]);

		}
		for (int element : pallet)
			ByteBufferUtils.putTriByte(buffer, element);
		buffer.writeShort((short) maxWidth);
		buffer.writeShort((short) maxHeight);
		buffer.write((byte) (pallet.length - 1));
		for (int imageId = 0; imageId < images.size(); imageId++)
			buffer.writeShort((short) images.get(imageId).getMinX());
		for (int imageId = 0; imageId < images.size(); imageId++)
			buffer.writeShort((short) images.get(imageId).getMinY());
		for (int imageId = 0; imageId < images.size(); imageId++)
			buffer.writeShort((short) images.get(imageId).getWidth());
		for (int imageId = 0; imageId < images.size(); imageId++)
			buffer.writeShort((short) images.get(imageId).getHeight());
		buffer.writeShort((short) images.size());

		return ByteBuffer.wrap(bos.toByteArray());
	}

	public void checkMaxDimensions() {
		maxWidth = 0;
		maxHeight = 0;
		for (BufferedImage image : images){
			if (image.getWidth() > maxWidth)
				maxWidth = image.getWidth();
			if (image.getHeight() > maxHeight)
				maxHeight = image.getHeight();
		}

	}


	public int addColourToPallet(int rgb) {
		if (pallet.length >= 256)
			return pallet.length - 1;
		//rgb
		//r >> 16 & 0xff
		//b >> 8 & 0xff
		//g & 0xff
		//we are accessing the current pallet of the image and making sure if dosn't use
		//more then x colours for some reason
		for (int index = 0; index < pallet.length; index++)
			if (pallet[index] == rgb)
				return index;
		int[] inscreasedPallete = new int[pallet.length + 1];
		System.arraycopy(pallet, 0, inscreasedPallete, 0, pallet.length);
		inscreasedPallete[pallet.length] = rgb;
		pallet = inscreasedPallete;
		if (pallet.length > 256) {
			JOptionPane.showMessageDialog(null, "Too many colours in pallet! Lower quality of delete child sprites.");
		}
		return pallet.length - 1;
	}
	/**
	 * Converts the buffered image objects added to images list into childrenSprites
	 */
	public void convertBufferedImages() {
		Sprite[] newSprites = new Sprite[images.size()];
		if (pallet == null)
			pallet = new int[]{0};
		for (int index = 0; index < images.size(); index++) {
			newSprites[index] = new Sprite();
			BufferedImage image = images.get(index);
			int[] rgbArray = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgbArray, 0, image.getWidth());
			newSprites[index].pixels = new byte[image.getWidth() * image.getHeight()];
			newSprites[index].transparencyBuffer = new byte[image.getWidth() * image.getHeight()];
			newSprites[index].height = image.getHeight();
			newSprites[index].width = image.getWidth();

			boolean hasTransparency = false;
			for (int pixel = 0; pixel < newSprites[index].pixels.length; pixel++) {
				//ensure we only get the first 3 numbers (RGB)
				int rgb = rgbArray[pixel] & 0xFFFFFF;
				int palletIndex = addColourToPallet(rgb) & 0xFF;
				newSprites[index].pixels[pixel] = (byte)palletIndex;
				//if the colour has transparency we add it to the buffer
				if (rgb >> 24 != 0) {
					newSprites[index].transparencyBuffer[pixel] = (byte) (rgb >> 24);
					hasTransparency = true;
				}
			}
			if (!hasTransparency) {
				newSprites[index].transparencyBuffer = null;
			}
		}
		childrenSprites = newSprites;
	}

	public static Sprite get(Cache store, int spriteId) {
		if (!sprites.containsKey(spriteId))
			try {
				System.out.println("getting sprite: " +spriteId);
				Sprite sprite = new Sprite(store.read(8, spriteId), spriteId);
				sprites.put(spriteId, sprite);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return sprites.get(spriteId);
	}

	public static Sprite get(Cache store, String name) {
		int spriteId = -1;
		try {
			spriteId = store.getFileId(8, name);
			if (!sprites.containsKey(spriteId) && spriteId > -1) {
				Sprite sprite = new Sprite(store.read(8, spriteId), spriteId);
				sprites.put(spriteId, sprite);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sprites.get(spriteId);
	}
	public void writeToCache(Cache cache) {
		try {
			if (id >= 0) {
				Container c = new Container(2, encode(), 1);
				cache.write(FileSystems.SPRITES.getID(), id, c);
			} else {
				System.out.println("ID NOT SET!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Sprite addSprite(Cache cache, BufferedImage baseSprite) {
		try {
			Sprite sprite = new Sprite();
			sprite.images.add(baseSprite);
			sprites.put(cache.getFileCount(8), sprite);
			Container c = new Container(2, sprite.encode(), 1);
			cache.write(8, cache.getFileCount(8), c);
			sprite.container = c;
			return sprite;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
