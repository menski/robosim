package de.menski.robosim;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.Point;


public class RoboMap {
	
	private final static Logger LOGGER = Logger.getLogger(RoboMap.class.getName());
	
	private static final int UNSET = -99;
	
	private ArrayList<ArrayList<Character>> map;
	private String fileName;
	private int xSize;
	private Point mark;
	private Point cursor;
	private Point robot;
	private int rotation;

	public RoboMap() {
		map = new ArrayList<ArrayList<Character>>();
		fileName = "";
		xSize = 0;
		mark = new Point(0,0);
		unsetMark();
		cursor = new Point(0, 0);
		robot = new Point(0,0);
		unsetRobot();
		rotation = 0;
	}
	
	public RoboMap(String fileName) {
		this();
		loadMap(fileName);
		this.fileName = fileName;
	}
	
	private boolean isEqualPoint(Point p, int x, int y) {
		return p.x == x && p.y == y;
	}
	
	private boolean isPointSet(Point p) {
		return p.x != UNSET && p.y != UNSET;
	}
	
	private void setPoint(Point p, int x, int y) {
		p.x = x;
		p.y = y;
	}
	
	private void setPoint(Point p, int x) {
		setPoint(p, x, x);
	}
	
	private void copyPoint(Point src, Point dest) {
		dest.x = src.x;
		dest.y = src.y;
	}
	
	public void setMark() {
		copyPoint(cursor, mark);
	}
	
	public void unsetPoint(Point p) {
		setPoint(p, UNSET);
	}
	
	public void unsetMark() {
		unsetPoint(mark);
	}
	
	public void unsetRobot() {
		rotation = 0;
		unsetPoint(robot);
	}
	
	public boolean isMarkSet() {
		return isPointSet(mark);
	}
	
	public boolean isRobotSet() {
		return isPointSet(robot);
	}
	
	public Point getMark() {
		return mark;
	}
	
	public int getMinMarkX() {
		if (isMarkSet()) {
			return Math.min(cursor.x, mark.x);
		}
		else {
			return cursor.x;
		}		
	}
	
	public int getMinMarkY() {
		if (isMarkSet()) {
			return Math.min(cursor.y, mark.y);
		}
		else {
			return cursor.y;
		}
	}
	
	public int getMaxMarkX() {
		if (isMarkSet()) {
			return Math.max(cursor.x, mark.x);
		}
		else {
			return cursor.x;
		}
	}
	
	public int getMaxMarkY() {
		if (isMarkSet()) {
			return Math.max(cursor.y, mark.y);
		}
		else {
			return cursor.y;
		}
	}
	
	public int getSize(int y) {
		return map.get(y).size();
	}
	
	public char getField(int y, int x) {
		return (char) map.get(y).get(x);
	}
	
	public void addField(int y, char c) {
		map.get(y).add(c);
	}
	
	public void addField(int y, int x, char c) {
		map.get(y).add(x, c);
	}
	
	public void setField(int y, int x, char c) {
		map.get(y).set(x, c);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public int getXSize() {
		return xSize;
	}
	
	public int getYSize() {
		return map.size();
	}
	
	public Point getCursor() {
		return cursor;
	}
	
	public void setCursorLeft() {
		cursor.x = 0;
	}
	
	public void setCursorRight() {
		cursor.x = getXSize() - 1;
	}
	
	public void setCursorTop() {
		cursor.y = 0;
	}

	public void setCursorBottom() {
		cursor.y = getYSize() - 1;
	}
	
	public Point getRobot() {
		return robot;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	private void addLine(ArrayList<Character> line) {
		map.add(line);
		xSize = Math.max(xSize, line.size());
	}

	public boolean loadMap(String fileName) {
		LOGGER.info(String.format("Try to load map from file %s", fileName));
		try {
			FileReader fin = new FileReader(fileName);
			int c;
			map.clear();
			ArrayList<Character> line = new ArrayList<Character>();
			while ((c = fin.read()) != -1) {
				if (c == 10 || c == 13) {
					addLine(line);
					line = new ArrayList<Character>();
					if (c == 13) {
						if ((c = fin.read()) != -1) {
							if (c == 10) {
								continue;
							}
						}
						else {
							break;
						}
					}
					else {
						continue;
					}
				}
				switch (c) {
				case 'v':
					rotation = 270;
					line.add(new Character('f'));
					setPoint(robot, line.size()-1, getYSize());
					break;
				case '<':
					rotation = 180;
					line.add(new Character('f'));
					setPoint(robot, line.size()-1, getYSize());
					break;
				case '^':
					rotation = 90;
					line.add(new Character('f'));
					setPoint(robot, line.size()-1, getYSize());
					break;
				case '>':
					rotation = 0;
					line.add(new Character('f'));
					setPoint(robot, line.size()-1, getYSize());
					break;
				case 'f':
				case 'w':
				case 'o':
				case ' ':
					line.add(new Character((char) c));
					break;
				default:
					line.add(new Character(' '));
					break;
				}
			}
			addLine(line);
			fin.close();
			for (int y = 0; y < getYSize(); ++y) {
				if (getSize(y) < getXSize()) {
					for (int x = getSize(y); x < getXSize(); ++x) {
						addField(y, ' ');
					}
				}
			}
			this.fileName = fileName;
			setPoint(cursor, 0);
			LOGGER.info(String.format("Successful load map from file %s", fileName));
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(String.format("Unable to load map from file %s", fileName));
		}
		return false;
	}
	
	public boolean saveMap() {
		return saveAsMap(fileName);
	}
	
	public boolean saveAsMap(String fileName) {
		LOGGER.info(String.format("Try to save map to file ", fileName));
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			for (int y = 0; y < getYSize(); ++y) {
				for (int x = 0; x < getSize(y); ++x) {
					if (isEqualPoint(robot, x, y)) {
						char c;
						switch (rotation) {
						case 90:
							c = '^';
							break;
						case 180:
							c = '<';
							break;
						case 270:
							c = 'v';
							break;
						case 0:
						default:
							c = '>';
							break;
						}
						out.write(c);
					}
					else {
						out.write(getField(y, x));
					}
				}
				out.newLine();
			}
			out.close();
			this.fileName = fileName;
			LOGGER.info(String.format("Successful save map to file %s", fileName));
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(String.format("Unable to save map to file %s", fileName));
		}
		return false;
	}
	
	public void printMap() {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getSize(y); ++x) {
				System.out.print(getField(y, x));
			}
			System.out.println();
		}
	}
	
	public void moveCursorRight() {
		if (++cursor.x > getXSize()) {
			cursor.x = -1;
		}
	}
	
	public void moveCursorLeft() {
		if (--cursor.x < -1) {
			cursor.x = getXSize();
		}
	}
	
	public void moveCursorUp() {
		if (--cursor.y < -1) {
			cursor.y = getYSize();
		}
	}
	
	public void moveCursorDown() {
		if (++cursor.y > getYSize()) {
			cursor.y = -1;
		}
	}
	
	private boolean markContainsRobot() {
		return robot.y >= getMinMarkY() && robot.y <= getMaxMarkY() && robot.x >= getMinMarkX() && robot.x <= getMaxMarkX();
	}
	
	private void fillLine(int y) {
		map.get(y).clear();
		for (int x = 0; x < getXSize(); ++x) {
			addField(y, ' ');
		}
	}
	
	public void addLineFront() {
		LOGGER.info("Add a line at the beginning");
		map.add(0, new ArrayList<Character>());
		LOGGER.info("Fill line 0 with ' '");
		fillLine(0);
		LOGGER.info(String.format("Increment cursor.y = %d => %d", cursor.y, cursor.y+1));
		cursor.y++;
		if (isMarkSet()) {
			LOGGER.info(String.format("Increment mark.y = %d => %d", mark.y, mark.y+1));
			mark.y++;
		}
		LOGGER.info(String.format("Increment robot.y = %d => %d", robot.y, robot.y+1));
		robot.y++;
	}
	
	public void addLineEnd() {
		LOGGER.info("Add a line at the end");
		map.add(new ArrayList<Character>());
		LOGGER.info(String.format("Fill line %d with ' '", getYSize()));
		fillLine(getYSize()-1);
	}
	
	public void addColumnLeft() {
		LOGGER.info("Add a field in front of each line");
		for (int y = 0; y < getYSize(); ++y) {
			addField(y, 0, ' ');
		}
		LOGGER.info(String.format("Increment cursor.x = %d => %d", cursor.x, cursor.x+1));
		cursor.x++;
		if (isMarkSet()) {
			LOGGER.info(String.format("Increment mark.x = %d => %d", mark.x, mark.x+1));
			mark.x++;
		}
		LOGGER.info(String.format("Increment robot.y = %d => %d", robot.y, robot.y+1));
		robot.x++;
		LOGGER.info(String.format("Increment xSize = %d => %d", xSize, xSize + 1));
		xSize++;
	}
	
	public void addColumnRight() {
		LOGGER.info("Add a field at the end of each line");
		for (int y = 0; y < getYSize(); ++y) {
				addField(y, ' ');
		}
		LOGGER.info(String.format("Increment xSize = %d => %d", xSize, xSize + 1));
		xSize++;
	}
	
	public void setField(char c) {
		if (c == 'f' || c == 'w' || c == 'o' || c == ' ') {
			if (getMinMarkY() < 0) {	/* Neue Zeile am Anfang */
				LOGGER.info(String.format("Add a line at the beginning [ getMinMarkY() = %d < 0 ]", getMinMarkY()));
				addLineFront();
			}
			if (getMaxMarkY() == getYSize()) { /* Neue Zeile am Ende */
				LOGGER.info(String.format("Add a line at the end [ getMaxMarkY() = %d < getYSize() = %d ]", getMinMarkY(), getYSize()));
				addLineEnd();
			}
			if (getMinMarkX() < 0) { /* Alle Zeilen um ein Feld am Anfang erweitern */
				LOGGER.info(String.format("Add a field in front of each line [ getMinMarkX() = %d < 0 ]", getMinMarkX()));
				addColumnLeft();
			}
			if (getMaxMarkX() >= getXSize()) { /* Zeilen um nötige Anzahl an Feldern erweitern */
				LOGGER.info(String.format("Add a field at the end of each line [ getMaxMarkX() = %d < getXSize() = %d ]", getMinMarkX(), getXSize()));
				addColumnRight();
			}
		}
		
		switch(c) {
		case 'f':
		case 'w':
		case 'o':
		case ' ':
			if (markContainsRobot()) {
				LOGGER.info("Mark contains robot");
				if (c != 'f') {
					LOGGER.info("Unset robot");
					unsetRobot();
				}
			}
			LOGGER.info(String.format("Set fields from (%d,%d) to (%d,%d) with '%c'", getMinMarkY(), getMinMarkX(), getMaxMarkY(), getMaxMarkX(), c));
			for (int y = getMinMarkY(); y <= getMaxMarkY(); ++y) {
				for (int x = getMinMarkX(); x <= getMaxMarkX(); ++x) {
					setField(y, x, c);		
				}
			}
			unsetMark();
			break;
		case 'r':
			unsetMark();
			if (cursor.y >= 0 && cursor.y < getYSize() && cursor.x >= 0 && cursor.x <= getXSize() && getField(cursor.y, cursor.x) == 'f') {
				if (cursor.equals(robot)) {
					rotation = (rotation+90) % 360; 
				}
				else {
					copyPoint(cursor, robot);
				}
			}
			else {
				System.err.println("Roboter can only be placed on floor fields");
			}
			break;
		default:
			System.err.printf("No valid field character '%c'\n", c);
			break;
		}
	}
}
