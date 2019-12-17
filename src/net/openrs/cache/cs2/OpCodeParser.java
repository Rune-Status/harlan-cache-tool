package net.openrs.cache.cs2;

import rs2.client.ClientScript;
import rs2.client.InterfaceID;
import rs2.client.Misc;
/**
 *
 * @author Harlan
 *
 */
public class OpCodeParser {
	private final ClientScript script;
	public OpCodeParser(ClientScript script) {
		env = new ScriptEnvironment(script);
		this.script = script;
	}

	private final ScriptEnvironment env;

	public String parse(int opcode) throws Exception {
		env.nextPc();
		String widget = null;
		Object[] scriptArgs = null;
		if ((opcode < 1000 || opcode >= 1100) && (opcode < 2000 || opcode >= 2100)) {
			if ((opcode < 1100 || opcode >= 1200) && (opcode < 2100 || opcode >= 2200)) {
				if ((opcode < 1200 || opcode >= 1300) && (opcode < 2200 || opcode >= 2300)) {
					if ((opcode < 1300 || opcode >= 1400) && (opcode < 2300 || opcode >= 2400)) {
						if (opcode >= 1400 && opcode < 1500 || opcode >= 2400 && opcode < 2500) {
							if (opcode >= 2000) {
								opcode -= 1000;
								InterfaceID id  = InterfaceID.decode(Integer.valueOf(env.popLastInt()));
								widget = "new WidgetPointer("+id.getId()+", "+id.getChildId()+")";
								int[] is_45_ = null;
								String class16 = env.popLastString();
								if (class16.length()  > 0
										&& class16.charAt(class16.length() -1) == 89) {
									int i_46_ = Integer.valueOf(env.popLastInt());
									if (i_46_ > 0) {
										is_45_ = new int[i_46_];
										while ((i_46_-- ^ 0xffffffff) < -1)
											is_45_[i_46_] = Integer.valueOf(env.popLastInt());
									}
									class16 = class16.substring(-1 + class16.length(), 0);
								}
								scriptArgs = new Object[1 + class16.length()];
								for (int i_48_ = scriptArgs.length + -1; i_48_ >= 1; i_48_--) {
									if (class16.charAt(i_48_ + -1) != 115) {
										scriptArgs[i_48_] = new Integer(env.popLastInt());
									} else {
										scriptArgs[i_48_] = env.popLastString();
									}
								}
								int i_49_ = Integer.valueOf(env.popLastInt());
								if (i_49_ != -1) {
									scriptArgs[0] = new Integer(i_49_);
								} else {
									scriptArgs = null;
								}
							}
						}
					}
				}

			} else {
				opcode -= 1000;
				InterfaceID id  = InterfaceID.decode(Integer.valueOf(env.popLastInt()));
				widget = "new WidgetPointer("+id.getId()+", "+id.getChildId()+")";
			}
		}
		switch(opcode) {
			case 0:
				env.addToIntStack(String.valueOf(script.operands[env.getPc()]));
				return "int i" + env.iscc + " = " +  String.valueOf(script.operands[env.getPc()])  + ";";
			case 3:
				env.addToStringStack(script.stringOperands[env.pc].toRealString());
				return "String s" + env.iscc + " = " +  script.stringOperands[env.pc].toRealString() + ";";
			case 6:
				env.pc += script.operands[env.pc];
				break;
			case 8:
				env.iscc -= 2;
				if (env.intStack[1 + env.iscc] == env.intStack[env.iscc] ) {
					env.pc += script.operands[env.pc];
				}
				break;
			case 10:
				env.iscc -= 2;
				if (Integer.valueOf(env.intStack[1 + env.iscc]) < Integer.valueOf(env.intStack[env.iscc])) {
					env.pc += script.operands[env.pc];
				}
				break;
			case 25:
				int i_13_ = script.operands[env.pc];
				env.addToIntStack("bitconfig_"+i_13_);
				break;
			case 31:
				env.iscc -= 2;
				if (env.toInt(env.intStack[1 + env.iscc]) >= env.toInt(env.intStack[env.iscc])) {
					env.pc += script.operands[env.pc];
				}
				break;
			case 33:
				env.addToIntStack("arg"+script.operands[env.getPc()]);
				break;
			case 34:
				return "arg"+script.operands[env.getPc()] + " = " + env.popLastInt() + ";";
			case 35:
				env.addToStringStack("arg" + (script.intArgumentCount + script.operands[env.getPc()]));
				break;
			case 36:
				return "arg"+(script.intArgumentCount +script.operands[env.getPc()]) + " = " + env.popLastString() + ";";

			case 40:
				StringBuilder builder = new StringBuilder();
				builder.append("script_"+script.operands[env.getPc()] +"(");
				boolean hasArgs = false;
				for (String s : env.getIntStack()) {
					builder.append(s+", ");
					hasArgs = true;
				}
				for (String s : env.getStringStack()) {
					builder.append(s+", ");
					hasArgs = true;
				}
				return hasArgs ? builder.substring(0, builder.length()-2) : builder.toString() + ");";
			case 42:
				env.setStackToGlobalPop(script.operands[env.getPc()]);
				break;
			case 43:
				env.popToGlobalTempStack(script.operands[env.getPc()]);
				break;
			case 102:
				return "deleteAllExtraChilds("+ env.popLastInt()+");";
			case 200:
				//checks if an interface component is valid
				return "if (getInterface("+env.currentInt()+").getComponent("+env.lookNextInt()+") != null) {";
			case 410:
				return "changeGender(" + env.popLastInt() + ");";
			case 21:
				env.debugStacks();
				//script end
				return "return;";
			case 1503:
				env.intStack[env.iscc++] = "SCROLL_POS";
				break;
			case 4001:
				env.iscc -= 2;
				String arg0 = env.intStack[env.iscc];
				String arg1 = env.intStack[env.iscc + 1];
				env.addToIntStack(arg0 + " - "+arg1);
				break;
			case 3408:

				break;
			case 1408:
				return "setDefaultActionTypeScript("+widget+", ScriptArgs("+Misc.objectArrayToString(scriptArgs)+"));";
			case 4003:
				env.iscc -= 2;
				String arg12 = env.intStack[env.iscc + 1];
				String arg2 = env.intStack[env.iscc + 1];
				env.intStack[env.iscc++] = arg12 + "/"+ arg2;
				break;
			case 4106:
				String val = env.popLastInt();
				env.addToStringStack(val);
				break;
			case 1112:
				String message = env.popLastString();
				return "setWidgetText("+widget+", "+message+")";
			default:
				System.out.println("UNHANDLED OPCODE: " +opcode);
				break;
		}
		return "";
	}

}
