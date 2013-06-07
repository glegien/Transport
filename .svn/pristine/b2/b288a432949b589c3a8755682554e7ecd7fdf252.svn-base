package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.History;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.Transaction;
import pl.edu.agh.cs.kraksim.weka.data.TransactionTable;
import pl.edu.agh.cs.kraksim.weka.utils.TransactionCreator;
import weka.core.Instance;

public class ClassificationTransactionCreator extends TransactionCreator {
	private static final Logger logger = Logger.getLogger(ClassificationTransactionCreator.class);

	public ClassificationTransactionCreator(PredictionSetup setup) {
		super(setup);
	}

	public Transaction createTestTransaction(History historyArchive, LinkInfo classRoad) {
		ArrayList<Double> attributeValues = new ArrayList<Double>();
		double valueForClassAttribue = Instance.missingValue(); 
		attributeValues.add(valueForClassAttribue);
		int historyDepth = setup.getMaxNumberOfInfluencedTimesteps() - setup.getMinNumberOfInfluencedTimesteps() + 1;
		addNoClassAttributeValues(historyArchive, classRoad, attributeValues, 0, historyDepth);
		
		logger.debug("Test transaction: " + attributeValues);
		return new Transaction(attributeValues);
	}
}
