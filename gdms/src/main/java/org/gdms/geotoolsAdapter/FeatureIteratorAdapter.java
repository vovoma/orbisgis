package org.gdms.geotoolsAdapter;

import java.util.NoSuchElementException;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;

public class FeatureIteratorAdapter implements FeatureIterator {

	private SpatialDataSource ds;

	private int index;

	public FeatureIteratorAdapter(SpatialDataSource ds) {
		index = 0;
		this.ds = ds;
	}

	public void remove() {
		throw new Error();
	}

	public void close() {

	}

	public boolean hasNext() {
		try {
			return index < ds.getRowCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Feature next() throws NoSuchElementException {
		index++;
		return new FeatureAdapter(ds, index - 1);
	}

}
