package pl.edu.agh.cs.kraksim.weka.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;


public class ResultWriter {
	private static final Logger logger = Logger.getLogger(ResultWriter.class);
	private final Archive<Boolean> congestionsArchive;
	private final PredictionArchive predictionsArchive;
	private final PredictionSetup setup;

	public ResultWriter(PredictionSetup setup, Archive<Boolean> congestionsArchive,
			PredictionArchive predictionsArchive) {
				this.setup = setup;
				this.congestionsArchive = congestionsArchive;
				this.predictionsArchive = predictionsArchive;
	}

	public void writeResult(String result) {
		logger.info(result);
		writeToFile("result.txt", result);
		writeDurationLevelArchiveToFile();
		writePredictionArchiveToFile();
	}
	
	private void writeDurationLevelArchiveToFile() {
		writeToFile("archive.txt", congestionsArchive.toString());
	}

	private void writePredictionArchiveToFile() {
		writeToFile("predictions.txt", predictionsArchive.toString());
	}

	private void writeToFile(String fileName, String text) {
		String path = createFolderPath();
		String filePath = path + File.separator + fileName;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(text);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String createFolderPath() {
		String dir = "output" + File.separator + setup.getOutputMainFolder()
				+ File.separator;
		File folder = new File(dir);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		return dir;
	}
}
