package harlan.util;


public enum FileSystems {
	VERSION_TABLE(255),
	SKELETON(0),
	SKIN(1),
	CONFIG(2),
	INTERFACES(3),
	SOUND_EFFECT(4),
	MAPS(5),
	MUSIC(6),
	MODEL(7),
	SPRITES(8),
	TEXTURES(9),
	HUFFMAN(10),
	MUSIC_2(11),
	CS2(12),
	FONTS(13),
	INSTRUMENTS(14),
	SOUND_EFFECTS_3(15),
	OBJECTS(16),
	CS_SETTINGS(17),
	NPC(18),
	ITEM(19),
	ANIMATION(20),
	GRAPHIC(21),
	SCRIPT_CONFIG(22),
	WORLD_MAP(23),
	QUICK_CHAT_MESSAGES(24),
	QUICK_CHAT_MENUS(25),
	TEXTURES_2(26),
	MAP_EFFECTS(27),
	FONTS_2(28),
	NATIVE_LIBRARIES(30),
	SHADERS(31),
	P11_FONTS_IMAGES(32),
	GAME_TIPS(33),
	P11_FONTS2_IMAGES(34),
	THEORA(35),
	VORBIS(36);
	FileSystems(int id) {
		this.id = id;
	}
	int id;
	public int getID() {
		return id;
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(name().toLowerCase().replaceAll("_", " "));
		s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
		return s.toString();
	}
	public static FileSystems forID(int id) {
		for (FileSystems a: FileSystems.values())
			if (a.getID() == id)
				return a;
		if (id == 26 || id == 255)
			return VERSION_TABLE;
		return null;
	}
}