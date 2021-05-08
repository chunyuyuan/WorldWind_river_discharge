/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwindx.examples.util.SectorSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;

import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.units.DateUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Demonstrates how to use the
 * {@link gov.nasa.worldwindx.examples.util.SectorSelector} utility.
 *
 * @author tag
 * @version $Id: SectorSelection.java 2109 2014-06-30 16:52:38Z tgaskins $
 */
public class SectorSelection2 extends ApplicationTemplate {
	public static class AppFrame extends ApplicationTemplate.AppFrame {
		private SectorSelector selector;
		private static final int N = 128;
		private static final Random random = new Random();
		private int n = 1;
		private static int check = 1;
		static double[] logs;
		static double[] lats;
		static Variable discharge;
		static List<LatLon> location;

		public AppFrame() {
			super(false, false, false);

			this.selector = new SectorSelector(getWwd());
			this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
			this.selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
			this.selector.setBorderWidth(3);

			String workDir = "C:\\Users\\cyuan5\\Documents\\Discharge_NCEP_Pristine_dTS2004.nc";
			// Reading netcdf run file
			// Path runPath = runFile.getParentFile().toPath();
			try {
				NetcdfDataset netcdfRunFileDataset = new NetcdfDataset(NetcdfDataset.openFile(workDir, null));

				discharge = netcdfRunFileDataset.findVariable("discharge");
				Variable lat = netcdfRunFileDataset.findVariable("latitude");
				Variable lon = netcdfRunFileDataset.findVariable("longitude");
				// ArrayList<String>location=new ArrayList<>();
				Array la= lat.read();
				lats = (double[]) la.copyTo1DJavaArray();

				Array lo = lon.read();
				logs = (double[]) lo.copyTo1DJavaArray();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JPanel p = new JPanel(new BorderLayout(5, 5));

			p.add(createPane(), BorderLayout.SOUTH);

			p.setVisible(true);
			this.add(p, BorderLayout.SOUTH);
			location=new ArrayList<LatLon>();
			location.add(new LatLon(Angle.fromDegrees(0), Angle.fromDegrees(0)));
			location.add(new LatLon(Angle.fromDegrees(0), Angle.fromDegrees(0)));
			location.add(new LatLon(Angle.fromDegrees(0), Angle.fromDegrees(0)));
			location.add(new LatLon(Angle.fromDegrees(0), Angle.fromDegrees(0)));
			this.getWwd().getInputHandler().addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {

				}

				public void keyReleased(KeyEvent e) {

				}

				@Override
				public void keyTyped(KeyEvent e) {

					if ((e.getKeyChar() - '0') == 1) {
						selector.enable();

					} else if ((e.getKeyChar() - '0') == 2) {
						check = 0;
						selector.disable();
						// System.out.println(location.size());
						// p.get
					}
					// TODO Auto-generated method stub

				}
			});

			this.selector.addPropertyChangeListener(SectorSelector.SECTOR_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {

					Sector sector = (Sector) evt.getNewValue();
					// sector.
					if (selector.getSector() != null) {
						location = selector.getSector().asList();

					} else {
						selector.disable();
						check = 1;
						System.out.println("???????????????????????????????????");
						System.out.println(sector != null ? sector : "no sector");
					}
				}
			});
		}

		private ChartPanel createPane() {
			final XYSeries series = new XYSeries("Data");

			for (int i = 0; i < 366; i++) {
				series.add(i+1, 0);
			}
			XYSeriesCollection dataset = new XYSeriesCollection(series);

			JFreeChart chart = ChartFactory.createXYLineChart("", "Day Time", "Discharge", dataset,
					PlotOrientation.VERTICAL, false, false, false);
			// chart.
			Range a = new Range(0, 366);
			XYPlot xyPlot = (XYPlot) chart.getPlot();
			// xyPlot.setDomainCrosshairVisible(true);
			// xyPlot.setRangeCrosshairVisible(true);
			XYItemRenderer renderer = xyPlot.getRenderer();
			ValueAxis domain = xyPlot.getDomainAxis();

			domain.setRange(0, 366);
			new Timer(500, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// System.out.println("check+ "+check);
					if (check == 0) {
						   try {
						// double minla=Double.MAX_VALUE;
					/*	for (LatLon t : location) {
							System.out.println(t.getLongitude().degrees);
						}*/
						double minla=location.get(0).getLatitude().degrees;
						double maxla=location.get(1).getLatitude().degrees;
						double minlo=location.get(0).getLongitude().degrees;
						double maxlo=location.get(1).getLongitude().degrees;
						int minlaa=0,maxlaa=0,minloo=0,maxloo=0;
						 for(int i=0;i<logs.length;i++){
					        if(logs[i]>=minlo){
					        	minloo=i;
					        	break;
					        }
					        }
						 for(int i=0;i<logs.length;i++){
						        if(logs[i]>=maxlo){
						        	maxloo=i;
						        	break;
						        }
						        }
						 for(int i=0;i<lats.length;i++){
						        if(lats[i]>=minla){
						        	minlaa=i;
						        	break;
						        }
						        }
						 for(int i=0;i<lats.length;i++){
						        if(lats[i]>=maxla){
						        	maxlaa=i;
						        	break;
						        }
						        }
						
							Array x=discharge.read("0:365:1,"+minlaa+":"+maxlaa+":1,"+minloo+":"+maxloo+":1");
							 double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
							series.clear();
							for (int i = 0; i < 366; i++) {
								double sum=0;
								for(int j=0;j<floatValues[0].length;j++){
									for(int l=0;l<floatValues[0][0].length;l++){
										if(floatValues[i][j][l]>0){
											sum+=floatValues[i][j][l];
										}
									}	
								}
								series.add(i+1, sum);
							} // .add(series.getItemCount(), random.nextGaussian());
							check = 1;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InvalidRangeException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					
					}
				}
			}).start();
			// System.out.println(chart.getXYPlot().getDomainAxis().getLabel().length());
			// chart.getXYPlot().s
			return new ChartPanel(chart, false, true, true, true, true) {
				@Override
				public java.awt.Dimension getPreferredSize() {
					return new java.awt.Dimension(300, 240);
				}

				public double getScaleX() {
					return 366;
				}

			};

		}

	}

	public static void main(String[] args) {
		ApplicationTemplate.start("Discharge Map Tool", AppFrame.class);
	}
}
