package pl.edu.agh.cs.kraksim.traffic;

import java.awt.Color;
import java.util.NoSuchElementException;
import java.util.Random;

import pl.edu.agh.cs.kraksim.core.Gateway;

public class TravellingScheme {

	private int count;
	private Gateway[] gateways;
	private Distribution[] departureDists;
	private Color driverColor;

	/**
	 * @param count
	 *            liczba samochodow, ktorych dotyczy schemat
	 * @param gateways
	 *            tablica wezlow schematu
	 * @param departureDists
	 *            tablica opisujaca rozklady p-stwa czasu odjazdu z węzłów;
	 *            gateways.length == departureDists.length + 1 (z ostatniego
	 *            węzła nie ma odjazdu)
	 */
	public TravellingScheme(int count, Gateway[] gateways,
			Distribution[] departureDists, Color driverColor) throws IllegalArgumentException {
		
	    if (gateways.length < 2)
			throw new IllegalArgumentException(
					"There should be at least two gateways in travelling scheme");
		if (gateways.length != departureDists.length + 1)
			throw new IllegalArgumentException(
					"There should be one gateway more than departure distributions");
		this.count = count;
		this.gateways = gateways;
		this.departureDists = departureDists;
		this.driverColor = driverColor;
	}

	public int getCount() {
		return count;
	}

	public Cursor cursor() {
		return new Cursor();
	}

	public class Cursor {
		int i;

		public boolean isValid() {
			return i < gateways.length - 1;
		}

		public Gateway srcGateway() throws NoSuchElementException {
			if (i >= gateways.length - 1)
				throw new NoSuchElementException();

			return gateways[i];
		}

		public Gateway destGateway() throws NoSuchElementException {
			if (i >= gateways.length - 1)
				throw new NoSuchElementException();

			return gateways[i + 1];
		}

		public int drawDepartureTurn(Random rg) throws NoSuchElementException {
			if (i >= gateways.length - 1)
				throw new NoSuchElementException();
			return (int) departureDists[i].draw(rg);
		}

		public void next() {
			i++;
		}

		public void rewind() {
			i = 0;
		}
	}

    public Color getDriverColor() {
        return driverColor;
    }

}
