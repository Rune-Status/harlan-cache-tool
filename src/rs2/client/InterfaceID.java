package rs2.client;

public class InterfaceID {

	InterfaceID(int interfaceId, int childId) {
		this.interfaceId = interfaceId;
		this.childId = childId;
	}
	InterfaceID(int interfaceId, int childId, int interfaceUID) {
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.interfaceUID = interfaceUID;
	}
	int interfaceUID;
	int interfaceId;
	int childId;
	public int getUID() {
		return interfaceUID;
	}
	public int getId() {
		return interfaceId;
	}
	public int getChildId() {
		return childId;
	}
	@Override
	public String toString() {
		return "interface["+interfaceId+"] child: "+childId+" id: "+interfaceUID;
	}

	public static InterfaceID decode(int interfaceUID) {
		int interfaceId = interfaceUID >> 16;
		int childId = interfaceUID & 0xffff;
		return new InterfaceID(interfaceId, childId, interfaceId);
	}
	public static InterfaceID encode(int interfaceId, int childId) {
		int interfaceUID =  interfaceId << 16 | childId;
		return new InterfaceID(interfaceId, childId, interfaceUID);
	}
}
