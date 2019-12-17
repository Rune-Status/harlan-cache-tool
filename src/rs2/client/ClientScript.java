package rs2.client;

import harlan.util.FileSystems;

import java.io.IOException;

import net.openrs.cache.Cache;
import net.openrs.util.ByteBufferUtils;



/* Class23_Sub13_Sub23 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class ClientScript
{
	public int stringArgumentCount;
	public HashTable[] jumpTables;
	public int[] opcodes;
	public JagexString[] stringOperands;
	public int intStackCount;
	public int stringStackCount;
	public int intArgumentCount;
	public JagexString name;
	public int[] operands;

	public static ClientScript loadScript(Cache cache, int scriptID) {
		byte[] data = null;
		try {
			data = cache.read(FileSystems.CS2.getID(), scriptID).getWritableData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data == null) {
			return null;
		}

		ClientScript script = new ClientScript();
		/**
		 * loading script: 41 data len :40 last byte: 1 2nd last byte: 0
len: 1 offset: 25 count: 5
		 */
		ByteBuffer2 buffer = new ByteBuffer2(data);

		/**
		 * Read the jump table offset part of the header
		 */
		buffer.position = buffer.buffer.length - 2;
		int jumpTableOffset = buffer.getShort();
		/**
		 * We read the header of the cs2 file, containing sizes of stuff
		 */
		int headerStart = -14 + buffer.buffer.length - jumpTableOffset;
		buffer.position = headerStart;
		int count = buffer.getInt();
		script.intArgumentCount = buffer.getUnsignedShort();
		script.stringArgumentCount = buffer.getUnsignedShort();
		script.intStackCount = buffer.getUnsignedShort();
		script.stringStackCount = buffer.getUnsignedShort();
		int jumpTableCount = buffer.getUnsignedByte();
		if (jumpTableCount > 0) {
			script.jumpTables = new HashTable[jumpTableCount];
			for (int tables = 0; (~tables) > (~jumpTableCount); tables++) {
				int instructions = buffer.getShort();
				HashTable table = new HashTable(Misc.encodeForHashTable(instructions));
				script.jumpTables[tables] = table;
				while ((~instructions--) < -1) {
					int index = buffer.getInt();
					int value = buffer.getInt();
					table.set(new IntegerNode(value), index);
				}
			}
		}
		/**
		 * Now we read the actual contents of the cs2 file, here is our operants and stuff
		 */
		buffer.position = 0;
		script.name = ByteBufferUtils.getCheckedJagexString(buffer);
		script.operands = new int[count];
		script.stringOperands = new JagexString[count];
		script.opcodes = new int[count];
		int operand = 0;
		while (headerStart > buffer.position) {
			int opcode = buffer.getUnsignedShort();
			if (opcode != 3) {
				if (opcode < 100 && opcode != 21 && opcode != 38 && opcode != 39) {
					script.operands[operand] = buffer.getInt();
				} else {
					script.operands[operand] = buffer.getUnsignedByte();
				}
			} else {
				script.stringOperands[operand] = ByteBufferUtils.getJagexString(buffer);
			}
			script.opcodes[operand++] = opcode;
		}
		return script;
	}
}
