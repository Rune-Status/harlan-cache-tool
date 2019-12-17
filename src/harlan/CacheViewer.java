package harlan;

import com.auradevil.t3d.data.Model;
import harlan.maps.MapBuilder;
import harlan.maps.MapIndexGenerator;
import harlan.maps.MapIndexGenerator.MapIndex;
import harlan.sound.MIDIEncoder;
import harlan.sound.MusicNames;
import harlan.util.FileSystems;
import harlan.util.FileUtility;
import net.openrs.cache.*;
import net.openrs.cache.Container;
import net.openrs.cache.ReferenceTable.ChildEntry;
import net.openrs.cache.ReferenceTable.Entry;
import net.openrs.cache.cs2.CS2Decompiler;
import net.openrs.cache.def.ConfigIndex;
import net.openrs.cache.def.ItemDefinitions;
import net.openrs.cache.sprite.Sprite;
import net.openrs.util.FileChannelUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;

public class CacheViewer extends JFrame {
	private final DefaultListModel<String> listModel = new DefaultListModel<String>();
	/**
	 *
	 */
	private static final long serialVersionUID = -3641521182852980660L;
	private DefaultMutableTreeNode treeNode;
	private DefaultTreeModel treeModel;
	JTree entity_tree = new JTree();
	static CacheViewer cacheViewer;
	public static CacheViewer getCacheViewer() {
		return cacheViewer;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ItemDefinitions.parseDumped();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);
					CacheViewer frame = new CacheViewer();
					cacheViewer = frame;
					UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
					frame.setTitle("Harlan's Cache Manager");
					frame.setVisible(true);
					MusicNames.initMusicMap();
					SaveCacheLocations shutdownHook = new SaveCacheLocations(frame);
					Runtime.getRuntime().addShutdownHook(shutdownHook);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private Cache cache = null;

	private final JPanel contentPane;
	private final JMenuItem load_cache;
	JList<String> fileSystemList = new JList<String>();
	JLabel lblThisIsSprite;
	private final JTable table;
	JDesktopPane imagePopup;
	Model model;
	JPanel sprite_panel;
	JButton btnSaveSprite;
	JLabel lblFileInformation = new JLabel("File Information");

	List<String> previouseCacheLocs;

	public List<String> getPreviousCacheLocs() {
		if (previouseCacheLocs == null) {
			previouseCacheLocs = new ArrayList<String>();
			FileInputStream fstream = null;
			try {
				fstream = new FileInputStream("previous_cache_loads.txt");
			} catch (FileNotFoundException e1) {
				//e1.printStackTrace();
				return null;
			}
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			System.out.println("loading prev cache locals");
			try {
				while ((strLine = br.readLine()) != null)
					if (!previouseCacheLocs.contains(strLine)) {
						System.out.println("adding line: "+strLine);

						previouseCacheLocs.add(strLine);
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Close the input stream
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return previouseCacheLocs;
	}
	public void saveCacheLocs() {
		if (previouseCacheLocs.isEmpty())
			return;
		try {
			//only get last 5 entries
			BufferedWriter br = new BufferedWriter(new FileWriter("previous_cache_loads.txt"));
			for (String s : previouseCacheLocs) {
				br.write(s);
				br.newLine();
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	JButton btnRepackCache = new JButton("Draw");
	/**
	 * Create the frame.
	 */
	public CacheViewer() {
		getPreviousCacheLocs();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 614, 454);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu file_menu = new JMenu("File");
		menuBar.add(file_menu);

		load_cache = new JMenuItem("Load Cache");
		load_cache.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				promptFolderChoose(arg0);
			}
		});
		file_menu.add(load_cache);

		JSeparator separator = new JSeparator();
		file_menu.add(separator);
		if (previouseCacheLocs != null)
			for (final String prevCacheLocs : previouseCacheLocs) {
				JMenuItem loc = new JMenuItem(prevCacheLocs);
				loc.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						setTitle("Harlan's Cache Manager - " + prevCacheLocs);

						openCache(new File(prevCacheLocs));
					}
				});
				file_menu.add(loc);
			}


		JSeparator separator_1 = new JSeparator();
		file_menu.add(separator_1);

		JMenuItem credits_menu_choice = new JMenuItem("Credits");
		file_menu.add(credits_menu_choice);
		credits_menu_choice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(CacheViewer.this,
						"Made by Harlan\nCredits to Steveadoo & the OpenRS team.");
			}
		});

		JMenuItem exit_menu = new JMenuItem("Exit");
		file_menu.add(exit_menu);
		exit_menu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		JMenuItem mntmGziper = new JMenuItem("GZiper");
		mntmGziper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GZiper gz = new GZiper();
			}
		});

		JMenuItem dmpMap = new JMenuItem("Dump Map Index");
		dmpMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MapIndexGenerator.dumpMapIndex(cacheDir, getCache());
			}
		});

		JMenuItem cstmMapImport = new JMenuItem("Custom Map Import");
		cstmMapImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					customMapImport();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});
		JMenuItem missingFiles = new JMenuItem("Fix missing files");
		missingFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					fixMissingFiles();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mnTools.add(cstmMapImport);
		mnTools.add(missingFiles);

		mnTools.add(mntmGziper);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel cache_not_loaded_panel = new JPanel();

		JLabel lblPleaseLoadYour = new JLabel("Please load your cache from File > Load Cache to begin.");
		cache_not_loaded_panel.add(lblPleaseLoadYour);
		lblPleaseLoadYour.setVerticalAlignment(SwingConstants.TOP);
		lblPleaseLoadYour.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel choose_file_system = new JPanel();

		lblThisIsSprite = new JLabel("Select a File System to use");
		lblThisIsSprite.setFont(new Font("Mangal", Font.PLAIN, 18));

		JScrollPane scrollPane = new JScrollPane();

		table = new JTable();
		table.setModel(new DefaultTableModel(
				new Object[][] {
						{"File System ID", null},
						{"Percentage Complete", null},
						{"File Count", null},
						{"CRC Value", null},
						{"Version", null}
				},
				new String[] {
						"", ""
				}
		) {
			Class[] columnTypes = new Class[] {
					String.class, Integer.class
			};
			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(148);
		table.getColumnModel().getColumn(1).setPreferredWidth(103);

		JButton view_contents = new JButton("View Contents");
		view_contents.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (getSelected_row() >= 0) {
					((CardLayout) getContentPane().getLayout()).next(getContentPane());
					try {
						fillTree();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		GroupLayout gl_choose_file_system = new GroupLayout(choose_file_system);
		gl_choose_file_system.setHorizontalGroup(
				gl_choose_file_system.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_choose_file_system.createSequentialGroup()
								.addGap(18)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_choose_file_system.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_choose_file_system.createSequentialGroup()
												.addGap(101)
												.addComponent(view_contents, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_choose_file_system.createSequentialGroup()
												.addGap(21)
												.addComponent(table, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_choose_file_system.createSequentialGroup()
												.addGap(30)
												.addComponent(lblThisIsSprite)))
								.addContainerGap(67, Short.MAX_VALUE))
		);
		gl_choose_file_system.setVerticalGroup(
				gl_choose_file_system.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_choose_file_system.createSequentialGroup()
								.addGroup(gl_choose_file_system.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_choose_file_system.createParallelGroup(Alignment.LEADING, false)
												.addGroup(gl_choose_file_system.createSequentialGroup()
														.addGap(82)
														.addComponent(table, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(view_contents, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_choose_file_system.createSequentialGroup()
														.addGap(22)
														.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_choose_file_system.createSequentialGroup()
												.addContainerGap()
												.addComponent(lblThisIsSprite, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(38, Short.MAX_VALUE))
		);
		fileSystemList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				fileSystemChoice(arg0);
			}
		});
		scrollPane.setViewportView(fileSystemList);
		fileSystemList.setValueIsAdjusting(true);
		fileSystemList.setVisibleRowCount(30);

		fileSystemList.setModel(listModel);


		fileSystemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		choose_file_system.setLayout(gl_choose_file_system);
		contentPane.setLayout(new CardLayout(0, 0));
		contentPane.add(cache_not_loaded_panel, "name_49931987643388");
		contentPane.add(choose_file_system, "file_system");

		final JPanel content_viewing = new JPanel();
		contentPane.add(content_viewing, "name_49947609862360");

		JScrollPane scrollPane_1 = new JScrollPane();

		JButton btnDumpAll = new JButton("Dump All");
		btnDumpAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					dumpAll();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		sprite_panel = new JPanel()
		{
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (selected_row == FileSystems.SPRITES.getID()) {//sprite != null && sprite.images.get(imageId) != null) {
					if (sprite == null) {
						System.out.println("NO Sprite selected!");
						return;
					}
					int imageId = 0;
					if (nested_selected_entity > 0) {
						imageId = nested_selected_entity;
					}
					BufferedImage image = sprite.images.get(imageId);
					if (image == null) {
						System.out.println("Buffered image is nul;l!!");
						return;
					}
					System.out.println("image id: "+ imageId);
					setSize(image.getTileWidth()+5, image.getTileHeight()+5);
					int x = super.getWidth()/2 - image.getTileWidth()/2;
					int y = super.getHeight()/2-image.getTileHeight()/2;
					g.setColor(Color.WHITE);
					g.fillRect(x, y, image.getTileWidth(), image.getTileHeight());
					g.drawImage(image, x, y, null);
				} else if (model != null) {

				} else if (getSelected_row() == FileSystems.MUSIC.getID()) {
					if (selected_entity < 0)
						return;
					ByteBuffer data = null;
					try {
						data = getCache().read(getSelected_row(), selected_entity).getData();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (BufferUnderflowException b) {

					}

					if (data != null) {
						data.position(data.capacity()-3);
						int tracks = data.get();
						int ticks = data.getShort();
						g.drawString("Tracks: "+tracks, 20, 20);
						g.drawString("Ticks: "+ticks, 20, 60);

						if (ticks == 12032) {
							data.position(0);
							byte tracks1 = data.get();
							byte ticks1 = data.get();
							byte byte3 = data.get();
							byte byte4 = data.get();
							byte byte5 = data.get();
							g.drawString("byte1: "+tracks1, 120, 40);
							g.drawString("byte2: "+ticks1, 120, 80);
							g.drawString("byte3: "+byte3, 120, 120);
							g.drawString("byte4: "+byte4, 120, 160);
							g.drawString("byte5: "+byte5, 120, 200);


						}
					}

				} else if (selected_row == FileSystems.MAPS.getID()) {
					{
						super.setBackground(Color.BLACK);
					}
					if (MapBuilder.parsed)
						for (int x = 0; x < 64; x++) {
							for (int y = 0; y < 64; y++)
								if ((MapBuilder.tileSettings[0][x][y] & 1) == 1) {
									g.setColor(Color.YELLOW);
									g.drawRect(x, y, 1, 1);
								}
						}

				} else if  (selected_row == FileSystems.ITEM.getID()) {
					JTable table = new JTable();
					table.setBounds(0, 0, 500, 1000);
					DefaultTableModel model = new DefaultTableModel();
					ItemDefinitions def = ItemDefinitions.forID(selected_entity);
					if (def != null) {
						model.addColumn("Value");
						model.addColumn("Field");
						model.addRow(new Object[] { "Name" , def.name } );
						model.addRow(new Object[] { "Model ID" , def.modelID } );
						model.addRow(new Object[] { "Value" , def.value } );
						for (int i = 0; i < def.actions.length; i++) {
							if (def.actions[i] != null && !def.actions[i].toRealString().equals(""))
								model.addRow(new Object[] { "Action" + i , def.actions[i].toRealString() } );
						}
						table.setModel(model);
						table.setRowHeight(30);
						table.getColumnModel().getColumn(0).setPreferredWidth(50);
						table.getColumnModel().getColumn(1).setPreferredWidth(100);
					} else {
						System.out.println("null def for id: " +selected_entity);
					}


					sprite_panel.add(table);
				}
			}
		};
		sprite_panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

		file_information = new JTable();
		file_information.setRowSelectionAllowed(false);


		btnSaveSprite = new JButton("Save");
		btnSaveSprite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFile(arg0);
			}
		});

		JButton btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (sprite != null) {
					//cache.
				} else
					JOptionPane.showMessageDialog(CacheViewer.this, "Please select an image from the tree menu before doing that.");
			}
		});

		JButton btnSpriteInsert = new JButton("Insert");
		btnSpriteInsert.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (sprite != null) {
					JFileChooser chooser = new JFileChooser();
					chooser.setMultiSelectionEnabled(true);
					if (chooser.showOpenDialog(CacheViewer.this) == JFileChooser.APPROVE_OPTION) {
						try {
							if (chooser.getSelectedFiles().length > 1) {
								for (File f : chooser.getSelectedFiles()) {
									sprite.images.add(ImageIO.read(f));
								}
							} else {
								sprite.images.add(ImageIO.read(chooser.getSelectedFile()));
							}
							sprite.convertBufferedImages();
							sprite.writeToCache(getCache());
							CacheViewer.this.fillTree();
							sprite_panel.repaint();

							JOptionPane.showMessageDialog(CacheViewer.this, "File inserted sucesfully!");

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else
					JOptionPane.showMessageDialog(CacheViewer.this, "Please select an image from the tree menu before doing that.");
			}
		});
		JButton btnReplace = new JButton("Replace");
		btnReplace.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(CacheViewer.this) == JFileChooser.APPROVE_OPTION)
					try {
						if (FileSystems.forID(getSelected_row()).equals(FileSystems.SPRITES)) {
							BufferedImage image = ImageIO.read(chooser.getSelectedFile());
							if (nested_selected_entity < 0)
								nested_selected_entity = 0;
							sprite.images.set(nested_selected_entity, image);
							sprite.convertBufferedImages();
							sprite_panel.repaint();
							sprite.writeToCache(getCache());
							CacheViewer.this.fillTree();
							JOptionPane.showMessageDialog(CacheViewer.this, "File replaced sucesfully!");
						} else {
							int oldSize = getCache().getStore().read(getSelected_row(), selected_entity).capacity();

							ByteBuffer buf = FileUtility.readFully(chooser.getSelectedFile());
							boolean sucess = getCache().getStore().write(getSelected_row(), selected_entity, buf);
							if (sucess)
								JOptionPane.showMessageDialog(CacheViewer.this, "File replaced sucesfully!");
							else
								JOptionPane.showMessageDialog(CacheViewer.this, "File replacement failed!");

							int newSize = getCache().getStore().read(getSelected_row(), selected_entity).capacity();
							System.out.println("old size: "+oldSize+" new Size:" +newSize);

						}
					} catch (Exception e3) {
						e3.printStackTrace();
					}
			}
		});

		JButton btnInsertNew = new JButton("Insert");
		btnInsertNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				insert();
			}
		});


		entity_data = new JTable();


		JLabel lblSpriteData = new JLabel("Entity Data");
		btnRepackCache.setVisible(false);

		JButton btnGoBack = new JButton("Go Back");
		btnGoBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				((java.awt.CardLayout) getContentPane().getLayout()).previous(getContentPane());
				setSelected_row(0);
				sprite = null;
				selected_entity = 0;
			}
		});

		GroupLayout gl_content_viewing = new GroupLayout(content_viewing);
		gl_content_viewing.setHorizontalGroup(
				gl_content_viewing.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_content_viewing.createSequentialGroup()
								.addGroup(gl_content_viewing.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addContainerGap()
												.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(gl_content_viewing.createParallelGroup(Alignment.TRAILING, false)
														.addGroup(gl_content_viewing.createSequentialGroup()
																.addComponent(file_information, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
																.addGroup(gl_content_viewing.createParallelGroup(Alignment.LEADING)
																		.addGroup(gl_content_viewing.createSequentialGroup()
																				.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
																				.addComponent(lblSpriteData, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
																				.addGap(18))
																		.addGroup(gl_content_viewing.createSequentialGroup()
																				.addGap(6)
																				.addComponent(entity_data, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))))
														.addGroup(gl_content_viewing.createParallelGroup(Alignment.LEADING)
																.addComponent(sprite_panel, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
																.addGroup(gl_content_viewing.createSequentialGroup()
																		.addComponent(btnSaveSprite)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(btnRemove)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(btnReplace)
																		.addGap(6)
																		.addComponent(btnSpriteInsert)
																		.addGap(6)
																		.addComponent(btnRepackCache)))))
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addGap(34)
												.addComponent(btnInsertNew)
												.addGap(18)
												.addComponent(btnDumpAll)
												.addGap(94)
												.addComponent(lblFileInformation))
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addContainerGap()
												.addComponent(btnGoBack, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_content_viewing.setVerticalGroup(
				gl_content_viewing.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_content_viewing.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_content_viewing.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addGroup(gl_content_viewing.createParallelGroup(Alignment.BASELINE)
														.addComponent(btnDumpAll)
														.addComponent(btnInsertNew))
												.addGap(7))
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addGroup(gl_content_viewing.createParallelGroup(Alignment.BASELINE)
														.addComponent(lblFileInformation)
														.addComponent(lblSpriteData))
												.addPreferredGap(ComponentPlacement.RELATED)))
								.addGroup(gl_content_viewing.createParallelGroup(Alignment.BASELINE)
										.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 332, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_content_viewing.createSequentialGroup()
												.addGroup(gl_content_viewing.createParallelGroup(Alignment.LEADING)
														.addGroup(gl_content_viewing.createSequentialGroup()
																.addGap(91)
																.addGroup(gl_content_viewing.createParallelGroup(Alignment.BASELINE)
																		.addComponent(btnSaveSprite)
																		.addComponent(btnRemove)
																		.addComponent(btnReplace)
																		.addComponent(btnSpriteInsert)
																		.addComponent(btnRepackCache)))
														.addGroup(gl_content_viewing.createParallelGroup(Alignment.BASELINE)
																.addComponent(file_information, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
																.addComponent(entity_data, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)))
												.addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
												.addComponent(sprite_panel, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnGoBack, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
		);
		GroupLayout gl_sprite_panel = new GroupLayout(sprite_panel);
		gl_sprite_panel.setHorizontalGroup(
				gl_sprite_panel.createParallelGroup(Alignment.LEADING)
						.addGap(0, 326, Short.MAX_VALUE)
		);
		gl_sprite_panel.setVerticalGroup(
				gl_sprite_panel.createParallelGroup(Alignment.LEADING)
						.addGap(0, 199, Short.MAX_VALUE)
		);
		sprite_panel.setLayout(gl_sprite_panel);
		entity_tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				treeSelected(arg0);
				content_viewing.repaint();
			}
		});

		entity_tree.setShowsRootHandles(true);
		scrollPane_1.setViewportView(entity_tree);
		content_viewing.setLayout(gl_content_viewing);

	}
	void dumpAll() throws IOException {
		FileSystems fs = FileSystems.forID(getSelected_row());
		String dir = cacheDir.getPath()+"/"+(fs != null ? fs.toString() : getSelected_row()+"_index");
		File dumpDir = new File(dir);
		dumpDir.mkdir();

		int filesToDump = 0;
		try {
			filesToDump = getCache().getFileCount(getSelected_row());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[] options = {"Yes",
				"No",};
		boolean dataEncoded = JOptionPane.showOptionDialog(this,
				"Would you like to dump the data encoded?",
				"Dump Files",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				null) == 0;
		int sucess_dumps = 0;
		for (int archiveIndex = 0; archiveIndex < filesToDump; archiveIndex++) {
			String fileName = dir + "/" + archiveIndex + (dataEncoded ? ".gz" : ".dat");
			if (fs.equals(FileSystems.CONFIG)) {
				fileName = dir + "/" + ConfigIndex.get(archiveIndex) + (dataEncoded ? ".gz" : ".dat");
			}
			File file = new File(fileName);

			if (fs.equals(FileSystems.SPRITES)) {
				Sprite sprite = Sprite.get(getCache(), archiveIndex);
				if (sprite == null)
					continue;
				File des = new File(dir + "/" + archiveIndex + ".png");
				System.out.println("making image: " + des.getName() + " path: " + des.getAbsolutePath());
				for (BufferedImage image : sprite.images) {
					if (image == null)
						continue;
					try {
						ImageIO.write(image, "PNG", des);
						sucess_dumps++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {
				ByteBuffer buf;
				try {

					if (dataEncoded) {
						buf = getCache().getStore().read(getSelected_row(), archiveIndex);
						if (buf.capacity() > 0) {
							FileUtility.writeFully(file, buf);
							System.out.println("making file: " + file.getName() + " path: " + file.getAbsolutePath() + "buf capacity: " + buf.capacity());
							sucess_dumps++;
						}
					} else {
						Archive a = getCache().getArchive(getSelected_row(), archiveIndex);
						if (a !=  null) {
							ByteArrayOutputStream stream = a.asOutputStream();
							FileOutputStream outputStream = new FileOutputStream(fileName);
							stream.writeTo(outputStream);
							sucess_dumps++;
							stream.close();
							stream.flush();
							outputStream.close();
							System.gc();
							System.out.println("opened and dumped archive: " + archiveIndex);
						} else
						System.out.println("Failed to open archive: " + archiveIndex);
					}
					System.gc();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Failed to dump file: " + archiveIndex + ".\n Problem: " + e.getMessage());
					e.printStackTrace();
				}

			}
		}
		JOptionPane.showMessageDialog(this, sucess_dumps + " complete files have been sucesfully dumped!");
	}


	private void treeSelected(TreeSelectionEvent evt) {
		FileSystems fs = FileSystems.forID(getSelected_row());
		if (entity_tree.getSelectionPath() != null)
			try {
				int child = -1;
				int childs_child = -1;
				if (entity_tree.getSelectionPath().getPathCount() == 3) {
					Object child2Obj = ((DefaultMutableTreeNode) entity_tree.getSelectionPath().getLastPathComponent()).getUserObject();
					Object child1Obj = ((DefaultMutableTreeNode)entity_tree.getSelectionPath().getPath()[1]).getUserObject();

					//childs child
					if (child2Obj instanceof String && ((String)child2Obj).contains(":")) {
						childs_child = Integer.parseInt(((String)child2Obj).substring(0, ((String)child2Obj).indexOf(":")).trim());
					} else
						childs_child = (Integer) ((DefaultMutableTreeNode) entity_tree.getSelectionPath().getLastPathComponent()).getUserObject();
					if (fs.equals(FileSystems.CONFIG))
						child = ConfigIndex.forValue((String)child1Obj);
					else
						child = (Integer) child1Obj;
				} else if (entity_tree.getSelectionPath().getPathCount() == 2) {
					//base child
					Object obj = ((DefaultMutableTreeNode) entity_tree.getSelectionPath().getLastPathComponent()).getUserObject();
					if (fs.equals(FileSystems.CONFIG)) {
						child = ConfigIndex.forValue((String)obj);
					} else {
						if (obj instanceof String && ((String)obj).contains(":"))
							child = Integer.parseInt(((String)obj).substring(0, ((String)obj).indexOf(":")).trim());
						else
							child = (Integer) ((DefaultMutableTreeNode) entity_tree.getSelectionPath().getLastPathComponent()).getUserObject();
					}
				}
				System.out.println("child: "+child+" childs_child: "+childs_child);
				if (child >= 0) {
					selected_entity = child;
					nested_selected_entity = childs_child;
					if (fs.equals(FileSystems.SPRITES)) {

						sprite = Sprite.get(getCache(), selected_entity);
						if (nested_selected_entity == -1)
							nested_selected_entity = 0;

						System.out.println("set sprite! id: "+selected_entity+" null ? "+(sprite == null));
					} else if (fs.equals(FileSystems.CS2)) {
						CS2Decompiler.decompileScriptInfo(cache, selected_entity);
					} else if (fs.equals(FileSystems.ITEM)) {
						if (childs_child < 0)
							childs_child = 0;
						ItemDefinitions.forID(child, childs_child);
					}
					int file_size;

					try {
						if (fs.equals(FileSystems.ITEM)) {
							file_size = getCache().read(getSelected_row(), selected_entity >>> 8, selected_entity & 0xFF).capacity();
						} else
							file_size = getCache().getStore().read(getSelected_row(), selected_entity).capacity();
					} catch (FileNotFoundException e) {
						selected_entity = 0;
						JOptionPane.showMessageDialog(this, "File is missing!");
						return;
					}
					boolean empty_file = file_size == 0;
					if (empty_file) {
						lblFileInformation.setText("File Is Empty!");
						btnSaveSprite.setEnabled(false);
					} else {
						lblFileInformation.setText("File Information");
						btnSaveSprite.setEnabled(true);
					}

					SortedMap<Integer, Entry> map = getCache().getEntries(getSelected_row());
					Entry entry = null;
					if (fs.equals(FileSystems.ITEM))
						entry = map.get(selected_entity >>> 8);
					else
						entry = map.get(selected_entity);
					DefaultTableModel defaultModel = new DefaultTableModel(
							new Object[][] {
									{"Identifier", entry.getIdentifier()},
									{"Version", entry.getVersion()},
									{"CRC", entry.getCrc()},
									{"File Length", file_size },
							},
							new String[] {
									"", ""
							}) {
						private static final long serialVersionUID = 1L;

						@Override
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};

					file_information.setModel(defaultModel);
					if (fs.equals(FileSystems.SPRITES)) {
						entity_data.setModel(new DefaultTableModel(
								new Object[][] {
										{"Max Width", sprite.getMaxWidth()},
										{"Max Height", sprite.getMaxHeight()},
										{"Children", sprite.images.size()},
										{"Type", sprite.readType},
										{"Pallet", sprite.childrenSprites[nested_selected_entity].pallet.length},


								},
								new String[] {
										"", ""
								}
						) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isCellEditable(int row, int column) {
								return false;
							}
						});


					} else if (fs.equals(FileSystems.MUSIC)) {
						ReferenceTable table = getCache().getReferenceTable(getSelected_row());
						System.out.println("song idenfier: "+entry.getIdentifier()+" reference table id: "+table.getArchiveIdentifier().getFileID(entry.getIdentifier()));
						String songName = "unknown";
						int id = table.getArchiveIdentifier().getFileID(entry.getIdentifier());
						if (MusicNames.musicIds.containsKey(id))
							songName = MusicNames.musicIds.get(id);
						System.out.println("song name: "+songName);

						ByteBuffer data = getCache().getStore().read(getSelected_row(), selected_entity);
						int comprssion = 0;
						int length = 0;
						if (data.capacity() > 0) {
							comprssion = data.get();
							length = data.getInt();
						}

						entity_data.setModel(new DefaultTableModel(
								new Object[][] {
										{"Name", songName},
										{"CRC", getCache().getFileCRC(getSelected_row(), selected_entity, false)},
										{"Compression", comprssion},
								},
								new String[] {
										"", ""
								}
						) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isCellEditable(int row, int column) {
								return false;
							}
						});
					} if (fs.equals(FileSystems.ITEM)) {
						entity_data.setModel(new DefaultTableModel(
								new Object[][] {
										{ "File ID", selected_entity >>> 8 },
										{ "Child ID", selected_entity & 0xFF },

								},
								new String[] {
										"", ""
								}
						) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isCellEditable(int row, int column) {
								return false;
							}
						});
					} else {
						ByteBuffer data = getCache().getStore().read(getSelected_row(), selected_entity);
						int comprssion = 0;
						int length = 0;
						if (data.capacity() > 0) {
							comprssion = data.get();
							length = data.getInt();
						}

						entity_data.setModel(new DefaultTableModel(
								new Object[][] {
										{"CRC", getCache().getFileCRC(getSelected_row(), selected_entity, false)},
										{"Compression", comprssion},
										{"length", length},
								},
								new String[] {
										"", ""
								}
						) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isCellEditable(int row, int column) {
								return false;
							}
						});
					}

					entity_data.getColumnModel().getColumn(0).setPreferredWidth(59);
					entity_data.getColumnModel().getColumn(1).setPreferredWidth(95);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	Sprite sprite = null;
	public int selected_entity = -1;
	int nested_selected_entity = -1;
	Map<FileSystems, Double> cachePercentageComplete = new HashMap<FileSystems, Double>();
	private int selected_row = -1;
	void fileSystemChoice(ListSelectionEvent e) {
		setSelected_row(((JList<String>) e.getSource()).getSelectedIndex());
		if (getSelected_row() < 0)
			return;
		FileSystems fs = FileSystems.forID(getSelected_row());
		if (fs != null)
			lblThisIsSprite.setText(fs.toString());
		else
			lblThisIsSprite.setText("Unidentified Index: "+getSelected_row());
		double succesfullCount = 0;
		double completion = 0;
		int fileCount = 0;
		try {
			fileCount = getCache().getFileCount(getSelected_row());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (cachePercentageComplete.get(fs) == null) {
			for (int subi = 0; subi < fileCount; subi++)
				try {
					if (getCache().read(getSelected_row(), subi).getData().capacity() != 0)
						succesfullCount++;
				} catch (Exception exp) {
				}
			completion = succesfullCount / fileCount * 100;
			cachePercentageComplete.put(fs, completion);
		} else
			completion = cachePercentageComplete.get(fs);
		DefaultTableModel model = new DefaultTableModel(
				new Object[][] {
						{"File System ID", getSelected_row()},
						{"Percentage Complete", completion},
						{"File Count", fileCount},
						{"CRC Value", getCache().getChecksumTable().getEntry(getSelected_row()).getCrc() },
						{"Version", getCache().getChecksumTable().getEntry(getSelected_row()).getVersion()},

				},
				new String[] {
						"", ""
				}
		) {
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class[] {
					String.class, Integer.class
			};
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		if (fs.equals(FileSystems.ITEM)) {
			model.addRow(new Object[] { "Item Count", ItemDefinitions.getItemCount()});
			ItemDefinitions.preloadItems(false);
		}
		table.setModel(model);
	}
	void writeList() {
		for (int i = 0; i < getCache().getStore().getIndexChannels().length; i++) {
			FileSystems system = FileSystems.forID(i);
			if (system != null)
				listModel.addElement(system.toString());
			else
				listModel.addElement("Unidentified Index: "+i);
		}
		listModel.addElement("Version Table");
	}
	void fillTree() throws IOException {
		FileSystems f = FileSystems.forID(getSelected_row());
		if (f != null)
			treeNode = new DefaultMutableTreeNode(f.toString());
		else
			treeNode = new DefaultMutableTreeNode("index_"+getSelected_row());
		treeModel = new DefaultTreeModel(treeNode);
		for (Map.Entry<Integer, Entry> entry : getCache().getEntries(getSelected_row()).entrySet()) {
			if (f.equals(FileSystems.ITEM)) {
				//for items we're going to list the item name with the id, so we do something different
				if (entry.getValue().getChildEntries() != null) {
					for (Map.Entry<Integer, ChildEntry>  child : entry.getValue().getChildEntries().entrySet()) {
						ItemDefinitions def = ItemDefinitions.forID(entry.getKey(), child.getKey());
						if (def != null) {
							String item = def.id + " : " +def.name.toRealString();
							DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(item);
							treeNode.add(fileNode);

						}
					}
				}
			} else {

				DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(entry.getKey());
				if (f.equals(FileSystems.CONFIG)) {
					String selectedConfig = ConfigIndex.get(entry.getKey());
					if (selectedConfig != null) {
						fileNode = new DefaultMutableTreeNode(selectedConfig);
					}
					if (selectedConfig != null && selectedConfig.equals("items")) {
						ItemDefinitions.preloadItems(true);
						for (ItemDefinitions def : ItemDefinitions.defs.values()) {
							fileNode.add(new DefaultMutableTreeNode(def.id + " : " +def.name.toRealString()));
						}
						treeNode.add(fileNode);
						continue;
					}
				}
				if (f.equals(FileSystems.SPRITES)) {
					//sprite children are handled differently then the regular children
					Sprite s = Sprite.get(cache, entry.getKey());
					for (int i = 1; i < s.images.size(); i++) {
						fileNode.add(new DefaultMutableTreeNode(i));
					}
				} else if (entry.getValue().getChildEntries() != null && entry.getValue().childSize() > 1) {
					for (Map.Entry<Integer, ChildEntry>  child : entry.getValue().getChildEntries().entrySet()) {
						fileNode.add(new DefaultMutableTreeNode(child.getKey()));
					}
				}
				treeNode.add(fileNode);
			}
		}
		treeModel.reload(treeNode);
		entity_tree.setModel(treeModel);
	}
	File cacheDir = null;
	private JTable file_information;
	private JTable entity_data;
	protected void openCache(File file) {
		if (file.isDirectory() && file.exists())
			try {
				if (!previouseCacheLocs.contains(file.getPath()))
					previouseCacheLocs.add(file.getPath());
				cacheDir = file;
				setCache(new Cache(FileStore.open(file)));
				listModel.clear();
				writeList();
				cachePercentageComplete.clear();
				((java.awt.CardLayout) getContentPane().getLayout()).show(getContentPane(), "file_system");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Bad folder choice! Please select a folder containg your main_file_cache files!");
			}
		else
			JOptionPane.showMessageDialog(this, "Make sure the cache you select is a directory and not a file, and exists as well");

	}

	public void saveFile(ActionEvent arg0) {
		if (selected_entity > -1) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file.isDirectory())
					try {
						if (FileSystems.forID(getSelected_row()).equals(FileSystems.SPRITES)) {
							File newFile = new File(file.getAbsolutePath() + "/sprite" +sprite.id+"_id"+0+".png");
							if (!newFile.exists())
								newFile.createNewFile();
							ImageIO.write(sprite.images.get(0), "PNG", newFile);
							JOptionPane.showMessageDialog(this, "Image saved sucesfully!");
						} else {
							File newFile = new File(file.getAbsolutePath() + "/"+FileSystems.forID(getSelected_row()).toString()+"_"+ selected_entity+".dat");
							if (!newFile.exists())
								newFile.createNewFile();
							FileOutputStream in = new FileOutputStream(newFile);
							FileChannel channel = in.getChannel();
							ByteBuffer buf = getCache().getStore().read(getSelected_row(), selected_entity);
							FileChannelUtils.writeFully(channel, buf);
							in.close();
							JOptionPane.showMessageDialog(this, "File saved sucesfully!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				else
					JOptionPane.showMessageDialog(this, "Make sure you select a folder.");
			}
		} else
			JOptionPane.showMessageDialog(this, "Please select a file from the tree menu before doing that.");
	}
	/**
	 * Custom import method will import maps from a directory and insert them into the cache.
	 * The criteria for them going is custom,
	 * @throws IOException
	 */
	private void customMapImport() throws IOException {
		int[] fromMaps = new int[] {628,
				632,
				636,
				270,
				274,
				278,
				394,
				398,
				404};
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();

			Map<Integer, MapIndex> indicies = MapIndexGenerator.loadMapIndicies(getCache());
			for (MapIndex index : indicies.values()) {
				if (index.landscapeFile > 0) {
					File replacer = new File(folder.getAbsoluteFile() + "/" + index.landscapeFile + ".dat");
					ByteBuffer buf = FileUtility.readFully(replacer);
					System.out.println("replacing file: "+index.landscapeFile);
					getCache().write(FileSystems.MAPS.getID(), index.landscapeFile, new Container(2, buf, 0));
				}

				for (int fromMap : fromMaps) {
					if (fromMap == index.mapFile && index.mapFile > 0) {
						File replacer = new File(folder.getAbsoluteFile() + "/" + index.mapFile + ".dat");
						ByteBuffer buf = FileUtility.readFully(replacer);
						getCache().write(FileSystems.MAPS.getID(), index.mapFile, new Container(2, buf, 0));
						System.out.println("replaced file: "+index.mapFile);
					}
				}
			}
		}
	}

	private void fixMissingFiles() throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int count = 0;
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			Object[] options = {"Yes",
					"No",};
			boolean encoded = JOptionPane.showOptionDialog(this,
					"Are you importing encoded data?",
					"Fix Missing Files",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					null) == 0;
			File folder = chooser.getSelectedFile();
			for (Map.Entry<Integer, Entry> entry : getCache().getEntries(getSelected_row()).entrySet()) {
				int file_id = entry.getKey();
				ByteBuffer cur_file = null;
				File replacer = new File(folder.getAbsoluteFile() + "/" + file_id + ".dat");
				if (!getCache().getStore().isInvalidIndex(getSelected_row(), file_id)) {
					cur_file = getCache().getStore().read(getSelected_row(), file_id);
				}
				if ((cur_file == null || cur_file.capacity() <= 0) && replacer.exists()) {
					System.out.println("file: "+file_id+" is empty! attempting to replace.");
					ByteBuffer buf = FileUtility.readFully(replacer);
					getCache().write(getSelected_row(), file_id, new Container(2, buf, 0), encoded);
					System.out.println("replaced file: "+file_id);
					count++;
				}
			}
		}
		JOptionPane.showMessageDialog(this, "Sucesfully replaced " + count + " files!");
	}
	public void promptFolderChoose(ActionEvent arg0) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			setTitle("Harlan's Cache Manager - " + selected.getAbsolutePath());
			openCache(selected);
		}
	}
	public void insert() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			try {
				boolean replacingAll = false;
				boolean replacingNone = false;
				Object[] options2 = {"Yes",
						"No",
						"I don't know"};
				boolean encoded = JOptionPane.showOptionDialog(this,
						"Are these files encoded?",
						"Insert File",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options2,
						null) == 0;
				for (File file : chooser.getSelectedFiles()) {
					int file_id = Integer.parseInt(file.getName().substring(0, file.getName().indexOf(".")));
					String extension = file.getName().substring(file.getName().indexOf(".")+1);
					System.out.println("attempting to add file: "+file_id+ " extension : "+extension);
					ByteBuffer cur_file = null;
					if (!replacingAll)

						try {
							cur_file = getCache().getStore().read(getSelected_row(), file_id);
						} catch (FileNotFoundException e) {

						} catch (java.io.EOFException e) {

						}
					if (cur_file != null && cur_file.capacity() > 0 && !replacingAll) {
						if (replacingNone) {
							System.out.println("replacing none! continuing");
							continue;
						}
						Object[] options = {"Yes",
								"No",
								"Replace all", "Replace None"};
						int response = JOptionPane.showOptionDialog(this,
								"A file already exists in the index "+file_id+" \n would you like to replace it?",
								"Insert File",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								null);
						System.out.println("responsse = "+response);
						if (response == 2)
							replacingAll = true;
						else if (response == 1)
							continue;
						else if (response == 3) {
							replacingNone = true;
							continue;
						}
					}

					ByteBuffer buf = null;
					if (extension.equalsIgnoreCase("mid")) {
						buf = MIDIEncoder.encode(file);
						encoded = false;

					} else {
						buf = FileUtility.readFully(file);
					}
					System.out.println("read file: "+file.getName()+" size: "+buf.capacity());

					if (encoded) {
						System.out.println("file is encoded!");
						int compression = encoded ? 2 : 0;
						if (!encoded) {
							buf.rewind();
							compression = buf.get();
							buf.rewind();
						}
						getCache().write(getSelected_row(), file_id, new Container(compression, buf, 0), true);
					} else {
						System.out.println("file is not encoded!");
						getCache().write(getSelected_row(), file_id, new Container(2, buf, 0));
					}
					System.out.println("sucesfully inserted file: "+file_id+" into file system: "+FileSystems.forID(getSelected_row()).toString());
				}//S33713  33732
			} catch (Exception e) {
				e.printStackTrace();
				//JOptionPane.showMessageDialog(this, "Error loading image, please make sure it's an image file.");
			}
		JOptionPane.showMessageDialog(this, "Done!");
		cachePercentageComplete.clear();

	}
	public Cache getCache() {
		return cache;
	}
	public void setCache(Cache cache) {
		this.cache = cache;
	}
	public int getSelected_row() {
		return selected_row;
	}
	public void setSelected_row(int selected_row) {
		this.selected_row = selected_row;
	}
}
