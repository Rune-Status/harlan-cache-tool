package net.openrs.cache.cs2;

import rs2.client.ClientScript;
import rs2.client.HashTable;
import net.openrs.cache.Cache;
/**
 *
 * @author Harlan
 *
 */
public class CS2Decompiler {



	public static void decompileScriptInfo(Cache cache, int scriptID) {

		ClientScript script = ClientScript.loadScript(cache, scriptID);
		System.out.println("SCRIPT INFO");
		System.out.println("_____________");
		System.out.println("id: "+scriptID);
		System.out.println("args (int): "+script.intArgumentCount+ " (string): "+script.stringArgumentCount);
		System.out.println("stack (int): "+script.intStackCount+" (string): "+script.stringStackCount);
		System.out.println("JUMP TABLE");
		System.out.println("_____________");
		if (script.jumpTables != null) {
			System.out.println("count: "+script.jumpTables.length);
			for (int i =0; i < script.jumpTables.length; i++) {
				/**
				 * SWITCH STATEMENT
				 */
				HashTable table = script.jumpTables[i];
				System.out.println("	table: "+i+ " id: "+table.id+" size: "+table.size);

			}
		}
		System.out.println("SCRIPT TABLE");
		System.out.println("_____________");
		System.out.println("opcodes: ");
		for (int i = 0; i < script.opcodes.length; i++) {
			System.out.println("i: "+i+" opcode: "+script.opcodes[i]);
		}
		System.out.println("operands: ");
		for (int i = 0; i < script.operands.length; i++) {
			System.out.println("i : "+i+" operand: "+script.operands[i]);
		}
		System.out.println("string operands: ");
		for (int i =0;i < script.stringOperands.length; i++) {

			System.out.println("i : "+i+" string operand: "+ (script.stringOperands[i] != null ? script.stringOperands[i].toRealString() : "null"));
		}
		System.out.println("DECOMPILED");
		System.out.println("_____________");
		CodeFormatter formatter = new CodeFormatter();
		OpCodeParser parser = new OpCodeParser(script);
		formatter.parse(generateHeader(script, scriptID));
		for (int opcode : script.opcodes) {
			try {
				String code = parser.parse(opcode);
				if (code.length() > 0)
					formatter.parse(code);
			} catch (Exception e) {
				//if
				//e.get
			}
		}
		formatter.parse("}");
		formatter.publish(System.out);
	}

	public static String generateHeader(ClientScript script, int id) {
		StringBuilder builder = new StringBuilder();
		builder.append("void script_" + id+ "(");
		int argCounter = 0;
		boolean hasArgs = script.intArgumentCount != 0 || script.stringArgumentCount  != 0;
		for (; argCounter < script.intArgumentCount; argCounter++) {
			builder.append("int arg"+argCounter+", ");
		}
		for (; argCounter < script.stringArgumentCount + script.intArgumentCount; argCounter++) {
			builder.append("String arg"+argCounter+", ");
		}
		return (hasArgs ? builder.substring(0, builder.length() - 2) : builder.toString()) + ") {";

	}

}
