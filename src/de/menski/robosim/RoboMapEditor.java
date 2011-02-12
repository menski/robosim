package de.menski.robosim;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RoboMapEditor implements PaintListener, KeyListener {

	private Display display;
	private Shell shell;
	private Canvas canvas;
	private RoboMap map;
	private Label statusbarLabel;
	
	public RoboMapEditor(RoboMap map) {
		this.map = map;
		display = new Display ();
		shell = new Shell(display);
		shell.setSize(640, 480);
		shell.setLayout(new FormLayout());
		addStatusbar();
		addCanvas();
		addMenubar();
		update();
	}
	
	public void open() {
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		Rectangle rect = canvas.getClientArea();
		int size = Math.min(Math.min(rect.width/(map.getXSize()+2), rect.height/(map.getYSize()+2)),90);

		for (int y = 0; y < map.getYSize(); ++y) {
			for (int x = 0; x < map.getSize(y); ++x) {
				drawField(e.gc, x, y, size, map.getField(y, x));
			}
		}
		drawCursor(e.gc, size);
		drawRobot(e.gc, size);
	}
	
	private void drawField(GC gc, int x, int y, int size, char type) {
		Rectangle field = new Rectangle((x+1)*size, (y+1)*size, size, size);
		switch (type) {
		case 'w':
			drawWall(gc, field);
			break;
		case 'f':
			drawFloor(gc, field);
			break;
		case 'o':
			drawObstacle(gc, field);
			break;
		default:
			break;
		}
		resetColors(gc);
		gc.drawRectangle(field);		
	}
	
	private void drawCursor(GC gc, int size) {
		gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.setLineWidth(3);
		for (int y = map.getMinMarkY(); y <= map.getMaxMarkY(); ++y) {
			for (int x = map.getMinMarkX(); x <= map.getMaxMarkX(); ++x) {
				gc.drawRectangle((x+1)*size, (y+1)*size, size, size);
			}
		}
		gc.setLineWidth(0);
		resetColors(gc);
	}
	
	private void resetColors(GC gc) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	}
	
	private void drawWall(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(field);
//		gc.drawLine(field.x, field.y+(int)(0.3*field.height), field.x+(int)(0.3*field.width), field.y);
//		gc.drawLine(field.x, field.y+(int)(0.6*field.height), field.x+(int)(0.6*field.width), field.y);
//		gc.drawLine(field.x, field.y+field.height, field.x+field.width, field.y);
//		gc.drawLine(field.x+(int)(0.3*field.width), field.y+field.height, field.x+field.width, field.y+(int)(0.3*field.height));
//		gc.drawLine(field.x+(int)(0.6*field.width), field.y+field.height, field.x+field.width, field.y+(int)(0.6*field.height));
		gc.drawLine(field.x, field.y+(int)(field.height/4), field.x+field.width, field.y+(int)(field.height/4));
		gc.drawLine(field.x+(int)(field.width/3), field.y, field.x+(int)(field.width/3), field.y+(int)(field.height/4));
		gc.drawLine(field.x+(int)(field.width*2/3), field.y, field.x+(int)(field.width*2/3), field.y+(int)(field.height/4));
		gc.drawLine(field.x, field.y+(int)(field.height*2/4), field.x+field.width, field.y+(int)(field.height*2/4));
		gc.drawLine(field.x+(int)(field.width/6), field.y+(int)(field.height/4), field.x+(int)(field.width/6), field.y+(int)(field.height*2/4));
		gc.drawLine(field.x+(int)(field.width*3/6), field.y+(int)(field.height/4), field.x+(int)(field.width*3/6), field.y+(int)(field.height*2/4));
		gc.drawLine(field.x+(int)(field.width*5/6), field.y+(int)(field.height/4), field.x+(int)(field.width*5/6), field.y+(int)(field.height*2/4));
		gc.drawLine(field.x, field.y+(int)(field.height*3/4), field.x+field.width, field.y+(int)(field.height*3/4));
		gc.drawLine(field.x+(int)(field.width/3), field.y+(int)(field.height*2/4), field.x+(int)(field.width/3), field.y+(int)(field.height*3/4));
		gc.drawLine(field.x+(int)(field.width*2/3), field.y+(int)(field.height*2/4), field.x+(int)(field.width*2/3), field.y+(int)(field.height*3/4));
		gc.drawLine(field.x+(int)(field.width/6), field.y+(int)(field.height*3/4), field.x+(int)(field.width/6), field.y+field.height);
		gc.drawLine(field.x+(int)(field.width*3/6), field.y+(int)(field.height*3/4), field.x+(int)(field.width*3/6), field.y+field.height);
		gc.drawLine(field.x+(int)(field.width*5/6), field.y+(int)(field.height*3/4), field.x+(int)(field.width*5/6), field.y+field.height);
	}
	
	private void drawFloor(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		gc.fillRectangle(field);
		gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawLine(field.x+(int)(0.5*field.width), field.y, field.x+(int)(0.5*field.width), field.y+field.height);
		gc.drawLine(field.x, field.y+(int)(0.5*field.height), field.x+field.width, field.y+(int)(0.5*field.height));
	}
	
	private void drawObstacle(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		gc.fillRectangle(field);
		gc.drawLine(field.x, field.y, field.x+field.width, field.y+field.height);
		gc.drawLine(field.x+field.width, field.y, field.x, field.y+field.height);
	}
	
	private void drawRobot(GC gc, int size) {
		if (map.isRobotSet()) {
			int x = map.getRobot().x;
			int y = map.getRobot().y;
			/* Tyres */
			gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			switch (map.getRotation()) {
			case 270:
				gc.fillRectangle(x*size+(int)(1.3*size), y*size+(int)(1.65*size), (int)(0.4*size), (int)(0.1*size));
				gc.fillRectangle(x*size+(int)(1.25*size), y*size+(int)(1.2*size), (int)(0.5*size), (int)(0.15*size));
				break;
			case 180:
				gc.fillRectangle(x*size+(int)(1.2*size), y*size+(int)(1.3*size), (int)(0.1*size), (int)(0.4*size));
				gc.fillRectangle(x*size+(int)(1.65*size), y*size+(int)(1.25*size), (int)(0.15*size), (int)(0.5*size));
				break;
			case 90:
				gc.fillRectangle(x*size+(int)(1.3*size), y*size+(int)(1.2*size), (int)(0.4*size), (int)(0.1*size));
				gc.fillRectangle(x*size+(int)(1.25*size), y*size+(int)(1.65*size), (int)(0.5*size), (int)(0.15*size));
				break;
			case 0:
				gc.fillRectangle(x*size+(int)(1.65*size), y*size+(int)(1.3*size), (int)(0.1*size), (int)(0.4*size));
				gc.fillRectangle(x*size+(int)(1.2*size), y*size+(int)(1.25*size), (int)(0.15*size), (int)(0.5*size));
			default:
				
				break;
			}
			/* Front */
			gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
			switch (map.getRotation()) {
			case 270:
				gc.fillRectangle(x*size+(int)(1.35*size), y*size+(int)(1.6*size), (int)(0.3*size), (int)(0.3*size));
				break;
			case 180:
				gc.fillRectangle(x*size+(int)(1.1*size), y*size+(int)(1.35*size), (int)(0.3*size), (int)(0.3*size));
				break;
			case 90:
				gc.fillRectangle(x*size+(int)(1.35*size), y*size+(int)(1.1*size), (int)(0.3*size), (int)(0.3*size));
				break;
			case 0:
				gc.fillRectangle(x*size+(int)(1.6*size), y*size+(int)(1.35*size), (int)(0.3*size), (int)(0.3*size));
			default:
				
				break;
			}
			/* Back */
			gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			switch (map.getRotation()) {
			case 270:
				gc.fillRectangle(x*size+(int)(1.3*size), y*size+(int)(1.1*size), (int)(0.4*size), (int)(0.5*size));
				break;
			case 180:
				gc.fillRectangle(x*size+(int)(1.4*size), y*size+(int)(1.3*size), (int)(0.5*size), (int)(0.4*size));
				break;
			case 90:
				gc.fillRectangle(x*size+(int)(1.3*size), y*size+(int)(1.4*size), (int)(0.4*size), (int)(0.5*size));
				break;
			case 0:
			default:
				gc.fillRectangle(x*size+(int)(1.1*size), y*size+(int)(1.3*size), (int)(0.5*size), (int)(0.4*size));
				break;
			}
		}
		resetColors(gc);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.keyCode) {
		case SWT.ARROW_LEFT:
			map.moveCursorLeft();
			break;
		case SWT.ARROW_RIGHT:
			map.moveCursorRight();
			break;
		case SWT.ARROW_UP:
			map.moveCursorUp();
			break;
		case SWT.ARROW_DOWN:
			map.moveCursorDown();
			break;
		case SWT.SHIFT:
			map.setMark();
			break;
		case SWT.ESC:
			map.unsetMark();
			break;
		case SWT.DEL:
			map.unsetRobot();
			break;
		case SWT.HOME:
			map.setCursorLeft();
			break;
		case SWT.END:
			map.setCursorRight();
			break;
		case SWT.PAGE_UP:
			map.setCursorTop();
			break;
		case SWT.PAGE_DOWN:
			map.setCursorBottom();
			break;
		default:
			switch (Character.toLowerCase(e.character)) {
			case 'f':
			case 'w':
			case 'o':
			case 'r':
			case ' ':
				map.setField(Character.toLowerCase(e.character));
				break;
			default:
				break;
			}
		}
		update();
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	private void addCanvas() {
		canvas = new Canvas(shell, SWT.BORDER);
		FormData canvasData = new FormData();
		canvasData.left = new FormAttachment(0);
		canvasData.right = new FormAttachment(100);
		canvasData.top = new FormAttachment(0);
		canvasData.bottom = new FormAttachment(statusbarLabel);
		canvas.setLayoutData(canvasData);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		canvas.addPaintListener(this);
		canvas.addKeyListener(this);
	}
	
	private void addStatusbar() {
		statusbarLabel = new Label(shell, SWT.BORDER);
		FormData labelData = new FormData();
	    labelData.left = new FormAttachment(0);
	    labelData.right = new FormAttachment(100);
	    labelData.bottom = new FormAttachment(100);
	    statusbarLabel.setLayoutData(labelData);
	    statusbarLabel.setText("Keys: f - Floor | w - Wall | o - Obstacle | SPACE - Empty Field | r - Place/Rotate Robot | SHIFT - Set mark");
	}
	
	private boolean saveMapAs() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save as");
        String[] filterExt = { "*.rmf", "*.*"};
        fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        if (selected != null) {
        	if (new File(selected).exists()) {
        		MessageBox m = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO );
				m.setText("Confirm overwrite");
				m.setMessage("File already exits. Overwrite?\n"+selected);
				if(m.open() == SWT.NO) {
					return false;
				}
        	}
           	boolean result = map.saveAsMap(selected);
           	update();
           	return result;
        }
        return false;
	}
	
	private boolean saveMap() {
		boolean result = false;
		if (map.getFileName().isEmpty()) {
			result = saveMapAs();
		}
		else {
			result = map.saveMap();
		}
		update();
		return result;
	}
	
	private void newMap() {
		map = new RoboMap();
		update();
	}
	
	private void openMap() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
        fd.setText("Open");
        String[] filterExt = { "*.rmf", "*.*"};
        fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        if (selected != null) {
        	map = new RoboMap(selected);
        	update();
        }
	}
	
	private void addMenubar() {
		Menu menuBar = new Menu(shell, SWT.BAR);
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");
		
		MenuItem editMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		editMenuHeader.setText("&Edit");
				
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);
		
		Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
		editMenuHeader.setMenu(editMenu);
		
		MenuItem fileMenuNew = new MenuItem(fileMenu, SWT.PUSH);
		fileMenuNew.setText("&New\tCTRL+n");
		fileMenuNew.setAccelerator(SWT.CTRL + 'N');
		fileMenuNew.addListener(SWT.Selection, new Listener() {	
			@Override
			public void handleEvent(Event event) {
				MessageBox m = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				m.setText("Confirm discard");
				m.setMessage("Save map before create new map?");
				boolean result = true;
				switch (m.open()) {
				case SWT.YES:
					result = saveMap();
				case SWT.NO:
					if (result) {
						newMap();
					}
					break;
				case SWT.CANCEL:
				default:
					break;
				}
			}
		});
		
		MenuItem fileMenuOpen = new MenuItem(fileMenu, SWT.PUSH);
		fileMenuOpen.setText("&Open\tCTRL+o");
		fileMenuOpen.setAccelerator(SWT.CTRL + 'O');
		fileMenuOpen.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MessageBox m = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				m.setText("Confirm discard");
				m.setMessage("Save map before open map?");
				boolean result = true;
				switch (m.open()) {
				case SWT.YES:
					result = saveMap();
				case SWT.NO:
					if (result) {
						openMap();
					}
					break;
				case SWT.CANCEL:
				default:
					break;
				}
			}
		});
		
		MenuItem fileMenuSave = new MenuItem(fileMenu, SWT.PUSH);
		fileMenuSave.setText("&Save\tCTRL+s");
		fileMenuSave.setAccelerator(SWT.CTRL + 'S');
		fileMenuSave.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				saveMap();
			}
		});
		
		MenuItem fileMenuSaveAs = new MenuItem(fileMenu, SWT.PUSH);
		fileMenuSaveAs.setText("Save &as\tCTRL+w");
		fileMenuSaveAs.setAccelerator(SWT.CTRL + 'W');
		fileMenuSaveAs.addListener(SWT.Selection, new Listener() {	
			@Override
			public void handleEvent(Event event) {
				saveMapAs();
			}
		});
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem fileMenuExit = new MenuItem(fileMenu, SWT.PUSH);
		fileMenuExit.setText("&Exit\tCTRL+q");
		fileMenuExit.setAccelerator(SWT.CTRL + 'Q');
		fileMenuExit.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MessageBox m = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				m.setText("Confirm discard");
				m.setMessage("Save map before exit?");
				boolean result = true;
				switch (m.open()) {
				case SWT.YES:
					result = saveMap();
				case SWT.NO:
					if (result) {
						shell.close();
					}
					break;
				case SWT.CANCEL:
				default:
					break;
				}
			}
		});
		
		MenuItem addLinesMenu = new MenuItem(editMenu, SWT.PUSH);
		addLinesMenu.setText("&Add fields\tCTRL+a");
		addLinesMenu.setAccelerator(SWT.CTRL + 'A');
		addLinesMenu.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				AddFieldsDialog afd = new AddFieldsDialog(shell);
				if (afd.open() == SWT.OK) {
					addLines(afd.dest, afd.number);
					update();
				}
			}
		});
		
		shell.setMenuBar(menuBar);
	}
	
	private void addLines(int dest, int n) {
		switch (dest) {
		case AddFieldsDialog.LEFT:
			for (int i = 0; i < n; ++i) {
				map.addColumnLeft();
			}
			break;
		case AddFieldsDialog.TOP:
			for (int i = 0; i < n; ++i) {
				map.addLineFront();
			}
			break;
		case AddFieldsDialog.BOTTOM:
			for (int i = 0; i < n; ++i) {
				map.addLineEnd();
			}
			break;
		case AddFieldsDialog.RIGHT:
		default:
			for (int i = 0; i < n; ++i) {
				map.addColumnRight();
			}
			break;
		}
	}
	
	private void update() {
		String fileName = map.getFileName();
		if (fileName.isEmpty()) {
			fileName = "New Map";
		}
		shell.setText("Robo-Map Editor - " + fileName);
		canvas.redraw();
	}
	
}
