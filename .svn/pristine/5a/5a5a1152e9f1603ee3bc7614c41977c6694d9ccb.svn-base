package pl.edu.agh.cs.kraksim.weka.statistics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;

import jxl.Workbook;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class PredictionsToExcel {
	private static final Logger logger = Logger.getLogger(PredictionsToExcel.class);
	private String trafficFileName;
	
	public PredictionsToExcel(PredictionSetup setup) {
		trafficFileName = setup.getOutputMainFolder();
	}
	
	public void writeToExcel(int actualTurn, Archive<Double> classData, Archive<Double> classDataPrediction) {
		String folderPath = "excel"; 
		String filePath = folderPath + File.separator + trafficFileName + actualTurn + "_prediction.xls";
		logger.debug("Create folder");
		File folder = new File(folderPath);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(new File(filePath));
			logger.debug("Write fields");
			writeFields(workbook, actualTurn, classData, classDataPrediction);

			workbook.write();
			workbook.close();
			logger.debug("Workbook closed");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	private void writeFields(WritableWorkbook workBook, int actualTurn, Archive<Double> classData, Archive<Double> classDataPrediction) throws RowsExceededException, WriteException {
		SortedSet<Integer> turns = new TreeSet<Integer>(classDataPrediction.getTurns());
		List<Double> c = classData.getCongestionListByTurn(turns.first());
		WritableSheet[] sheetTable = new WritableSheet[c.size()];
		for(int i = 0; i < c.size(); i++) {
			sheetTable[i] = (WritableSheet) workBook.createSheet("" + i, i);
		}
		int j = 0;
		for (Integer turn : turns) {
			if (turn < actualTurn) {
				List<Double> congestionList = classData.getCongestionListByTurn(turn);
				List<Double> predictionList = classDataPrediction.getCongestionListByTurn(turn);
				for (int i = 0; i < congestionList.size(); i++) {
					Double realValue = congestionList.get(i);
					Double predictedValue = predictionList.get(i);
					
					WritableSheet sheet = sheetTable[i];
					Number number = new Number(0, j, turn);
					sheet.addCell(number);
					Number number2 = new Number(1, j, realValue);
					sheet.addCell(number2);
					Number number3 = new Number(2, j, predictedValue);
					sheet.addCell(number3);
				}
			}
			j++;
		}
	}

}
