package de.menski.robosim;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class RoboSim {

	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	static public void setupLogger() throws IOException {
		// Create Logger
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler("robomap.log");

		// Create txt Formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoboMap map = new RoboMap();
		if (args.length == 1) {
			try {
				map.loadMap(args[0]);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			setupLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RoboMapEditor editor = new RoboMapEditor(map);
		editor.open();
	}

}
