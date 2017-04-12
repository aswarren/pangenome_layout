package pangenome.layout;

import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
//import org.gephi.io.exporter.spi.CharacterExporter;
//import org.gephi.io.exporter.spi.Exporter;
//import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
//import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.yifanHu.YifanHu;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.plugin.multilevel.MaximalMatchingCoarsening;
import org.gephi.layout.plugin.multilevel.MultiLevelLayout;
import pangenome.MultiSpecial;
import org.gephi.layout.plugin.multilevel.MultiLevelLayout;
import org.gephi.layout.plugin.multilevel.YifanHuMultiLevel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutProperty;
//import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

//import com.itextpdf.text.PageSize;

public class ImportExport {

	public ImportExport() {
		// TODO Auto-generated constructor stub
	}

	public void script(String fileName) {
		// Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// Get controllers and models
		ImportController importController = Lookup.getDefault().lookup(
				ImportController.class);

		// Import file
		Container container;
		try {

			File file=new File(fileName);

			// this.testFile(file);
			container = importController.importFile(file);
			
			if (container == null) {
				System.out.println("container is null");
				return ;
			}
			ContainerLoader loader = container.getLoader();

			loader.setEdgeDefault(EdgeDefault.UNDIRECTED); // Force DIRECTED

			container.setAllowAutoNode(true); // Don't create missing nodes

		} catch (Exception ex) {
			ex.printStackTrace();
			
			return;
		}

		// Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		// Get graph model of current workspace
		GraphModel graphModel = Lookup.getDefault()
				.lookup(GraphController.class).getModel();

		//run YifanMultiLevel and ForceAtlas2
		panacondaLayout(graphModel);

		String output_file=fileName.replace(".gexf", ".layout.gexf");

		// Export full graph
		ExportController ec = Lookup.getDefault()
				.lookup(ExportController.class);
		try {
			ec.exportFile(new File(output_file));
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		
		/*

		// Export only visible graph
		GraphExporter exporter = (GraphExporter) ec.getExporter("gexf"); // Get GEXF exporter
																			
		exporter.setExportVisible(true); // Only exports the visible (filtered) graph
		exporter.setWorkspace(workspace);
		try {
			ec.exportFile(new File("visible_graph.gexf"), exporter);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

		// Export to Writer
		Exporter exporterGraphML = ec.getExporter("graphml"); // Get GraphML exporter
		exporterGraphML.setWorkspace(workspace);
		StringWriter stringWriter = new StringWriter();
		ec.exportWriter(stringWriter, (CharacterExporter) exporterGraphML);
		// System.out.println(stringWriter.toString()); //Uncomment this line
		 *
		 */

	}

	public static void main(String[] args) {
		if(args.length<1){
			System.out.println(">java -jar gexf_layout.jar example.gexf");
			System.exit(0);
		}
		ImportExport run = new ImportExport();
		run.script((String)args[0]);

	}
	

	/**YifanHuMultiLevel
	 * @param Quadtree Max Level 10, 
	 * Theta 1.2, 
	 * Minimum level size 3,
	 * Minimum coarsening rate 0.75, 
	 *  Step ratio 0.97, 
	 *  Optimal Distance 100
	 */
	/**ForceAtlas2
	Threads=2, 
	Prevent Overlap = false, 
	Edge Weight Influence 1.0, 
	Scaling 2.0, 
	Gravity 1.0, 
	Tolerance 0.1, 
	Approximate Repulsion = true, 
	Approximation 1.2 
	**/
	public void panacondaLayout(GraphModel graphModel) {
		
		//YifanHuMultiLevel yfumulti=new YifanHuMultiLevel();
		//Layout firstlayout = yfumulti.buildLayout();
		
		
		YifanHuMultiLevel yfumulti = new YifanHuMultiLevel();
		MaximalMatchingCoarsening coarsening=new MaximalMatchingCoarsening();
		MultiLevelLayout firstlayout=new MultiLevelLayout(yfumulti, coarsening);
		firstlayout.setGraphModel(graphModel);
		firstlayout.resetPropertiesValues();
		//firstlayout.setGraphModel(graphModel);
		firstlayout.setQuadTreeMaxLevel(20);
		firstlayout.setBarnesHutTheta(1.2f);
		firstlayout.setMinSize(3);
		firstlayout.setMinCoarseningRate(0.99d);
		firstlayout.setStepRatio(0.97f);
		firstlayout.setOptimalDistance(100f);
		LayoutProperty stuff[] = firstlayout.getProperties();
		//firstlayout.initAlgo();
		//firstlayout.goAlgo();
		//firstlayout.endAlgo();
		
		
		/*ForceAtlas2 secondlayout= new ForceAtlas2(null);
		secondlayout.setThreadsCount(4);
		//unknown how to prevent overlap
		secondlayout.resetPropertiesValues();
		secondlayout.setEdgeWeightInfluence(1.0);
		secondlayout.setScalingRatio(2.0);
		secondlayout.setGravity(1.0);
		//unknown how to set tolerance
		secondlayout.setBarnesHutOptimize(true);
		secondlayout.setBarnesHutTheta(1.2);*/
		AutoLayout autolayout = new AutoLayout(30, TimeUnit.SECONDS);
		autolayout.setGraphModel(graphModel);
		autolayout.addLayout(firstlayout, 1f);
		//autolayout.addLayout(secondlayout, 1.0f);
		autolayout.execute();
		firstlayout.endAlgo();
		
		
		
	
		
		/*if (firstlayout.canAlgo()) {
			firstlayout.initAlgo();
			int c = 1;
			while (c <= 20) {
				firstlayout.goAlgo();
				c++;
			}
			firstlayout.endAlgo();
			
		}*/
	}

	
	/*public void yifanHuLayout(GraphModel graphModel){ 
		YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		layout.setGraphModel(graphModel);
		layout.resetPropertiesValues();
		layout.setQuadTreeMaxLevel(10);
		layout.setBarnesHutTheta(1.2f);
		// layout.setInitialStep(3f);
		layout.setStepRatio(0.77f);
		layout.setOptimalDistance(100f);
		layout.initAlgo();
		for (int i = 0; i < 10 && layout.canAlgo(); i++) {
			layout.goAlgo();
		}
		layout.endAlgo();
	}*/
	
	/**
	Threads=2, 
	Prevent Overlap = false, 
	Edge Weight Influence 1.0, 
	Scaling 2.0, 
	Gravity 1.0, 
	Tolerance 0.1, 
	Approximate Repulsion = true, 
	Approximation 1.2 
	**/
	public void forceAtlas2A(GraphModel graphModel){
		ForceAtlas2Builder builder=new ForceAtlas2Builder();
		ForceAtlas2 fAtlas=new ForceAtlas2(builder);
		fAtlas.setGraphModel(graphModel);
		fAtlas.resetPropertiesValues();
		fAtlas.setAdjustSizes(true);
		fAtlas.setThreadsCount(2);
		fAtlas.setOutboundAttractionDistribution(false);
		fAtlas.setEdgeWeightInfluence(1.0);
		fAtlas.setScalingRatio(2.0);
		fAtlas.setGravity(1.0);
		fAtlas.setJitterTolerance(0.1);
		fAtlas.initAlgo();
		if(fAtlas.canAlgo()) {
			fAtlas.goAlgo();
		}
		fAtlas.endAlgo();
	}
	
	

	/**
	Threads 2, 
	Prevent Overlap = true, 
	Edge Weight Influence 1.0, 
	Scaling 2.0, 
	Gravity 1.0, 
	Tolerance 0.1, 
	Approximate Repulsion = false,
	Approximation 1.2
	**/
	public void forceAtlas2B(GraphModel graphModel){
		ForceAtlas2Builder builder=new ForceAtlas2Builder();
		ForceAtlas2 fAtlas=new ForceAtlas2(builder);
		fAtlas.setGraphModel(graphModel);
		fAtlas.resetPropertiesValues();
		fAtlas.setAdjustSizes(true);
		fAtlas.setThreadsCount(2);
		fAtlas.setOutboundAttractionDistribution(true);
		fAtlas.setGravity(1.0);
		fAtlas.setEdgeWeightInfluence(1.0);
		fAtlas.setScalingRatio(2.0);
		fAtlas.setJitterTolerance(0.1);
		fAtlas.initAlgo();
		if(fAtlas.canAlgo()) {
			fAtlas.goAlgo();
		}
		fAtlas.endAlgo();
	}
	
	
	public void testFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		int c = 1;
		while (line != null && c < 10) {
			System.out.println(line);
			line = br.readLine();
			c++;
		}
		fr.close();
		br.close();
	}

}
