package net.openrs.cache;

import harlan.util.FileSystems;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.zip.CRC32;

import net.openrs.cache.ReferenceTable.Entry;
import net.openrs.util.ByteBufferUtils;
import net.openrs.util.crypto.Djb2;
import net.openrs.util.crypto.Whirlpool;

/**
 * The {@link Cache} class provides a unified, high-level API for modifying
 * the cache of a Jagex game.
 * @author Graham
 * @author `Discardedx2
 */
public final class Cache implements Closeable {

	/**
	 * The file store that backs this cache.
	 */
	private final FileStore store;

	/**
	 * Creates a new {@link Cache} backed by the specified {@link FileStore}.
	 * @param store The {@link FileStore} that backs this {@link Cache}.
	 */
	public Cache(FileStore store) {
		this.store = store;
		try {
			checksumTable =	createChecksumTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the number of index files, not including the meta index file.
	 * @return The number of index files.
	 * @throws IOException if an I/O error occurs.
	 */
	public int getTypeCount() throws IOException {
		return store.getTypeCount();
	}

	/**
	 * Gets the number of files of the specified type.
	 * @param type The type.
	 * @return The number of files.
	 * @throws IOException if an I/O error occurs.
	 */
	public int getFileCount(int type) throws IOException {
		if (type == 26)
			type = 255;
		return store.getFileCount(type);
	}

	/**
	 * Gets the {@link FileStore} that backs this {@link Cache}.
	 * @return The underlying file store.
	 */
	public FileStore getStore() {
		return store;
	}

	ChecksumTable checksumTable;
	public ChecksumTable getChecksumTable() {
		return checksumTable;
	}

	/**
	 * Computes the {@link ChecksumTable} for this cache. The checksum table
	 * forms part of the so-called "update keys".
	 * @return The {@link ChecksumTable}.
	 * @throws IOException if an I/O error occurs.
	 */
	public ChecksumTable createChecksumTable() throws IOException {
		/* create the checksum table */
		int size = store.getTypeCount();
		ChecksumTable table = new ChecksumTable(size);

		/* loop through all the reference tables and get their CRC and versions */
		for (int i = 0; i < size; i++) {
			ByteBuffer buf = store.read(255, i);

			int crc = 0;
			int version = 0;
			byte[] whirlpool = new byte[64];

			/*
			 * if there is actually a reference table, calculate the CRC,
			 * version and whirlpool hash
			 */
			if (buf.limit() > 0) { // some indices are not used, is this appropriate?
				ReferenceTable ref = ReferenceTable.decode(Container.decode(buf).getData());
				crc = ByteBufferUtils.getCrcChecksum(buf);
				version = ref.getVersion();
				buf.position(0);
				whirlpool = ByteBufferUtils.getWhirlpoolDigest(buf);
			}

			table.setEntry(i, new ChecksumTable.Entry(crc, version, whirlpool));
		}

		/* return the table */
		return table;
	}

	/**
	 * Reads a file from the cache.
	 * @param type The type of file.
	 * @param file The file id.
	 * @return The file.
	 * @throws IOException if an I/O error occurred.
	 */
	public Container read(int type, int file) throws IOException {
		/* we don't want people reading/manipulating these manually */
		if (type == 255)
			throw new IOException("Reference tables can only be read with the low level FileStore API!");

		/* delegate the call to the file store then decode the container */
		return Container.decode(store.read(type, file));
	}


	/**
	 * Gets a file id from the cache by name
	 * @param type The type of file.
	 * @param name the name of the file
	 * @return The file id.
	 */
	public int getFileId(int type, String name) throws IOException {
		int identifier = Djb2.djb2(name);
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		for (int id = 0; id < table.size(); id++) {
			Entry e = table.getEntry(id);
			if (e.getIdentifier() == identifier)
				return id;
		}
		return -1;
	}


	public int getMemberId(int type, int fileId, String string) throws IOException {
		int identifier = Djb2.djb2(string);
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		Entry e = table.getEntry(fileId);
		for (int i = 0; i < e.capacity(); i++) {
			Entry c = table.getEntry(i);
			if (c.getIdentifier() == identifier)
				return i;
		}
		return -1;
	}
	public SortedMap<Integer, Entry> getEntries(int type) throws IOException {
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table.getEntries();
	}
	public SortedMap<Integer, Entry> getEntries(FileSystems type) throws IOException {
		Container tableContainer = Container.decode(store.read(255, type.getID()));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table.getEntries();
	}
	public Entry getEntry(FileSystems type, int child) throws IOException {
		Container tableContainer = Container.decode(store.read(255, type.getID()));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table.getEntries().get(child);
	}
	public ReferenceTable getReferenceTable(int type) throws IOException {
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table;
	}

	public int getFileCRC(int type, int id, boolean useReferenceTable) throws IOException {
		if (!useReferenceTable) {
			ByteBuffer buffer = store.read(type, id);
			if (buffer.capacity() == 0)
				return 0;
			byte[] bytes = new byte[buffer.limit() - 2]; // last two bytes are the version and shouldn't be included
			buffer.mark();
			try {
				buffer.position(0);
				buffer.get(bytes, 0, bytes.length);
			} finally {
				buffer.reset();
			}

			/* calculate the new CRC checksum */
			CRC32 crc = new CRC32();
			crc.update(bytes, 0, bytes.length);
			return (int)crc.getValue();
		} else {
			SortedMap<Integer, Entry> map = getEntries(type);
			Entry entry = map.get(id);
			return entry.getCrc();
		}
	}

	public boolean isEncodedContainer(ByteBuffer buf) {
		System.out.println("check if container is encoded buf pos: "+buf.position()+" capacity: "+buf.capacity());
		int file_settings = buf.get();
		int length = buf.getInt();
		if (file_settings >= 0 && length >= 0)
			if (file_settings <= 2)
				return true;
		return false;
	}

	public void write(int type, int file, Container container) throws IOException {
		write(type, file, container, false);
	}

	/**
	 * Writes a file to the cache and updates the {@link ReferenceTable} that
	 * it is associated with.
	 * @param type The type of file.
	 * @param file The file id.
	 * @param container The {@link Container} to write.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(int type, int file, Container container, boolean encoded) throws IOException {
		/* we don't want people reading/manipulating these manually */
		if (type == 255)
			throw new IOException("Reference tables can only be modified with the low level FileStore API!");

		/* increment the container's version */
		container.setVersion(container.getVersion() + 1);

		/* decode the reference table for this index */
		ByteBuffer tableBuf = store.read(255, type);
		Container tableContainer = Container.decode(tableBuf);
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		/* grab the bytes we need for the checksum */
		ByteBuffer buffer;
		container.getData().rewind();
		if (encoded)
			buffer = container.getData();
		else
			buffer = container.encode();
		byte[] bytes = new byte[buffer.limit() - 2]; // last two bytes are the version and shouldn't be included
		buffer.rewind();
		buffer.mark();
		try {
			buffer.position(0);
			buffer.get(bytes, 0, bytes.length);
		} finally {
			buffer.reset();
		}

		/* calculate the new CRC checksum */
		CRC32 crc = new CRC32();
		crc.update(bytes, 0, bytes.length);

		/* update the version and checksum for this file */
		ReferenceTable.Entry entry = table.getEntry(file);
		if (entry == null) {
			/* create a new entry for the file */
			entry = new ReferenceTable.Entry();
			table.putEntry(file, entry);
		}
		entry.setVersion(container.getVersion());
		entry.setCrc((int) crc.getValue());

		/* calculate and update the whirlpool digest if we need to */
		if ((table.getFlags() & ReferenceTable.FLAG_WHIRLPOOL) != 0) {
			byte[] whirlpool = Whirlpool.whirlpool(bytes, 0, bytes.length);
			entry.setWhirlpool(whirlpool);
		}

		/* update the reference table version */
		table.setVersion(table.getVersion() + 1);

		/* save the reference table */
		tableContainer = new Container(tableContainer.getType(), table.encode());
		store.write(255, type, tableContainer.encode());

		/* save the file itself */
		store.write(type, file, buffer);
	}

	/**
	 * Reads a file contained in an archive in the cache.
	 * @param type The type of the file.
	 * @param file The archive id.
	 * @param file The file within the archive.
	 * @return The file.
	 * @throws IOException if an I/O error occurred.
	 */
	public ByteBuffer read(int type, int file, int member) throws IOException {
		/* grab the container and the reference table */
		Container container = read(type, file);
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		/* check if the file/member are valid */
		ReferenceTable.Entry entry = table.getEntry(file);
		if (entry == null || member < 0 || member >= entry.capacity())
			throw new FileNotFoundException();

		/* extract the entry from the archive */
		Archive archive = Archive.decode(container.getData(), entry.capacity());
		return archive.getEntry(member);
	}

	public Archive getArchive(int type, int file) throws IOException {
		//container is decoded!
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		/* check if the file/member are valid */
		ReferenceTable.Entry entry = table.getEntry(file);
		if (entry == null)
			return null;
		Archive archive = null;
		try {
		/* extract the entry from the archive */
			/* grab the container and the reference table */
			Container container = read(type, file);
			archive = Archive.decode(container.getData(), entry.capacity());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return archive;
	}

	public int getContainerCount(int type, int file) throws IOException {
		/* grab the container and the reference table */
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		ReferenceTable.Entry entry = table.getEntry(file);
		return entry.capacity();
	}

	/**
	 * Writes a file contained in an archive to the cache.
	 * @param type The type of file.
	 * @param file The id of the archive.
	 * @param member The file within the archive.
	 * @param data The data to write.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(int type, int file, int member, ByteBuffer data) throws IOException {
		/* grab the reference table */
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		/* create a new entry if necessary */
		ReferenceTable.Entry entry = table.getEntry(file);
		int oldArchiveSize = -1;
		if (entry == null) {
			entry = new ReferenceTable.Entry();
			table.putEntry(file, entry);
		} else
			oldArchiveSize = entry.capacity();

		/* add a child entry if one does not exist */
		ReferenceTable.ChildEntry child = entry.getEntry(member);
		if (child == null) {
			child = new ReferenceTable.ChildEntry();
			entry.putEntry(member, child);
		}

		/* extract the current archive into memory so we can modify it */
		Archive archive;
		int containerType, containerVersion;
		if (file < store.getFileCount(type) && oldArchiveSize != -1) {
			Container container = read(type, file);
			containerType = container.getType();
			containerVersion = container.getVersion();
			archive = Archive.decode(container.getData(), oldArchiveSize);
		} else {
			containerType = Container.COMPRESSION_GZIP;
			containerVersion = 1;
			archive = new Archive(member + 1);
		}

		/* expand the archive if it is not large enough */
		if (member >= archive.size()) {
			Archive newArchive = new Archive(member + 1);
			for (int id = 0; id < archive.size(); id++)
				newArchive.putEntry(id, archive.getEntry(id));
			archive = newArchive;
		}

		/* put the member into the archive */
		archive.putEntry(member, data);

		/* create 'dummy' entries */
		for (int id = 0; id < archive.size(); id++)
			if (archive.getEntry(id) == null) {
				entry.putEntry(id, new ReferenceTable.ChildEntry());
				archive.putEntry(id, ByteBuffer.allocate(1));
			}

		/* write the reference table out again */
		tableContainer = new Container(tableContainer.getType(), table.encode());
		store.write(255, type, tableContainer.encode());

		/* and write the archive back to memory */
		Container container = new Container(containerType, archive.encode(), containerVersion);
		write(type, file, container);
	}


	@Override
	public void close() throws IOException {
		store.close();
	}

	public boolean entryExists(int selected_row, int file_id) throws IOException {
		Container tableContainer = Container.decode(store.read(255, selected_row));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table.getEntry(file_id) != null;
	}

	public int findName(ReferenceTable table, String name) {
		if(name == null) {
			return -1;
		}
		int hash = Djb2.djb2(name);
		for(int entry = 0; entry < table.size(); entry++) {
			if(table.getEntry(entry).getIdentifier() == hash) {
				return entry;
			}
		}
		return -1;
	}

	public ByteBuffer read(ReferenceTable table, int index, String name) {
		try {
			int id = findName(table, name);
			if (id == -1) {
				return null;
			}
			return store.read(index, id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getFileCount(int id, int member) {
		Container tableContainer = null;
		try {
			tableContainer = Container.decode(store.read(255, id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
		return table.getEntries().size();
	}
}
