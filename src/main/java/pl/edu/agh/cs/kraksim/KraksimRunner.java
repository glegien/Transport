package pl.edu.agh.cs.kraksim;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.main.MainVisualisationPanel;
import pl.edu.agh.cs.kraksim.main.Simulation;

public class KraksimRunner {
	private static final Logger logger = Logger.getLogger(KraksimRunner.class);

	/**
	 * Main
	 * 
	 * @param args
	 *            may contain config file path
	 */
	public static void main(String[] args) {

		final Properties props = KraksimConfigurator.getPropertiesFromFile(args);

		// we assume that if there is no word about visualisation in config,
		// then it is necessary...
		boolean visualise = true;
		// but if there is...
		if (props.containsKey("visualization") && props.getProperty("visualization").equals("false")) {
			visualise = false;
		}

		// set up the prediction
		String predictionConfig = props.getProperty("enablePrediction");
		String predictionFileConfig = props.getProperty("predictionFile");
		if ((predictionConfig == null) || !(predictionConfig.equals("true"))) {
			KraksimConfigurator.disablePrediction();
			logger.debug("Prediction disabled");
		} else {
			KraksimConfigurator.configurePrediction(predictionFileConfig);
			logger.debug("Prediction configured");
		}
		logger.debug(props.getProperty("dynamicRouting"));
		

		// start simulation - with or without visualisation
		if (visualise) {
			String lookAndFeel = null;
			lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			try {
				UIManager.setLookAndFeel(lookAndFeel);
			} catch (Exception e) {

			}

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					JFrame frame = new JFrame("Kraksim Visualiser");
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

					frame.getContentPane().add(new MainVisualisationPanel(props));
					frame.setSize(370, 270);
					// frame.setResizable( false );
					frame.setVisible(true);
				}
			});
		} else {
			/*
			 * Thread simThread = new Thread (new Runnable(){
			 * 
			 * //@Override public void run() { Simulation sim = new
			 * Simulation(KraksimConfigurator
			 * .prepareInputParametersForSimulation(props)); sim.doRun(); } });
			 */
			Thread simThread = new Thread(
					new Simulation(KraksimConfigurator.prepareInputParametersForSimulation(props)));

			simThread.start();
			try {
				simThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
