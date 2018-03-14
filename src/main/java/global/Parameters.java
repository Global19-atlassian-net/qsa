package global;

import algorithm.scoring.ScoreParameters;
import global.io.LineFile;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author Antonin Pavelka
 */
public class Parameters implements Serializable {

	private File file;
	// auto-initialized fields:
	private int maxDbSize;
	private int maxResidues;
	private int minResidues;
	private double continuousDistanceMin;
	private double continuousDistanceMax;
	private int wordLength;
	private double residueContactDistance;
	private int indexBins;
	private double atomContactDistance;
	private int skipX;
	private int skipY;
	private double maxFragmentRmsd;
	private double maxDeviation;
	private double avgDeviation;
	private double initialTmFilter;
	private double tmFilter;
	private double tmThresholdForRepresentants;
	private boolean visualize;
	private boolean visualizeBiwords;
	private boolean debug;
	private boolean displayFirstOnly;
	private boolean parallel;
	private double angleDifference;
	private double coordinateDifference;
	private double minComponentSize;
	private String referenceLengthType;
	private int customReferenceLength;
	private boolean hierarchicalSearch;

	private Parameters(File file) {
		this.file = file;
	}

	public static Parameters create(File file) {
		Parameters parameters = new Parameters(file);
		LineFile lineFile = new LineFile(file);
		Set<String> names = new HashSet<>();
		for (String line : lineFile.readLines()) {
			if (line.trim().isEmpty() || line.startsWith("#")) {
				continue;
			}
			StringTokenizer st = new StringTokenizer(line, " \t");
			String name = st.nextToken();
			String value = st.nextToken();
			parameters.initialize(name, value);
			names.add(name);
		}
		parameters.checkIfAllAreInitialized(names);
		return parameters;
	}

	private void initialize(String name, String value) {
		Class c = getClass();
		try {
			Field field = c.getDeclaredField(name);
			if (field.getType() == int.class) {
				int i = Integer.parseInt(value);
				field.setInt(this, i);
			} else if (field.getType() == double.class) {
				double d = Double.parseDouble(value);
				field.setDouble(this, d);
			} else if (field.getType() == boolean.class) {
				boolean b = Boolean.parseBoolean(value);
				field.setBoolean(this, b);
			} else if (field.getType() == String.class) {
				field.set(this, value);
			}
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}

	}

	public void checkIfAllAreInitialized(Set<String> names) {
		Class clazz = getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType() == File.class) {
				continue;
			}
			String name = field.getName();
			if (!names.contains(name)) {
				fieldNotInitialized(name);
			}
		}
	}

	private void fieldNotInitialized(String name) {
		throw new RuntimeException("Field " + name + " is not initialized in parameters file "
			+ file.getAbsolutePath());
	}

	public ScoreParameters getScorePars() {
		return new ScoreParameters();
	}

	public int getMaxResidues() {
		return maxResidues;
	}

	public int getMinResidues() {
		return minResidues;
	}

	public double getContinuousDistanceMin() {
		return continuousDistanceMin;
	}

	public double getContinuousDistanceMax() {
		return continuousDistanceMax;
	}

	public int getWordLength() {
		return wordLength;
	}

	public boolean isVisualize() {
		return visualize;
	}

	public boolean isVisualizeBiwords() {
		return visualizeBiwords;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isDisplayFirstOnly() {
		return displayFirstOnly;
	}

	public boolean isParallel() {
		return parallel;
	}

	public int getMaxDbSize() {
		return maxDbSize;
	}

	public double getResidueContactDistance() {
		return residueContactDistance;
	}

	public int getIndexBins() {
		return indexBins;
	}

	public double getAtomContactDistance() {
		return atomContactDistance;
	}

	@Deprecated
	public int getSkipX() {
		return skipX;
	}

	@Deprecated
	public int getSkipY() {
		return skipY;
	}

	public double getMaxFragmentRmsd() {
		return maxFragmentRmsd;
	}

	public double getMaxDeviation() {
		return maxDeviation;
	}

	public double getAvgDeviation() {
		return avgDeviation;
	}

	public double getTmThresholdForRepresentants() {
		return tmThresholdForRepresentants;
	}

	public double getTmFilter() {
		return tmFilter;
	}

	public double getInitialTmFilter() {
		return initialTmFilter;
	}

	/*public boolean hasExternalBiwordSource() {
		return externalBiwordSource != null && !"none".equals(externalBiwordSource);
	}*/

 /*public String getExternalBiwordSource() {
		return externalBiwordSource;
	}*/
	public double getMinComponentSize() {
		return minComponentSize;
	}

	public double getAngleDifference() {
		return angleDifference;
	}

	public double getCoordinateDifference() {
		return coordinateDifference;
	}

	public int getReferenceLength(int queryLength, int targetLength) {
		switch (referenceLengthType) {
			case "min":
				return Math.min(queryLength, targetLength);
			case "custom":
				return customReferenceLength;
			case "query":
				return queryLength;
			default:
				throw new RuntimeException(referenceLengthType);
		}
	}

	public boolean isHierarchicalSearch() {
		return hierarchicalSearch;
	}
}
