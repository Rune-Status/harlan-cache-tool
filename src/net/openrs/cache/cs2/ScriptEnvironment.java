package net.openrs.cache.cs2;

import rs2.client.ClientScript;


/**
 *
 * @author Harlan
 *
 */
public class ScriptEnvironment {

	private final ClientScript script;
	public ScriptEnvironment(ClientScript script) {
		this.script = script;
		intStack = new String[1000];
		stringStack = new String[1000];

	}

	public String[] intStack;
	public String[] stringStack;
	public String[] tempGlobalStack = new String[2000];
	public int iscc;//int
	public int sscc;//string
	public int pc = -1;

	public void reset() {
		iscc = 0;
		pc = -1;
		intStack = new String[1000];
	}

	public void addToIntStack(String s) {
		intStack[iscc++] = s;
	}
	public void popToGlobalTempStack(int index) {
		tempGlobalStack[index] = popLastInt();
	}
	public void setStackToGlobalPop(int index) {
		intStack[iscc++] = tempGlobalStack[index];

	}
	public String getStackArg(int index) {
		return intStack[index];
	}
	public String popLastInt() {
		return intStack[--iscc];
	}
	public String lookNextInt() {
		return intStack[iscc+1];

	}
	public String currentInt() {
		return intStack[iscc];
	}

	public void nextPc() {
		pc++;
	}

	public int getPc() {
		return pc;
	}

	public String[] getStringStack() {
		String[] stringArgs = new String[script.stringArgumentCount];
		for (int stackInd = 0; (script.stringStackCount ^ 0xffffffff) < (stackInd ^ 0xffffffff); stackInd++)
			stringArgs[stackInd] = stringStack[stackInd + sscc - script.stringStackCount];
		return stringArgs;
	}

	public String[] getIntStack() {
		String[] intArgs = new String[script.intArgumentCount];
		for (int intScc = 0; intScc < script.intStackCount; intScc++) {
			int index = iscc + intScc - script.intStackCount - 1;
			System.out.println("index: "+index);
			intArgs[intScc] = String.valueOf(intStack[index]);
		}
		return intArgs;
	}

	public void addToStringStack(String string) {
		stringStack[sscc++] = string;
	}

	public void debugStacks() {
		for (int i = 0;i < intStack.length; i++) {
			if (intStack[i] != null)
				System.out.println("i : "+i+" int: "+intStack[i]);
		}
		for (int i = 0;i < stringStack.length; i++) {
			if (intStack[i] != null)
				System.out.println("i : "+i+" string: "+stringStack[i]);
		}
	}

	public int toInt(String s) {
		if (s == null)
			return 0;
		return Integer.valueOf(s);
	}

	public String popLastString() {
		return stringStack[--sscc];

	}

}
