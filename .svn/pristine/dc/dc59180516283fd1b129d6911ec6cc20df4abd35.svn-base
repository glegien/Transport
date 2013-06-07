package pl.edu.agh.cs.kraksim.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.collections15.Transformer;

import pl.edu.agh.cs.kraksim.KraksimConfigurator;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.core.Phase;
import pl.edu.agh.cs.kraksim.core.Phase.LightState;
import pl.edu.agh.cs.kraksim.sna.GraphVisualizator;
import pl.edu.agh.cs.kraksim.sna.centrality.CentrallityCalculator;
import pl.edu.agh.cs.kraksim.sna.centrality.KmeansClustering;
import pl.edu.agh.cs.kraksim.sna.centrality.MeasureType;
import pl.edu.agh.cs.kraksim.util.MeasuresExcelWriter;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class MainVisualisationPanel extends JPanel implements GraphVisualizator{

	private static final long serialVersionUID = 2195425331247205783L;
	
	private VisualizationViewer<Node, Link> vv;

	private transient Controllable sim = null;
	private JPanel simPanel = null;
	private Component ctrlPane = null;

	JPanel commandsPane = null;
	JButton run = new JButton("Run");
	JButton step = new JButton("Step");
	JButton pause = new JButton("Pause");

	//Do grafu
	JPanel measures = new JPanel();
	JPanel  graphPanel = new JPanel();
	private MeasuresExcelWriter excelWriter = new MeasuresExcelWriter();
	
	private Properties params = new Properties();

	SetUpPanel setUpPanel = null;

	public MainVisualisationPanel() {
		// super();
		initLayout();
	}

	public MainVisualisationPanel(Properties props) {
		// super();
		initParams(props);
		initLayout();
	}

	private void initParams(Properties params) {
		this.params = params;
	}

	private void setProperties(Properties params) {
		this.params.putAll(params);
	}

	public void initializeSimulation(Properties params) {
		setProperties(params);
		System.out.println("Simulation is to be initialized");

		if (sim != null) {
			this.sim = null;
			this.remove(this.simPanel);
			this.simPanel = null;
		}

		String[] paramsList = KraksimConfigurator.prepareInputParametersForSimulation(params);
		sim = new Simulation(paramsList);
		
		sim.setGraphVisualizator(this);

		SimulationVisualizator vis = sim.getVisualizator();

		if (vis instanceof GUISimulationVisualizator) {
			GUISimulationVisualizator simPanel = ((GUISimulationVisualizator) sim
					.getVisualizator());

			this.addSimPanel(simPanel.getVisualizatorComponent());
			this.addControlPanel(simPanel.getControllPane());

		}
	
		Thread runner = new Thread(sim);
		runner.start();

		run.setEnabled(true);
		step.setEnabled(true);
		pause.setEnabled(false);
		initGraph();
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("Plik");
		bar.add(file);
		
		JMenuItem loadMenu = new JMenuItem("Załaduj");

		loadMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainVisualisationPanel.this.initializeSimulation(MainVisualisationPanel.this.params);
			}
		});
		
		JMenuItem load = new JMenuItem("Ustawienia");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainVisualisationPanel.this.pause.doClick();
				SetUpPanel panel = MainVisualisationPanel.this.setUpPanel;
				if (panel == null) {
					panel = new SetUpPanel(MainVisualisationPanel.this,
							MainVisualisationPanel.this.params);
					MainVisualisationPanel.this.setUpPanel = panel;
				} else {
					panel.initLayout();
				}
			}
		});

		JMenuItem exit = new JMenuItem("Zakończ");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(1);
			}
		});
		file.add(loadMenu);
		file.add(load);
		file.add(exit);
		this.add(bar, BorderLayout.NORTH);

		commandsPane = new JPanel();
		commandsPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		commandsPane.setBorder(BorderFactory.createTitledBorder("Commands"));
		commandsPane.setPreferredSize(new Dimension(600, 55));
		commandsPane.setMinimumSize(new Dimension(600, 55));
		commandsPane.setMaximumSize(new Dimension(1600, 55));

		// synchronize buttons first
		run.setEnabled(false);
		step.setEnabled(false);
		pause.setEnabled(false);

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sim != null) {
					sim.doRun();

					run.setEnabled(false);
					step.setEnabled(false);
					pause.setEnabled(true);
				}
			}
		});

		step.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (sim != null) {
					sim.doStep();

					run.setEnabled(true);
					step.setEnabled(true);
					pause.setEnabled(false);
				}
			}
		});

		pause.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (sim != null) {
					sim.doPause();

					run.setEnabled(true);
					step.setEnabled(true);
					pause.setEnabled(false);
				}
			}
		});

		commandsPane.add(run);
		commandsPane.add(step);
		commandsPane.add(pause);

		commandsPane.setVisible(false);

	}

	private void addControlPanel(Component ctrlPanel) {
		if (this.ctrlPane != null) {
			this.remove(this.ctrlPane);
		}
		this.ctrlPane = ctrlPanel;
		add(ctrlPanel, BorderLayout.SOUTH);
		ctrlPanel.setVisible(true);
	}

	private void addSimPanel(Component simPanel) {
		JPanel pane = null;
		if (this.simPanel != null) {
			this.simPanel.removeAll();
			pane = this.simPanel;
		} else {
			pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.add(commandsPane, BorderLayout.NORTH);
			this.simPanel = pane;
		}

		JScrollPane scroller = new JScrollPane(simPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(600, 400));
		scroller.setMinimumSize(new Dimension(600, 100));
		scroller.setMaximumSize(new Dimension(1600, 1200));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		graphPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("Simulation", null, scroller, "");
		JScrollPane graphScroller = new JScrollPane(graphPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabbedPane.addTab("Graph", graphScroller);

		commandsPane.setVisible(true);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(tabbedPane, BorderLayout.CENTER);
		panel.add(new JScrollPane(measures, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.LINE_END);
		
		//pane.add(scroller, BorderLayout.CENTER);
		pane.add(panel, BorderLayout.CENTER);

		add(pane, BorderLayout.CENTER);
	}

	public Simulation getSimulation(){
		return (Simulation)sim;
	}
	
	@Override
	public void refreshGraph() {
		CentrallityCalculator.calculateCentrallity(getSimulation().getModules().getGraph(), MeasureType.PageRank,3);
		refreshMeasures(new ArrayList<Node>(getSimulation().getModules().getGraph().getVertices()));
		refreshGraphCoolors();
	}
	
	private void initGraph(){
		Graph<Node, Link> graph = getSimulation().getModules().getGraph();
		KmeansClustering.clusterGraph(graph);
		
		Layout<Node, Link> layout = new FRLayout<Node, Link>(graph);
		layout.setSize(new Dimension(900, 600)); 
		double maxX = 0, maxY = 0;
		for(Node node : graph.getVertices()){
			if(node.getPoint().getX() > maxX)
				maxX = node.getPoint().getX();
			if(node.getPoint().getY() > maxY)
				maxY = node.getPoint().getY();
		}
		layout.setSize(new Dimension((int)maxX,(int)maxY));
		for(Node node : graph.getVertices()){
			Point2D p = node.getPoint();
			layout.setLocation(node, p);
		}
		System.out.println(maxX + " - " + maxY);
		vv = new VisualizationViewer<Node, Link>(
				layout);
		vv.setPreferredSize(new Dimension(900,600));
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
			
			public Paint transform(Node node) {
				Color [] colors = {Color.yellow,Color.blue,Color.red, Color.pink,Color.white,Color.cyan,Color.orange, Color.magenta, Color.gray, Color.black};
				if(node.isGateway())
					return Color.GREEN;
				
				Collection<Set<Node>> colection = KmeansClustering.currentClustering.values();
				//kolor klastra
				Iterator<Set<Node>> iterator = colection.iterator();
				for(int i=0; i<colection.size();i++)
					if(iterator.next().contains(node)){
						return colors[i];
					}
				return Color.white;
			}
		});
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
			
			public String transform(Node node) {
				return node.getId();
			}
		});
        
        vv.getRenderContext().setVertexShapeTransformer(new Transformer<Node, Shape>() {
			
			@Override
			public Shape transform(Node arg0) {
				if(arg0.isGateway())
					new Ellipse2D.Double(-15, -15, 30, 30);
				Collection<Set<Node>> colection = KmeansClustering.currentClustering.values();
				Set<Node> meansColection = KmeansClustering.currentClustering.keySet();

				Iterator<Set<Node>> iterator = colection.iterator();
				for(int i=0; i<colection.size();i++)
					if(iterator.next().contains(arg0)){
						if(meansColection.contains(arg0))
							return new Rectangle(-15, -15, 30, 30);
						return new Ellipse2D.Double(-15, -15, 30, 30);
					}
				return new Ellipse2D.Double(-15, -15, 30, 30);
			}
		});
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm); 
		graphPanel.add(vv, BorderLayout.CENTER);
		
		measures.setLayout(new GridLayout(graph.getVertexCount(), 2));
		List<Node> nodes = new ArrayList<Node>(graph.getVertices());
		refreshMeasures(nodes);
		
		
	}
	
	private void refreshGraphCoolors(){
		Graph<Node, Link> graph = getSimulation().getModules().getGraph();
		KmeansClustering.clusterGraph(graph);
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
			
			public Paint transform(Node node) {
				Color [] colors = {Color.yellow,Color.blue,Color.red, Color.pink,Color.white,Color.cyan,Color.orange, Color.magenta, Color.gray, Color.black};
				if(node.isGateway())
					return Color.GREEN;
				
				Collection<Set<Node>> colection = KmeansClustering.currentClustering.values();
				//kolor klastra
				Iterator<Set<Node>> iterator = colection.iterator();
				for(int i=0; i<colection.size();i++)
					if(iterator.next().contains(node)){
						return colors[i];
					}
				return Color.white;
			}
		});
		vv.getRenderContext().setVertexShapeTransformer(new Transformer<Node, Shape>() {
			
			@Override
			public Shape transform(Node arg0) {
				if(arg0.isGateway())
					new Ellipse2D.Double(-15, -15, 30, 30);
				Collection<Set<Node>> colection = KmeansClustering.currentClustering.values();
				Set<Node> meansColection = KmeansClustering.currentClustering.keySet();

				Iterator<Set<Node>> iterator = colection.iterator();
				for(int i=0; i<colection.size();i++)
					if(iterator.next().contains(arg0)){
						if(meansColection.contains(arg0))
							return new Rectangle(-15, -15, 30, 30);
						return new Ellipse2D.Double(-15, -15, 30, 30);
					}
				return new Ellipse2D.Double(-15, -15, 30, 30);
			}
		});
		vv.repaint();
	}
	
	private void refreshMeasures(List<Node> nodes){
		Collections.sort(nodes, new Comparator<Node>() {

			public int compare(Node o1, Node o2) {
				return new Double(o2.getMeasure()).compareTo(new Double(o1.getMeasure()));
			}
		});
		measures.removeAll();
		for(Node n : nodes){
			measures.add(new JLabel(n.getId() + " : "));
			measures.add(new JLabel(String.format("%1$.5f    ", n.getMeasure())));
			if(n instanceof Intersection){
				Intersection inter = (Intersection)n;
				for(Phase key : inter.phases){
					Iterator<LightState> iter = key.iterator();
					while(iter.hasNext()){
						LightState state = iter.next();
						System.out.println("Weszlo");
						System.out.println(state.isGreen()+ " czas " + key.getGreenDuration() + " czasczas " + key.getDuration());
					}
				}
			}
				
		}
		excelWriter.persistIteration(nodes);
	}
}
