/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/
package gov.nasa.worldwindx.examples.analytics;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.ExampleUtil;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Illustrates how to configure and display a 3D geographic grid of scalar data
 * using the World Wind <code>{@link
 * AnalyticSurface}</code>. Analytic surface defines a grid over a geographic
 * <code>{@link Sector}</code> at a specified altitude, and enables the caller
 * to specify the color and height at each grid point.
 * <p/>
 * This illustrates three key AnalyticSurface configurations:
 * <ul>
 * <li>Displaying a static data set where each grid point uses color and height
 * to indicate the data's magnitude.</li>
 * <li>Displaying data that varies by color over time on the terrain
 * surface.</li>
 * <li>Displaying data that varies by color and height over time at a specified
 * altitude.</li>
 * </ul>
 *
 * @author dcollins
 * @version $Id$
 */
public class AnalyticSurfaceDemo2 extends ApplicationTemplate {
	static Variable discharge;
	// protected static final String DATA_PATH =
	// "gov/nasa/worldwindx/examples/data/wa-precip-24hmam-5km.tif";

	public static class AppFrame extends ApplicationTemplate.AppFrame {

		protected static final double HUE_RED = 1;
		protected RenderableLayer analyticSurfaceLayer;
		static double[] logs;
		static double[] lats;

		public AppFrame() throws IOException, InvalidRangeException {
		 //     getWwd().setView(new BasicFlyView());
			this.initAnalyticSurfaceLayer();
		}

		protected void initAnalyticSurfaceLayer() throws IOException, InvalidRangeException {
			this.analyticSurfaceLayer = new RenderableLayer();
			this.analyticSurfaceLayer.setPickEnabled(false);
			this.analyticSurfaceLayer.setName("Analytic Surfaces");
			insertBeforePlacenames(this.getWwd(), this.analyticSurfaceLayer);
			this.getLayerPanel().update(this.getWwd());

			try {

				String workDir = "C:\\Users\\yuanchunyu\\Documents\\Discharge_NCEP_Pristine_dTS2004.nc";
				// Reading netcdf run file
				// Path runPath = runFile.getParentFile().toPath();
				NetcdfDataset netcdfRunFileDataset = new NetcdfDataset(NetcdfDataset.openFile(workDir, null));

				discharge = netcdfRunFileDataset.findVariable("discharge");
				Variable lat = netcdfRunFileDataset.findVariable("latitude");
				Variable lon = netcdfRunFileDataset.findVariable("longitude");
				// ArrayList<String>location=new ArrayList<>();
				Array la = lat.read();
				lats = (double[]) la.copyTo1DJavaArray();

				Array lo = lon.read();
				logs = (double[]) lo.copyTo1DJavaArray();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// createRandomAltitudeSurface(HUE_BLUE, HUE_RED, 40, 40,
			// this.analyticSurfaceLayer);
			createRandomColorSurface(1, HUE_RED, 1720, 1120, this.analyticSurfaceLayer);

			// Load the static precipitation data. Since it comes over the
			// network, load it in a separate thread to
			// avoid blocking the example if the load is slow or fails.
			/*
			 * Thread t = new Thread(new Runnable() { public void run() {
			 * createPrecipitationSurface(HUE_BLUE, HUE_RED,
			 * analyticSurfaceLayer); } }); t.start();
			 */
		}
	}
/*
	protected static Renderable createLegendRenderable(final AnalyticSurface surface, final double surfaceMinScreenSize,
			final AnalyticSurfaceLegend legend) {
		return new Renderable() {
			public void render(DrawContext dc) {
				Extent extent = surface.getExtent(dc);
				if (!extent.intersects(dc.getView().getFrustumInModelCoordinates()))
					return;

				if (WWMath.computeSizeInWindowCoordinates(dc, extent) < surfaceMinScreenSize)
					return;

				legend.render(dc);
			}
		};
	}*/

	protected static void createRandomColorSurface(double minHue, double maxHue, int width, int height,
			RenderableLayer outLayer) throws IOException, InvalidRangeException {
		/*
		 * double minValue = -200e3; double maxValue = 200e3;
		 */
		double minValue = -20;
		double maxValue = 20;

		AnalyticSurface surface = new AnalyticSurface();
		surface.setSector(Sector.fromDegrees(5.0, 60.999999999999105, -138, -52.00000000000138));
		surface.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
		surface.setDimensions(width, height);
		surface.setClientLayer(outLayer);
		outLayer.addRenderable(surface);

		// BufferWrapper firstBuffer = randomGridValues(width, height, minValue,
		// maxValue);
		BufferFactory te = new BufferFactory.DoubleBufferFactory();
		BufferWrapper firstBuffer = te.newBuffer(1926400);
		Array x = discharge.read("1,0:1119:1,0:1719:1");
		// double[] values = (double[]) x.copyTo1DJavaArray();//new
		// double[1926400];
		double[] values = new double[1926400];
		double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
		// System.out.println(x.getSize());

		// this. positions = new ArrayList<Position>();
		// int coun=0;
		// long a=System.currentTimeMillis();
		int c = 0;
		for (int i = 1119; i >= 0; i--) {
			for (int j = 0; j <= 1719; j++) {

				if (Double.isNaN(floatValues[0][i][j])) {
					// continue;
					values[c++] = -1;
				} else {
					values[c++] = floatValues[0][i][j];

					// else
					// this. positions.add(new
					// Position(Angle.fromDegrees(lats[i]),
					// Angle.fromDegrees(logs[j]),10));
					// System.out.println(floatValues[0][i][j]);
				}

				// System.out.println(floatValues[0][i][j]);
				// coun+=1;
			}
		}

		// for(int i=0;i<1926400;i++){
		// values[i]=1;
		// }
		firstBuffer.putDouble(0, values, 0, 1926400);
		// BufferWrapper secondBuffer = randomGridValues(width, height, minValue
		// * 2d, maxValue / 2d);
		System.out.println(firstBuffer.length());
		// for(int i=0;i<firstBuffer.length();i++){
		// System.out.println(firstBuffer.getDouble(i));

		// }
		// mixValuesOverTime(2000L, firstBuffer, secondBuffer, minValue,
		// maxValue, minHue, maxHue, surface);
		surface.setValues(
				createMixedColorGradientGridValues(1, x, minValue, maxValue, minHue, maxHue));

		AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
		attr.setDrawShadow(false);
	//	attr.setShadowOpacity(1);
		attr.setInteriorOpacity(0.7);
	attr.setInteriorMaterial(Material.BLACK);
		attr.setOutlineOpacity(0);
		attr.setOutlineWidth(0);
		surface.setSurfaceAttributes(attr);
		 surface.setVerticalScale(0);
	}

	public static Iterable<? extends AnalyticSurface.GridPointAttributes> createMixedColorGradientGridValues(double a,
			Array x, double minValue, double maxValue, double minHue,
			double maxHue) {
		ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();
		//double[] values = new double[1926400];
		double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
		// System.out.println(x.getSize());

		// this. positions = new ArrayList<Position>();
		// int coun=0;
		// long a=System.currentTimeMillis();

		for (int i = 1119; i >= 0; i--) {
			for (int j = 0; j <= 1719; j++) {
				double value = -200;
				if (floatValues[0][i][j]> 1) {
					// continue;
					value = floatValues[0][i][j];
					//value =  WWMath.mixSmooth(a, floatValues[0][i][j],
							//floatValues[0][i][j]);
				} 
				attributesList
				.add(AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
				// System.out.println(floatValues[0][i][j]);
				// coun+=1;
			}
		}

		/*long length = Math.min(firstBuffer.length(), secondBuffer.length());
		for (int i = 0; i < length; i++) {
			double value = -200;
			if (firstBuffer.getDouble(i) > 20)
				value = 1;// WWMath.mixSmooth(a, firstBuffer.getDouble(i),
							// secondBuffer.getDouble(i));
			// System.out.println(firstBuffer.getDouble(i));
			attributesList
					.add(AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
		}
*/
		return attributesList;
	}

	public static void main(String[] args) {
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        

		ApplicationTemplate.start("World Wind Analytic Surface", AppFrame.class);
	}
}