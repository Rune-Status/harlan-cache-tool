package net.openrs.cache.def;

import harlan.CacheViewer;
import harlan.util.FileSystems;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.openrs.cache.Archive;
import rs2.client.ByteBuffer2;
import rs2.client.JagexString;

/**
 *
 * @author Harlan
 *
 */
public class ItemDefinitions {

	public static int REVISION = 498;


	public int modelRotation1 = 0;
	public int maleDialogueHat;
	public int lendItemIDTest = -1;
	public int stackable = 0;
	public int modelZoom;
	public int diagionalRotation;
	public int[] stackIDs;
	public int modelOffset1;
	public boolean unknownFlag2;
	public JagexString name = JagexString.empty();
	public JagexString description = JagexString.empty();
	public short[] reassignOld;
	public int[][] shadowData;
	protected boolean membersObject;
	public int modelBrightness;
	public JagexString[] actions;
	public int lendItemID;
	public int femaleWieldY;
	public int modelSizeX;
	public int maleEmblem;
	public byte[] recoulerCacheIndices;
	public int modelOffset2;
	public int femaleEmblem;
	public short[] reassignNew;
	public int maleEquip2;
	public int id;
	public int maleWieldY;
	public int femaleDialogueHat;
	public int maleWieldX;
	public int femaleWieldX;
	public int modelID;
	public int[] stackAmounts;
	public short[] modifiedModelColors;
	public int femaleEquip1;
	public int modelSizeY;
	private short[] originalModelColors;
	public int team;
	public JagexString[] groundActions;
	public int maleDialogue;
	public int modelRotation2;
	public int femaleDialogue;
	public int value;
	public int notedTemplateID;
	public int unknownFlag;
	public int modelShadowing;
	public int modelSizeZ;
	public int notedID;

	final void copyValues(ItemDefinitions item, ItemDefinitions item2) {
		modelRotation2 = item.modelRotation2;
		modelOffset2 = item.modelOffset2;
		modelZoom = item.modelZoom;
		reassignNew = item.reassignNew;
		modelID = item.modelID;
		value = item2.value;
		originalModelColors = item.originalModelColors;
		stackable = 1;
		diagionalRotation = item.diagionalRotation;
		name = item2.name;
		reassignOld = item.reassignOld;
		membersObject = item2.membersObject;
		modelRotation1 = item.modelRotation1;
		modelOffset1 = item.modelOffset1;
		modifiedModelColors = item.modifiedModelColors;
		recoulerCacheIndices = item.recoulerCacheIndices;
	}


	final void readValueLoop(ByteBuffer2 buffer) {
		for (;;) {
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0) {
				break;
			}
			readValues(opcode, buffer);
		}
	}

	final ItemDefinitions getStackedItem(int stackSize) {
		if (stackIDs != null && stackSize > 1) {
			int newStackId = -1;
			for (int i_31_ = 0; i_31_ < 10; i_31_++) {
				if ((stackAmounts[i_31_] ^ 0xffffffff) >= (stackSize ^ 0xffffffff) && stackAmounts[i_31_] != 0) {
					newStackId = stackIDs[i_31_];
				}
			}
			if ((newStackId ^ 0xffffffff) != 0) {
				return ItemDefinitions.forID(newStackId);
			}
		}
		return this;
	}

	public ByteBuffer convert(int revision) {
		ByteBuffer buf = ByteBuffer.allocate(500);
		if (!isDefault(modelID)) {
			buf.put((byte)1);
			buf.putShort((short)modelID);
		}
		if (!isDefault(name)) {
			buf.put((byte)2);
			name.writeToBuffer(buf);
		}
		if (!isDefault(description)) {
			buf.put((byte)3);
			description.writeToBuffer(buf);
		}
		if (!isDefault(description)) {
			buf.put((byte)4);
			buf.putShort((short)modelZoom);
		}
		if (!isDefault(modelRotation1)) {
			buf.put((byte)5);
			buf.putShort((short)modelRotation1);
		}
		if (!isDefault(modelRotation2)) {
			buf.put((byte)6);
			buf.putShort((short)modelRotation2);
		}
		if (!isDefault(modelOffset2)) {
			buf.put((byte)7);
			buf.putShort((short)modelOffset2);
		}
		if (!isDefault(modelOffset1)) {
			buf.put((byte)8);
			buf.putShort((short)modelOffset1);
		}
		//skip 9 & 10
		if (!isDefault(stackable)) {
			buf.put((byte)11);
		}
		if (!isDefault(value)) {
			buf.put((byte)12);
			buf.putInt(value);
		}
		if (!isDefault(membersObject)) {
			buf.put((byte)16);
		}
		if (!isDefault(maleWieldX) || !isDefault(maleWieldY)) {
			buf.put((byte)23);
			buf.putShort((short)maleWieldX);
			if (revision == 498) {
				buf.put((byte)maleWieldY);
			}
		}
		if (!isDefault(femaleWieldX)) {
			buf.put((byte)26);
			buf.putShort((short)femaleWieldX);
		}
		for (int i = 0; i < actions.length; i++) {
			if (!isDefault(actions[i])) {
				buf.put((byte)(35 + i));
				actions[i].writeToBuffer(buf);
			}
		}
		if (!isDefault(femaleEmblem)) {
			buf.put((byte)79);
			buf.putShort((short)femaleEmblem);
		}
		if (!isDefault(femaleDialogue)) {
			buf.put((byte)91);
			buf.putShort((short)femaleDialogue);
		}
		if (!isDefault(maleDialogueHat)) {
			buf.put((byte)92);
			buf.putShort((short)maleDialogueHat);
		}
		if (!isDefault(femaleDialogueHat)) {
			buf.put((byte)93);
			buf.putShort((short)femaleDialogueHat);
		}
		if (!isDefault(unknownFlag)) {
			buf.put((byte)96);
			buf.put((byte)unknownFlag);
		}
		if (!isDefault(modelSizeY)) {
			buf.put((byte)111);
			buf.putShort((short)modelSizeY);
		}
		if (!isDefault(modelSizeZ)) {
			buf.put((byte)112);
			buf.putShort((short)modelSizeZ);
		}
		if (!isDefault(modelBrightness)) {
			buf.put((byte)113);
			buf.put((byte)modelBrightness);
		}
		//skip 116 & 177
		if (!isDefault(modelShadowing)) {
			buf.put((byte)114);
			buf.put((byte)modelShadowing);
		}
		if (!isDefault(team)) {
			buf.put((byte)115);
			buf.put((byte)team);
		}
		if (!isDefault(lendItemID)) {
			buf.put((byte)122);
			buf.putShort((short)lendItemID);
		}
		for (int i = 0; i < shadowData.length; i++) {
			if (!isDefault(shadowData[i])) {
				buf.put((byte)124);
				buf.put((byte)i);
				for (int i2 = 0; i2 < 6; i2++) {
					buf.putShort((short)shadowData[i][i2]);
				}
			}
		}
		//skip 125 & 126
		//skip 249
		if (!isDefault(lendItemIDTest)) {
			buf.put((byte)121);
			buf.putShort((short)lendItemIDTest);
		}
		if (!isDefault(modelSizeX)) {
			buf.put((byte)110);
			buf.putShort((short)modelSizeX);
		}
		for (int i = 0 ; i < stackIDs.length; i++) {
			if (!isDefault(stackIDs[i])) {
				buf.put((byte)(100 + i));
				buf.putShort((short)stackIDs[i]);
				buf.putShort((short)stackAmounts[i]);
			}
		}
		if (!isDefault(notedTemplateID)) {
			buf.put((byte)98);
			buf.putShort((short)notedTemplateID);
		}
		if (!isDefault(notedID)) {
			buf.put((byte)97);
			buf.putShort((short)notedID);
		}
		if (!isDefault(diagionalRotation)) {
			buf.put((byte)95);
			buf.putShort((short)diagionalRotation);
		}
		if (!isDefault(maleDialogue)) {
			buf.put((byte)90);
			buf.putShort((short)maleDialogue);
		}
		if (!isDefault(maleEmblem)) {
			buf.put((byte)78);
			buf.putShort((short)maleEmblem);
		}
		if (!isDefault(unknownFlag2)) {
			buf.put((byte)65);
		}
		if (!isDefault(recoulerCacheIndices)) {
			buf.put((byte)42);
			buf.put((byte)recoulerCacheIndices.length);
			for (byte recoulerCacheIndice : recoulerCacheIndices) {
				buf.put(recoulerCacheIndice);
			}
		}
		if (!isDefault(reassignOld)) {
			buf.put((byte)41);
			buf.put((byte)reassignOld.length);
			for (int i = 0; i < reassignOld.length; i++) {
				buf.putShort(reassignOld[i]);
				buf.putShort(reassignNew[i]);
			}
		}
		if (!isDefault(modifiedModelColors)) {
			buf.put((byte)40);
			buf.put((byte)modifiedModelColors.length);
			for (short modifiedModelColor : modifiedModelColors) {
				buf.putShort(modifiedModelColor);
				buf.putShort(modifiedModelColor);
			}
		}
		for (int i = 0; i < groundActions.length; i++) {
			if (!isDefault(groundActions[i])) {
				buf.put((byte)(30 + i));
				groundActions[i].writeToBuffer(buf);
			}
		}
		if (!isDefault(femaleEquip1) || !isDefault(femaleWieldY)) {
			buf.put((byte)23);
			buf.putShort((short)femaleEquip1);
			if (revision == 498) {
				buf.put((byte)femaleWieldY);
			}
		}
		if (!isDefault(maleEquip2)) {
			buf.put((byte)24);
			buf.putShort((short)femaleEquip1);
		}
		buf.flip();
		return buf;
	}


	public boolean isDefault(Object o) {
		if (o == null)
			return true;
		else if (o instanceof Integer) {
			return ((Integer)o).intValue() == 0;
		} else if (o instanceof JagexString) {
			return ((JagexString)o).toRealString().length() > 0;
		}
		return false;
	}

	private final void readValues(int opcode, ByteBuffer2 buffer) {

		switch(opcode) {
			case 1:
				modelID = buffer.getUnsignedShort();
				break;
			case 2:
				name = buffer.readString();
				break;
			case 3:
				description = buffer.readString();
				break;
			case 4:
				modelZoom = buffer.getUnsignedShort();
				break;
			case 5:
				modelRotation1 = buffer.getUnsignedShort();
				break;
			case 6:
				modelRotation2 = buffer.getUnsignedShort();
				break;
			case 7:
				modelOffset2 = buffer.getUnsignedShort();
				if ((modelOffset2 ^ 0xffffffff) < -32768) {
					modelOffset2 -= 65536;
				}
				break;
			case 8:
				modelOffset1 = buffer.getUnsignedShort();
				if (modelOffset1 > 32767) {
					modelOffset1 -= 65536;
				}

				break;
			case 9:
				buffer.getShort();
				break;
			case 10:
				buffer.getShort();
				break;
			case 11:
				stackable = 1;
				break;
			case 16:
				membersObject = true;
				break;
			case 23:
				maleWieldX = buffer.getUnsignedShort();
				if (REVISION == 498)
					maleWieldY = buffer.getUnsignedByte();
				break;
			case 12:
				value = buffer.getInt();
				break;


			case 26:
				femaleWieldX = buffer.getUnsignedShort();
				break;
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
				actions[opcode - 35] = buffer.readString();
				break;
			case 79:
				femaleEmblem = buffer.getUnsignedShort();
				break;
			case 91:
				femaleDialogue = buffer.getUnsignedShort();
				break;
			case 92:
				maleDialogueHat = buffer.getUnsignedShort();
				break;
			case 93:
				femaleDialogueHat = buffer.getUnsignedShort();
				break;
			case 96:
				unknownFlag = buffer.getUnsignedByte();
				break;
			case 111:
				modelSizeY = buffer.getUnsignedShort();
				break;
			case 112:
				modelSizeZ = buffer.getUnsignedShort();
				break;
			case 113:
				modelBrightness = buffer
						.readSignedByte();
				break;
			case 116:
				@SuppressWarnings("unused")
				int lendID = buffer.getUnsignedByte();
				break;
			case 117:
				@SuppressWarnings("unused")
				int lentItemId = buffer.getUnsignedShort();
				break;

			case 114:
				modelShadowing = 5 * buffer.readSignedByte();
				break;
			case 115:
				team = buffer.getUnsignedByte();
				break;
			case 122:
				lendItemID = buffer.getUnsignedShort();
				break;
			case 124:
				if (shadowData == null) {
					shadowData = new int[11][];
				}
				int i_32_ = buffer.getUnsignedByte();
				shadowData[i_32_] = new int[6];
				for (int i_33_ = 0; i_33_ < 6; i_33_++)
					shadowData[i_32_][i_33_] = buffer.getShort();

				break;
			case 125:
			case 126:
				//some 508 weird data!
				buffer.read3Bytes();
				break;
			case 249:
				//more weird 508 data
				//packet absolutely useless, has no unique data
				int len = buffer.getUnsignedByte();
				for (int i = 0; i < len; i++) {
					int useRSI = buffer.getUnsignedByte();
					if (useRSI != 0) {
						buffer.readString();
					} else {
						buffer.readIntV3();
					}
				}
				break;
			case 121:
				lendItemIDTest = buffer
						.getUnsignedShort();
				break;
			case 110:
				modelSizeX = buffer
						.getUnsignedShort();
				break;
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
			case 108:
			case 109:
				if (stackIDs == null) {
					stackAmounts = new int[10];
					stackIDs = new int[10];
				}
				stackIDs[opcode + -100] = buffer.getUnsignedShort();
				stackAmounts[-100 + opcode] = buffer.getUnsignedShort();
				break;
			case 98:
				notedTemplateID = buffer
						.getUnsignedShort();
				break;
			case 97:
				notedID = buffer.getUnsignedShort();
				break;
			case 95:
				diagionalRotation = buffer.getUnsignedShort();
				break;
			case 90:
				maleDialogue = buffer.getUnsignedShort();
				break;
			case 78:
				maleEmblem = buffer.getUnsignedShort();
				break;
			case 65:
				unknownFlag2 = true;
				break;
			case 42:
				int i_34_ = buffer.getUnsignedByte();
				recoulerCacheIndices = new byte[i_34_];
				for (int i_35_ = 0; (i_35_ ^ 0xffffffff) > (i_34_ ^ 0xffffffff); i_35_++)
					recoulerCacheIndices[i_35_] = buffer.readSignedByte();
				break;
			case 41:
				int i_36_ = buffer.getUnsignedByte();
				reassignOld = new short[i_36_];
				reassignNew = new short[i_36_];
				for (int i_37_ = 0; (i_36_ ^ 0xffffffff) < (i_37_ ^ 0xffffffff); i_37_++) {
					reassignOld[i_37_] = (short) buffer.getUnsignedShort();
					reassignNew[i_37_] = (short) buffer.getUnsignedShort();
				}
				break;
			case 40:
				int i_38_ = buffer.getUnsignedByte();
				modifiedModelColors = new short[i_38_];
				originalModelColors = new short[i_38_];
				for (int i_39_ = 0; (i_39_ ^ 0xffffffff) > (i_38_ ^ 0xffffffff); i_39_++) {
					originalModelColors[i_39_] = (short) buffer.getUnsignedShort();
					modifiedModelColors[i_39_] = (short) buffer.getUnsignedShort();
				}
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
				groundActions[-30 + opcode] = buffer.readString();
				if (groundActions[opcode + -30].contains(JagexString.createString("Hidden"))) {
					groundActions[opcode - 30] = null;
				}
				break;
			case 25:
				femaleEquip1 = buffer.getUnsignedShort();
				if (REVISION == 498)
					femaleWieldY = buffer.getUnsignedByte();
				break;
			case 24:
				maleEquip2 = buffer.getUnsignedShort();
				break;

			default:
				System.out.println("Missing opcode: " + opcode);
		}

	}


	final void copyDefinitions(ItemDefinitions item1, ItemDefinitions item2) {
		recoulerCacheIndices = item1.recoulerCacheIndices;
		femaleDialogueHat = item2.femaleDialogueHat;
		modelZoom = item1.modelZoom;
		modelID = item1.modelID;
		modelRotation1 = item1.modelRotation1;
		modelRotation2 = item1.modelRotation2;
		maleDialogueHat = item2.maleDialogueHat;
		femaleWieldX = item2.femaleWieldX;
		membersObject = item2.membersObject;
		modelOffset1 = item1.modelOffset1;
		maleEquip2 = item2.maleEquip2;
		femaleEmblem = item2.femaleEmblem;
		femaleEquip1 = item2.femaleEquip1;
		maleWieldX = item2.maleWieldX;
		modifiedModelColors = item1.modifiedModelColors;
		originalModelColors = item1.originalModelColors;
		actions = new JagexString[5];
		femaleDialogue = item2.femaleDialogue;
		maleDialogue = item2.maleDialogue;
		team = item2.team;
		groundActions = item2.groundActions;
		reassignOld = item1.reassignOld;
		name = item2.name;
		modelOffset2 = item1.modelOffset2;
		maleEmblem = item2.maleEmblem;
		value = 0;
		reassignNew = item1.reassignNew;
		diagionalRotation = item1.diagionalRotation;
		if (item2.actions != null) {
			for (int i = 0; i < 4; i++)
				actions[i] = item2.actions[i];
		}
		actions[4] = JagexString.createString("Discard");
	}

	static final int getFileId(int itemId) {
		return itemId >>> 8;
	}

	static final int getChildId(int itemId) {
		return itemId & 0xff;
	}
	static int itemCount = -1;
	public static final int getItemCount() {
		if (itemCount > 0)
			return itemCount;
		try {
			int total = 0;
			int count = CacheViewer.getCacheViewer().getCache().getFileCount(FileSystems.ITEM.getID());
			//count starts at 0 inclusive, don't want to include last one
			count--;
			//255 items per file container
			total = count * 255;
			//except for the last one
			//which we need to add manually
			int lastIndexCount = CacheViewer.getCacheViewer().getCache().getEntry(FileSystems.ITEM, count).childSize();
			total += lastIndexCount;
			itemCount = total;
			return total;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static Map<Integer, ItemDefinitions> defs = new HashMap<Integer, ItemDefinitions>();

	public static void preloadItems(boolean usingConfigCache) {
		if (!defs.isEmpty())
			return;

		try {
			/*int id1 = 1 << 8 | 16;

			ByteBuffer buf1 = CacheViewer.getCacheViewer().getCache().read(FileSystems.ITEM.getID(), 1, 16);
			int cap1 = buf1.capacity();
			System.out.println("read item! : " + cap1);

			loadItem(buf1, id1);*/

			long start = System.currentTimeMillis();
			if (!usingConfigCache) {
				int count = CacheViewer.getCacheViewer().getCache().getFileCount(FileSystems.ITEM.getID());
				for (int fileId = 0; fileId < count; fileId++) {
					System.out.println("Decoding archive: " + fileId);
					Archive archive = CacheViewer.getCacheViewer().getCache().getArchive(FileSystems.ITEM.getID(), fileId);
					for (int childId = 0; childId < archive.size(); childId++) {
						ByteBuffer buf = archive.getEntry(childId);
						int id = fileId << 8 | childId;
						loadItem(buf, id);
					}
					System.gc();
				}
			} else {
				Archive archive = CacheViewer.getCacheViewer().getCache().getArchive(FileSystems.CONFIG.getID(), ConfigIndex.forValue("items"));
				System.out.println("Arhive size: " + archive.size());
				for (int fileId = 0; fileId < archive.size(); fileId++) {
					ByteBuffer buf = archive.getEntry(fileId);
					loadItem(buf, fileId);
				}
			}
			for (ItemDefinitions def : defs.values()) {
				if ((def.notedTemplateID ^ 0xffffffff) != 0) {
					def.copyValues(forID(def.notedTemplateID), forID(def.notedID));
				}
				if (def.lendItemID != -1) {
					def.copyDefinitions(forID(def.lendItemID), forID(def.lendItemIDTest));
				}
			}

			System.out.println("took :"+ (System.currentTimeMillis() - start) + " milliseconds!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void loadItem(ByteBuffer buf, int id) {
		if (buf.capacity() <= 0) {
			System.out.println("Failed to read item: " + id);
			return;
		}
		ItemDefinitions item = new ItemDefinitions();
		item.id = id;
		if (buf != null) {
			item.readValueLoop(new ByteBuffer2(buf.array()));
		}
		defs.put(id, item);
	}

	public static void parseDumped() {
		Path f = Paths.get("C:\\Users\\Harlan\\Desktop\\cache tool\\items.dat");

		try {
			byte[] data = Files.readAllBytes(f);


			ByteBuffer2 buf = new ByteBuffer2(data);

			int id = 0;
			while (buf.canRead()) {
				ItemDefinitions item = new ItemDefinitions();
				item.id = id++;
				item.readValueLoop(buf);
				System.out.println(item.name);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final ItemDefinitions forID(int fileId, int childId) {
		int id = fileId << 8 | childId;
		return forID(id);
	}

	public static final ItemDefinitions forID(int id) {
		if (defs.containsKey(id))
			return defs.get(id);
		return null;
	}

	public ItemDefinitions() {
		maleDialogueHat = -1;
		lendItemID = -1;
		modelOffset1 = 0;
		modelSizeX = 128;
		unknownFlag2 = false;
		femaleEmblem = -1;
		maleEquip2 = -1;
		femaleDialogueHat = -1;
		team = 0;
		modelSizeY = 128;
		maleWieldX = -1;
		membersObject = false;
		maleDialogue = -1;
		modelBrightness = 0;
		femaleDialogue = -1;
		groundActions = new JagexString[] { null, null, JagexString.createString("Take"), null, null };
		modelZoom = 2000;
		maleEmblem = -1;
		femaleEquip1 = -1;
		actions = new JagexString[] { null, null, null, null, JagexString.createString("Drop") };
		modelOffset2 = 0;
		femaleWieldY = 0;
		modelRotation2 = 0;
		value = 1;
		diagionalRotation = 0;
		notedTemplateID = -1;
		femaleWieldX = -1;
		maleWieldY = 0;
		unknownFlag = 0;
		modelShadowing = 0;
		notedID = -1;
		modelSizeZ = 128;
	}

}
