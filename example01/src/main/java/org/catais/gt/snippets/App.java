package org.catais.gt.snippets;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

/**
 * Hello world!
 *
 */
public class App 
{	
    public static void main( String[] args ) throws IOException
    {
    	final String fileName = "/Users/stefan/Downloads/dom2014_607228_50cm.tif";
   	
    	long startTime;
    	long endTime;
    	
    	startTime = System.currentTimeMillis();
    	
    	File file = new File(fileName);

    	AbstractGridFormat format = GridFormatFinder.findFormat(file);
    	GridCoverage2DReader reader = format.getReader(file);
    	
//    	System.out.println(reader.getDatasetLayout().getNumInternalOverviews());
//    	System.out.println(reader.getCoordinateReferenceSystem());
//    	System.out.println(reader.getFormat());
//    	System.out.println(reader.getOriginalEnvelope());
//    	System.out.println(reader.getOriginalGridRange());
    	
        ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);

        //this will basically read 4 tiles worth of data at once from the disk...
        ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();

        //Setting read type: use JAI ImageRead (true) or ImageReaders read methods (false)
        ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);
    	
        GridCoverage2D coverage = reader.read(new GeneralParameterValue[]{policy, gridsize, useJaiRead});
        
        endTime = System.currentTimeMillis();
        System.out.println("Opening GeoTIFF took " + (endTime - startTime) + " milliseconds");
        
        double x = coverage.getEnvelope2D().x;
        double y = coverage.getEnvelope2D().y;
        double width = coverage.getEnvelope2D().width;
        double height = coverage.getEnvelope2D().height;
        double resX = reader.getResolutionLevels()[0][0];
        double resY = reader.getResolutionLevels()[0][1];
        
        double offsetX = resX/2;
        double offsetY = resY/2;
                
        for (double i = x + offsetX; i < x + width; i = i + resX) {
        	
        	for (double j = y + offsetY; j < y + height; j = j + resY) {
//            	System.out.println(i + " / " + j);
        		
        		Point2D point = new Point2D.Double(i, j);
        		double[] res = new double[1];
        		coverage.evaluate(point, res);

//        		System.out.println(res[0]);
        	}
        }
        endTime = System.currentTimeMillis();
        System.out.println("Reading all values from GeoTIFF took " + (endTime - startTime) + " milliseconds");
    }
}
