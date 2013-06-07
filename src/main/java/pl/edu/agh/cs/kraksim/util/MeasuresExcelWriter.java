package pl.edu.agh.cs.kraksim.util;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.sna.centrality.CentrallityCalculator;
import pl.edu.agh.cs.kraksim.sna.centrality.MeasureType;

public class MeasuresExcelWriter {
	private static final String DEFAULT_DIR = ".\\measures\\";
	private static final String DEFAULT_FILENAME = "measures";
	private static final int DEFAULT_ITERS = 30;
	
	private int iters;
	
	private int currIter = 1;
	private boolean done = false;
	
	private HSSFWorkbook workbook;
	private Map<Node, List<Double>> nodesMeasures = new HashMap<Node, List<Double>>();
	private Map<Node, Integer> topTenAppearance = new HashMap<Node, Integer>();
	
	
	public MeasuresExcelWriter(){
		init(DEFAULT_ITERS);
	}
	
	public MeasuresExcelWriter(int iters){
		init(iters);
	}

	
	public void persistIteration(List<Node> nodes){
		if(done){
			return;
		}
		addToNodesMeasures(nodes);
		addToTopTenAppereances(nodes);
		HSSFSheet sheet = createSheet();
		addHeader(sheet);
		for(int i=0; i<nodes.size(); i++){
			Node n = nodes.get(i);
			addRow(sheet, i+1, i+1, n.getId(), n.getMeasure());
		}
		nextIter();
	}
	
	private HSSFSheet createSheet(){
		return workbook.createSheet("Iteration " + currIter);
	}
	
	private void addHeader(HSSFSheet sheet){
		HSSFRow row = sheet.createRow(0);
		addHeaderCell(row, 0, "Rank");
		addHeaderCell(row, 1, "Node");
		addHeaderCell(row, 2, "Value");
	}
	
	private void addHeaderCell(HSSFRow row, int pos, String value){
		HSSFCell cell = row.createCell(pos);
		cell.setCellValue(value);
		setHeaderStyle(cell);
	}
	
	private void setHeaderStyle(HSSFCell cell){
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		cell.setCellStyle(style);
	}
	
	private void addRow(HSSFSheet sheet, int pos, int rank, String node, double value){
		HSSFRow row = sheet.createRow(pos);
		
		HSSFCell rankCell = row.createCell(0);
		rankCell.setCellValue(rank);
		
		HSSFCell nodeCell = row.createCell(1);
		nodeCell.setCellValue(node);
		
		HSSFCell valueCell = row.createCell(2);
		valueCell.setCellValue(value);
		
		if(rank <= 10){
			setTopTenStyle(rankCell, nodeCell, valueCell);
		}
	}
	
	private void setTopTenStyle(HSSFCell ... cells){
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);
		for(HSSFCell c : cells){
			c.setCellStyle(style);
		}
	}
	
	private void nextIter(){
		currIter++;
		if(currIter > iters){
			persistNodesMeasures();
			persistTopTenAppereances();
			writeToFile();
			done = true;
		}
	}
	
	private void writeToFile(){
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(getFileName());
			workbook.write(fos);
			fos.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(fos);
		}
	}
	
	private void init(int iters){
		this.iters = iters;
		workbook = new HSSFWorkbook();
	}
	
	private void addToTopTenAppereances(List<Node> nodes){
		for(int i=0; i<10 && i<nodes.size(); i++){
			Node n = nodes.get(i);
			if(topTenAppearance.get(n) == null){
				topTenAppearance.put(n, new Integer(1));
			}else{
				Integer apps = topTenAppearance.get(n);
				apps++;
				topTenAppearance.remove(n);
				topTenAppearance.put(n, apps);
			}
		}
	}
	
	private void addToNodesMeasures(List<Node> nodes){
		for(Node n : nodes){
			List<Double> measures = nodesMeasures.get(n);
			if(measures == null){
				measures = new ArrayList<Double>();
				nodesMeasures.put(n, measures);
			}
			measures.add(n.getMeasure());
		}
	}
	
	private void persistTopTenAppereances(){
		HSSFSheet sheet = workbook.createSheet("Top ten appereances");
		HSSFRow header = sheet.createRow(0);
		HSSFCell cell = header.createCell(0);
		cell.setCellValue("Node");
		cell = header.createCell(1);
		cell.setCellValue("Appereances");
		
		int rowNr = 1;
		for(Node n : topTenAppearance.keySet()){
			Integer apps = topTenAppearance.get(n);
			HSSFRow row = sheet.createRow(rowNr);
			cell = row.createCell(0);
			cell.setCellValue(n.getId());
			cell = row.createCell(1);
			cell.setCellValue(apps);
			rowNr++;
		}
	}
	
	private void persistNodesMeasures(){
		List<Node> nodes = new ArrayList<Node>(nodesMeasures.keySet());
		
		HSSFSheet sheet = workbook.createSheet("Nodes measures");
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCell cell = headerRow.createCell(0);
		cell.setCellValue("Node/Iter");
		
		List<HSSFRow> rows = new LinkedList<HSSFRow>();
		//pierwsza kolumna
		for(int i=0; i<nodes.size(); i++){
			HSSFRow row = sheet.createRow(i+1);
			rows.add(row);
			cell = row.createCell(0);
			cell.setCellValue(nodes.get(i).getId());
		}
		
		// nag��wek
		for(int i=0; i<nodesMeasures.get(nodes.get(0)).size(); i++){
			cell = headerRow.createCell(i+1);
			cell.setCellValue((i+1));
		}
		
		// dane
		int rowNr = 0;
		for(Node n : nodes){
			HSSFRow row = rows.get(rowNr);
			int cellNr = 1;
			for(Double m : nodesMeasures.get(n)){
				cell = row.createCell(cellNr);
				cell.setCellValue(m);
				cellNr++;
			}
			rowNr++;
		}
	}
	
	private String getFileName(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		MeasureType mType = CentrallityCalculator.measureType;
		return String.format("%s%s_%s_%s.xls", DEFAULT_DIR, DEFAULT_FILENAME, format.format(new Date()), mType.toString());
	}
}
