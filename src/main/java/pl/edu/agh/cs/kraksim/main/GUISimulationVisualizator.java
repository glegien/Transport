package pl.edu.agh.cs.kraksim.main;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.ministat.CityMiniStatExt;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.sna.SnaConfigurator;
import pl.edu.agh.cs.kraksim.sna.centrality.CentrallityStatistics;
import pl.edu.agh.cs.kraksim.visual.VisualizatorComponent;

@SuppressWarnings("serial")
class GUISimulationVisualizator implements SimulationVisualizator {
	private static final Logger logger = Logger.getLogger(GUISimulationVisualizator.class);

	private transient CityMiniStatExt cityStat;
	private VisualizatorComponent visualizatorComponent;

	private JLabel phaseDisp;
	private JLabel turnDisp;
	private JLabel carCountDisp;
	private JLabel travelCountDisp;
	private JLabel avgVelocityDisp;
	private JSlider zoomSlider;
	private JSlider refreshPeriodSlider;
	private int refreshPeriod;
	private JSlider turnDelaySlider;
	private int turnDelay;
	private JPanel toolbar;
	Container controllPane;

	GUISimulationVisualizator(City city, CarInfoIView carInfoView,
			BlockIView blockView, MiniStatEView statView) {
		// setToolTipText( "kraksim" );

		cityStat = statView.ext(city);

		visualizatorComponent = createVisualizator();
		controllPane = createControlPane(visualizatorComponent);

		visualizatorComponent.loadMap(city, carInfoView, blockView, statView);
	}

	/**
	 * @return
	 * 
	 */
	private Container createControlPane(
			final VisualizatorComponent visualizatorComponent) {
		Container ctrllPane = Box.createHorizontalBox();
		// Container pane = Box.createVerticalBox();
		ctrllPane.setPreferredSize(new Dimension(600, 55));
		ctrllPane.setMinimumSize(new Dimension(600, 55));
		ctrllPane.setMaximumSize(new Dimension(1600, 55));

		phaseDisp = new JLabel("START", SwingConstants.CENTER);
		turnDisp = new JLabel();
		carCountDisp = new JLabel();
		travelCountDisp = new JLabel();
		avgVelocityDisp = new JLabel();
		resetStats();

		ctrllPane.add(wrap("phase", phaseDisp));
		ctrllPane.add(wrap("turn", turnDisp));
		ctrllPane.add(wrap("car count", carCountDisp));
		ctrllPane.add(wrap("travel count", travelCountDisp));
		ctrllPane.add(wrap("avg. V (of ended travels)", avgVelocityDisp));

		ctrllPane.add(Box.createVerticalGlue());

		zoomSlider = new JSlider(new DefaultBoundedRangeModel(40, 0, 20, 400));
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				float zoom = slider.getValue() / 100.0f;
				visualizatorComponent.setScale(zoom);
			}
		});

		ctrllPane.add(wrap("zoom", zoomSlider));

		refreshPeriodSlider = new JSlider(new DefaultBoundedRangeModel(1, 0, 1,
				100));
		refreshPeriodSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				refreshPeriod = slider.getValue();
			}
		});
		ctrllPane.add(wrap("refresh period", refreshPeriodSlider));
		refreshPeriod = 1;

		turnDelaySlider = new JSlider(new DefaultBoundedRangeModel(25, 0, 0,
				1000));
		turnDelaySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				turnDelay = slider.getValue();
			}
		});
		ctrllPane.add(wrap("turn delay", turnDelaySlider));
		turnDelay = 25;

		return ctrllPane;
	}

	/**
	 * @return
	 * 
	 */
	private VisualizatorComponent createVisualizator() {
		VisualizatorComponent visComp = new VisualizatorComponent();
		JScrollPane scroller = new JScrollPane(visComp,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(600, 400));
		scroller.setMinimumSize(new Dimension(600, 100));
		scroller.setMaximumSize(new Dimension(1600, 1200));

		return visComp;
	}

	private Box wrap(String title, JComponent component) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		box.setBorder(BorderFactory.createTitledBorder(title));
		return box;
	}

	private void resetStats() {
		turnDisp.setText("0");
		carCountDisp.setText("0");
		travelCountDisp.setText("0");
		avgVelocityDisp.setText("-");
	}

	public void startLearningPhase(int phaseNum) {
		phaseDisp.setText("LEARNING " + (phaseNum + 1));
	}

	public void startTestingPhase() {
		phaseDisp.setText("TESTING");
	}

	public void endPhase() {
		resetStats();
	}

	public void end(long elapsed) {
	}

	public void update(int turn) {
		if (turnDelay > 0)
			try {
				Thread.sleep(turnDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int travelsFinished = 0;
			String medvDisp = null;
		if (turn % refreshPeriod == 0) {
			visualizatorComponent.update();
			this.turnDisp.setText("" + turn);
			carCountDisp.setText("" + cityStat.getCarCount());
			travelsFinished = cityStat.getTravelCount();
			travelCountDisp.setText("" + travelsFinished);
			double medV = cityStat.getAvgVelocity();
			medvDisp = String.format("%5.2f", medV);
			avgVelocityDisp.setText(medvDisp);
		}
		
	    //Centrallity stats
	    if(turn % SnaConfigurator.getSnaRefreshInterval() == 0){
	    	try{
	    		CentrallityStatistics.writeTravelTimeData(cityStat, turn);
	    		CentrallityStatistics.writeKlasteringInfo(turn);
	    	}catch(Exception e){
	    		logger.error("Cannot update statistics.", e);
	    	}
	    }
	    
	    	
		
	}

	public JPanel getToolbar() {
		return toolbar;
	}

	public void setToolbar(JPanel toolbar) {
		this.toolbar = toolbar;
	}

	public Container getControllPane() {
		return controllPane;
	}

	public VisualizatorComponent getVisualizatorComponent() {
		return visualizatorComponent;
	}

	public void createWindow() {

		String lookAndFeel = null;
		lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			// on error, we get default swing look and feel
		}

		final JPanel panel = new JPanel();

		panel.add(getControllPane());
		panel.add(getVisualizatorComponent());

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				JFrame frame = new JFrame("Test");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

				frame.getContentPane().add(panel);
				frame.setSize(350, 250);
				// frame.setResizable( false );
				frame.setVisible(true);
			}
		});

	}
}
