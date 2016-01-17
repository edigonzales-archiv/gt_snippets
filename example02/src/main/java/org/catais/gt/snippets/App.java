package org.catais.gt.snippets;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.media.jai.Interpolation;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.gce.imagemosaic.ImageMosaicFormat;
import org.geotools.gce.imagemosaic.ImageMosaicReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationBicubic2;;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	final String fileName = "/Users/stefan/tmp/dtm2014/dtm2014.shp"; //gdaltindex absolute path does not work?
       	
    	long startTime;
    	long endTime;
    	
    	startTime = System.currentTimeMillis();
    	
    	File file = new File(fileName);
    	
        ImageMosaicFormat format = new ImageMosaicFormat();
        ImageMosaicReader reader = format.getReader(file);
        
        endTime = System.currentTimeMillis();
        System.out.println("Opening Reader took " + (endTime - startTime) + " milliseconds");

//    	System.out.println(reader.getCoordinateReferenceSystem());
//    	System.out.println(reader.getFormat());
//    	System.out.println(reader.getOriginalEnvelope());
//    	System.out.println(reader.getOriginalGridRange());
    	
        ParameterValue<OverviewPolicy> policy = ImageMosaicFormat.OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);

        ParameterValue<String> gridsize = ImageMosaicFormat.SUGGESTED_TILE_SIZE.createValue();
        gridsize.setValue("512,512"); // best performance when size does match with blocksize of tiff?

        ParameterValue<Boolean> useJaiRead = ImageMosaicFormat.USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);
        
        ParameterValue<Integer> maxAllowedTiles = ImageMosaicFormat.MAX_ALLOWED_TILES.createValue();
        maxAllowedTiles.setValue(4); // There is no need for more tiles than four.
        
        ParameterValue<Boolean> allowMultithreading = ImageMosaicFormat.ALLOW_MULTITHREADING.createValue();
        allowMultithreading.setValue(true);

        ParameterValue<double[]> backgroundValues = ImageMosaicFormat.BACKGROUND_VALUES.createValue();
        double[] values = new double[1];
        values[0] = -9999.0; 
        backgroundValues.setValue(values);
        
        // Does not have any impact?!
        ParameterValue<Interpolation> interpolationMethod = ImageMosaicFormat.INTERPOLATION.createValue();
        interpolationMethod.setValue(new InterpolationBilinear());
        
		// limit yourself to reading just a bit of it
        
        CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
        
//        double x = 593500.4;
//        double y = 228500.7;
//        double x = 613001.4; // exception (da komplett ausserhalb)
//        double x = 2613000.4; // NPE
        double x = 613000.4; // -9999 da teilcoverage noch bissle mit daten.
        double y = 234500.0;

        
        double offset = 1;
        double lowerX = Math.round(x - offset);
        double upperX = Math.round(x + offset);
        double lowerY = Math.round(y - offset);
        double upperY = Math.round(y + offset);
        
        GeneralDirectPosition posLower = new GeneralDirectPosition(lowerX, lowerY);
        GeneralDirectPosition posUpper = new GeneralDirectPosition(upperX, upperY);
        
        double resX = reader.getResolutionLevels()[0][0];
        double resY = reader.getResolutionLevels()[0][1];

        int width = (int) ((upperX - lowerX) / resX);
        int height = (int) ((upperY - lowerY) / resY);
        
        
//        System.out.println(posLower);
//        System.out.println(posUpper);
//        System.out.println(width);
//        System.out.println(height);

        GridEnvelope2D gridEnvelope = new GridEnvelope2D(0, 0,   width,   height);
       
//        System.out.println(gridEnvelope);

        
        
//        DirectPosition lc = reader.getOriginalEnvelope().getLowerCorner();
//        DirectPosition uc = reader.getOriginalEnvelope().getUpperCorner();
//        double x = lc.getOrdinate(0);
//        double y = lc.getOrdinate(1);
//        double width = reader.getOriginalGridRange().getHigh(0) + 1;
//        double height = reader.getOriginalGridRange().getHigh(1) + 1;
        
        
//		ParameterValue<GridGeometry2D> gg =  AbstractGridFormat.READ_GRIDGEOMETRY2D.createValue();
//		GeneralEnvelope envelope = reader.getOriginalEnvelope();
//		Dimension dim = new Dimension();
//		dim.setSize(reader.getOriginalGridRange().getSpan(0)/2.0, reader.getOriginalGridRange().getSpan(1)/2.0);
//		Rectangle rasterArea = (( GridEnvelope2D)reader.getOriginalGridRange());
//		rasterArea.setSize(dim);
//		GridEnvelope2D range= new GridEnvelope2D(rasterArea);
		//gg.setValue(new GridGeometry2D(range,envelope));

		// Envelope
		GeneralEnvelope generalEnvelope = new GeneralEnvelope(posLower,  posUpper);
		generalEnvelope.setCoordinateReferenceSystem(crs);
//		System.out.println("generalEnvelope:" + generalEnvelope);
		
		ParameterValue<GridGeometry2D> gg =  AbstractGridFormat.READ_GRIDGEOMETRY2D.createValue();
		gg.setValue(new GridGeometry2D(gridEnvelope, generalEnvelope));
		
//        System.out.println(gg);
//        System.out.println(rasterArea);
//        System.out.println(envelope);
//        System.out.println("GridRange: " + reader.getOriginalGridRange());
//        System.out.println("Envelope: " +  reader.getOriginalEnvelope());
//        System.out.println("LowerCorner: " +  reader.getOriginalEnvelope().getLowerCorner());
//        System.out.println("UpperCorner: " +  reader.getOriginalEnvelope().getUpperCorner());
        
		
//      GridCoverage2D coverage = reader.read(new GeneralParameterValue[]{policy, gridsize, useJaiRead, maxAllowedTiles, allowMultithreading, backgroundValues});
//        GridCoverage2D coverage = reader.read(new GeneralParameterValue[]{gg, policy, gridsize, useJaiRead, maxAllowedTiles, allowMultithreading, backgroundValues, interpolationMethod});
        GridCoverage2D coverage = reader.read(new GeneralParameterValue[]{gg, policy, gridsize, useJaiRead, maxAllowedTiles, allowMultithreading, backgroundValues});
//        GridCoverage2D coverage = reader.read(new GeneralParameterValue[]{policy, gridsize, useJaiRead, allowMultithreading});
//        GridCoverage2D coverage = reader.read(null);

        // org.opengis.coverage.PointOutsideCoverageException:
//		Point2D point = new Point2D.Double(644454.0, 245490.0);
//		Point2D point = new Point2D.Double(606454, 227490);
//		double[] res = new double[1];
//		coverage.evaluate(point, res);
//		System.out.println(res[0]);
        
//      	DirectPosition2D pos = new DirectPosition2D(coverage.getCoordinateReferenceSystem(), x, y);
		Point2D point = new Point2D.Double(x, y);
		double[] res = new double[1];
		coverage.evaluate(point, res);
		System.out.println(res[0]);

      	
//      	float[] obj = (float[]) coverage.evaluate(pos);
//      	System.out.println(obj[0]);
		
//		System.out.println(coverage.getEnvelope2D());
//		System.out.println(coverage.getInterpolation());
//		System.out.println(coverage.getNumSampleDimensions());
//		System.out.println(coverage.getCoordinateReferenceSystem().getClass());
//		System.out.println(coverage.getProperties().get("OriginalFileSource"));
//		System.out.println(coverage.getProperties().keySet());
//		System.out.println(coverage.getProperties());

        endTime = System.currentTimeMillis();
        System.out.println("Reading value from ImageMosaic took " + (endTime - startTime) + " milliseconds");

        
        coverage = reader.read(new GeneralParameterValue[]{gg, policy, gridsize, useJaiRead, maxAllowedTiles, allowMultithreading, backgroundValues});
		point = new Point2D.Double(x, y);
		res = new double[1];
		coverage.evaluate(point, res);
		System.out.println(res[0]);

        endTime = System.currentTimeMillis();
        System.out.println("Reading value from ImageMosaic took " + (endTime - startTime) + " milliseconds");

        
        System.out.println( "Hello World!" );
    }
}
