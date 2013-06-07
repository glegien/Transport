package pl.edu.agh.cs.kraksim.sna;

import java.util.Properties;

import pl.edu.agh.cs.kraksim.KraksimConfigurator;

/**
 * Klasa pomocnicza dla konfiguracji algorytmu opartego na SNA
 *
 */
public class SnaConfigurator {
	private static final String SNA_ENABLED = "snaEnabled";
	private static final String SNA_CLUSTERS = "snaClusters";
	private static final String SNA_REFRESH_INTERVAL = "snaRefreshInterval";

	private static final Boolean SNA_ENABLED_DEFAULT = false;
	private static final Integer SNA_CLUSTERS_DEFAULT = 4;
	private static final Integer SNA_REFRESH_INTERVAL_DEFAULT = 100;

	private static Properties cachedProps = null;

	private static Properties getProps() {
		if (cachedProps == null) {
			cachedProps = KraksimConfigurator.getPropertiesFromFile();
		}
		return cachedProps;
	}

	public static Boolean getSnaEnabled() {
		try {
			String enabled = getProps().getProperty(SNA_ENABLED);
			return enabled != null ? Boolean.parseBoolean(enabled): SNA_ENABLED_DEFAULT;
		} catch (Exception e) {
			return SNA_ENABLED_DEFAULT;
		}
	}
	
	public static Integer getSnaClusters() {
		try {
			String clusters = getProps().getProperty(SNA_CLUSTERS);
			return clusters != null ? Integer.parseInt(clusters) : SNA_CLUSTERS_DEFAULT;
		} catch (Exception e) {
			return SNA_CLUSTERS_DEFAULT;
		}
	}
	
	public static Integer getSnaRefreshInterval() {
		try {
			String interval = getProps().getProperty(SNA_REFRESH_INTERVAL);
			return interval != null ? Integer.parseInt(interval) : SNA_REFRESH_INTERVAL_DEFAULT;
		} catch (Exception e) {
			return SNA_REFRESH_INTERVAL_DEFAULT;
		}
	}
}
