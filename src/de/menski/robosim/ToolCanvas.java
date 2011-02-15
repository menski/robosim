package de.menski.robosim;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sun.rmi.runtime.Log;

public class ToolCanvas extends Canvas implements PaintListener, MouseListener {
	
	private final static Logger LOGGER = Logger.getLogger(ToolCanvas.class.getName());

	private static final char[] BUTTONS = {'0', ' ', 'f', 'w', 'o', 'r'};
	private static final int NFIELDS = BUTTONS.length;

	private Shell shell;
	private Display display;
	private int fieldSize;
	private int fieldBorder;
	private int selected;

	public ToolCanvas(Composite composite, int style) {
		super(composite, style);
		shell = (Shell) composite;
		display = shell.getDisplay();
		addPaintListener(this);
		addMouseListener(this);
		selected = 0;
		setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	@Override
	public void paintControl(PaintEvent e) {
		fieldSize = Math.min(getClientArea().height / NFIELDS, getClientArea().width-(int)(0.2*getClientArea().width));
		fieldBorder = (getClientArea().width - fieldSize)/2;
		for (int i = 0; i < NFIELDS; ++i) {
			Rectangle field = new Rectangle(fieldBorder, (i+1)*fieldBorder+i*fieldSize, fieldSize, fieldSize);
			switch (i) {
			case 0:				
				drawNothing(e.gc, field);
				break;
			case 1:
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.fillRectangle(field);
				resetColors(e.gc);
				break;
			case 2:			
				drawFloor(e.gc, field);
				break;
			case 3:				
				drawWall(e.gc, field);
				break;
			case 4:				
				drawObstacle(e.gc, field);
				break;
			case 5:				
				drawRobot(e.gc, field);
				break;
			default:
				break;
			}
			e.gc.drawRectangle(field);
			if (i == selected) {
				drawSelection(e.gc, field);
			}
		}
	}

	private void resetColors(GC gc) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	}
	
	private void drawSelection(GC gc, Rectangle field) {
		gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.setLineWidth(3);
		gc.drawRectangle(field);
		gc.setLineWidth(0);
		resetColors(gc);		
	}
	
	private void drawNothing(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
		int[] polygon = new int[14];
		polygon[0] = field.x+(int)(0.9*field.width);
		polygon[1] = field.y+(int)(0.8*field.height);
		polygon[2] = field.x+(int)(0.8*field.width);
		polygon[3] = field.y+(int)(0.9*field.height);
		polygon[4] = field.x+(int)(0.3*field.width);
		polygon[5] = field.y+(int)(0.4*field.height);
		polygon[6] = field.x+(int)(0.2*field.width);
		polygon[7] = field.y+(int)(0.5*field.height);
		polygon[8] = field.x+(int)(0.1*field.width);
		polygon[9] = field.y+(int)(0.1*field.height);
		polygon[10] = field.x+(int)(0.5*field.width);
		polygon[11] = field.y+(int)(0.2*field.height);
		polygon[12] = field.x+(int)(0.4*field.width);
		polygon[13] = field.y+(int)(0.3*field.height);
		gc.fillPolygon(polygon);
		resetColors(gc);
	}

	private void drawWall(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(field);
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
		resetColors(gc);
	}

	private void drawObstacle(GC gc, Rectangle field) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		gc.fillRectangle(field);
		gc.drawLine(field.x, field.y, field.x+field.width, field.y+field.height);
		gc.drawLine(field.x+field.width, field.y, field.x, field.y+field.height);
		resetColors(gc);
	}

	private void drawRobot(GC gc, Rectangle field) {
		/* Tyres */
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.fillRectangle(field.x+(int)(0.3*field.width), field.y+(int)(0.2*field.height), (int)(0.4*field.width), (int)(0.1*field.height));
		gc.fillRectangle(field.x+(int)(0.25*field.width), field.y+(int)(0.65*field.height), (int)(0.5*field.width), (int)(0.15*field.height));

		/* Front */
		gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		gc.fillRectangle(field.x+(int)(0.35*field.width), field.y+(int)(0.1*field.height), (int)(0.3*field.width), (int)(0.3*field.height));

		/* Back */
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(field.x+(int)(0.3*field.width), field.y+(int)(0.4*field.height), (int)(0.4*field.width), (int)(0.5*field.height));

		resetColors(gc);
	}
	
	public char getSelected() {
		return BUTTONS[selected];
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.x > fieldBorder && e.x < fieldBorder+fieldSize) {
			int s = (int) (e.y/(fieldBorder+fieldSize));
			if (s < NFIELDS && e.y > s*(fieldBorder+fieldSize)+fieldBorder && e.y < (s+1)*(fieldBorder+fieldSize)) {
				selected = s;
				redraw();
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {}

}
