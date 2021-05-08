/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package App;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import gov.nasa.worldwindx.examples.util.SectorSelector;
import gov.nasa.worldwindx.examples.util.ToolTipController;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.Wedge;
import gov.nasa.worldwind.util.BufferFactory;
import gov.nasa.worldwind.util.BufferWrapper;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;

import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.File;  
import java.io.InputStreamReader;  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileInputStream;  
import java.io.FileWriter;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.multiarray.MultiArray;
import ucar.netcdf.Attribute;
import ucar.netcdf.Netcdf;
import ucar.netcdf.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Draws a grid of points on the terrain. The points are evenly spaced throughout a region defined by a four sided
 * polygon.
 *
 * @author tag
 * @version $Id: GridOfPoints.java 2109 2014-06-30 16:52:38Z tgaskins $
 */
public class RiverDischarge extends ApplicationTemplate
{
    protected static final int NUM_POINTS_WIDE = 2000;
    protected static final int NUM_POINTS_HIGH = 2000;
	//static String fileName = "C:\\Users\\cyuan5\\Documents\\Discharge_NCEP_Pristine_dTS2004.nc";
	static double[]logs;
	static double[] lats;
	static Variable discharge;

	private static final int N = 128;
	private static final Random random = new Random();
	private int n = 1;

	
	
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
       // protected RenderableLayer layer; // layer to display the polygon and the intersection
      //  protected HashMap<Position, Object> positionInfo;
      //  protected PointGrid grid ;
     //   protected List<Position> positions;
       // protected List<Position> positions1;
       // protected List<Position> positions2;
      //  protected List<Position> corners;
	static List<LatLon> location;
		private SectorSelector selector;
		private static int check = 1;
		protected static final double HUE_RED = 1;
		private static int hintf = 1;
		private static int valuef = 1;
		//RenderableLayer analyticSurfaceLayer;
		RenderableLayer analyticSurfaceLayer;
		
		
        public AppFrame() throws IOException, InvalidRangeException
        {
       
            super(false, false, false);
         //  getWwd().setView(new BasicFlyView());
             RenderableLayer layer; // layer to display the polygon and the intersection
            // Create the grid and its positions. This one is rectangular; it need not be, but must be four-sided.
             this.analyticSurfaceLayer= new RenderableLayer();
             this.analyticSurfaceLayer.setPickEnabled(false);
 		//	analyticSurfaceLayer.setName("Analytic Surfaces");
 			
 			//this.getLayerPanel().update(this.getWwd());
 			//this.wwjPanel.set
 		//	this.layerPanel.setEnabled(false);
 			//this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, 100));
            // this.getContentPane().re
 		//	this.controlPanel.setEnabled(false);
 			try {

 				String workDir = "C:\\\\Users\\\\yuanchunyu\\\\Documents\\\\Discharge_NCEP_Pristine_dTS2004.nc";
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

 		
 			createRandomColorSurface(this.hintf,this.valuef,1, HUE_RED, 1720, 1120, this.analyticSurfaceLayer);
 			insertBeforePlacenames(this.getWwd(), analyticSurfaceLayer);
 

 		
     
			this.selector = new SectorSelector(getWwd());
			this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
			this.selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
			this.selector.setBorderWidth(3);
           
            
            // Create a hash map to store the data associated with each position.
           // this.positionInfo = new HashMap<Position, Object>(NUM_POINTS_WIDE * NUM_POINTS_HIGH);

         	
       
    
            //////////////////////////////////////////////////////
    	
    		
    		///////////////////////////////////////////////////
    		 
    		JPanel p = new JPanel(new BorderLayout(1, 1));

			p.add(makeDetailHintControlPanel());
			p.add(makeDetailHintControlPanel_value(), BorderLayout.NORTH);
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
  			 // System.out.println("Ã¨Â¿ï¿½Ã¨Â¡Å’Ã¦â€�?Â¶Ã©â€�?Â´Ã¦ËœÂ¯Ã¯Â¼Å¡"+(end-star));
  			//System.out.println(rhData[1][0][2]);
  		
            // Create the PointGrid shape.
          //  this.grid = new PointGrid(corners, this.positions, NUM_POINTS_WIDE * NUM_POINTS_HIGH);



        
        }
        
        protected JPanel makeDetailHintControlPanel()
        {
            JPanel controlPanel = new JPanel(new BorderLayout(100,1));
            
           // controlPanel.setLocation(150,150);
            controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
                new TitledBorder("day time")));

            JPanel elevationSliderPanel = new JPanel(new BorderLayout(0, 1));
            {
                int MIN = 0;
                int MAX = 366;
                int cur = 0;
                JSlider slider = new JSlider(MIN, MAX, cur);
                slider.setMajorTickSpacing(366);
                slider.setMinorTickSpacing(1);
                slider.setExtent(1);
                slider.setPaintTicks(true);
                
                Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
                labelTable.put(0, new JLabel("l"));
                labelTable.put(0, new JLabel("0.0"));
                labelTable.put(366, new JLabel("r"));
                slider.setLabelTable(labelTable);
                slider.setPaintLabels(true);
                slider.addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e)
                    {
                    	JSlider source = ((JSlider) e.getSource()) ;
                      //  if (!source.getValueIsAdjusting()) {
                            int hint = (int)source.getValue();
                            try {
                            	//this.a
                            
    							setWedgeDetailHint(hint);
    							   getWwd().redraw();
    						} catch (IOException | InvalidRangeException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
                       // }
                        
                     
                    }
                });
                elevationSliderPanel.add(slider, BorderLayout.SOUTH);
            }

            JPanel sliderPanel = new JPanel(new GridLayout(1, 0));
            sliderPanel.add(elevationSliderPanel);

            controlPanel.add(sliderPanel, BorderLayout.SOUTH);
            return controlPanel;
        }
        
        protected JPanel makeDetailHintControlPanel_value()
        {
            JPanel controlPanel = new JPanel(new BorderLayout(100,1));
            
           // controlPanel.setLocation(150,150);
            controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
                new TitledBorder("value")));

            JPanel elevationSliderPanel = new JPanel(new BorderLayout(0, 1));
            {
                int MIN =0;
                int MAX = 101;
                int cur = 1;
                JSlider slider = new JSlider(MIN, MAX, cur);
                slider.setMajorTickSpacing(101);
                slider.setMinorTickSpacing(1);
               // slider.setExtent(10);
                slider.setPaintTicks(true);
                
                Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
                labelTable.put(1, new JLabel(String.valueOf(1)));	
                for(int i=10;i<101;i+=10) {
                	  labelTable.put(i, new JLabel(String.valueOf(i)));	
                }
        
                slider.setLabelTable(labelTable);
                slider.setPaintLabels(true);
                slider.addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e)
                    {
                    	JSlider source = ((JSlider) e.getSource()) ;
                      //  if (!source.getValueIsAdjusting()) {
                            int value = (int)source.getValue();
                            try {
                            	//this.a
                            
                            	setWedgeDetailValue(value);
    							   getWwd().redraw();
    						} catch (IOException | InvalidRangeException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
                       // }
                        
                     
                    }
                });
                elevationSliderPanel.add(slider, BorderLayout.SOUTH);
            }

            JPanel sliderPanel = new JPanel(new GridLayout(1, 0));
            sliderPanel.add(elevationSliderPanel);

            controlPanel.add(sliderPanel, BorderLayout.SOUTH);
            return controlPanel;
        }
        protected RenderableLayer getLayer()
        {
            for (Layer layer : getWwd().getModel().getLayers())
            {
                if (layer.getName().contains("Renderable"))
                {
                    return (RenderableLayer) layer;
                }
            }

            return null;
        }

        protected void setWedgeDetailHint(int hint) throws IOException, InvalidRangeException
        {
          
    		this.getLayer().removeAllRenderables();
        //	this.analyticSurfaceLayer.clearList();
         //   getWwd().clearList();
    		this.hintf=hint;
        	createRandomColorSurface(this.hintf,this.valuef,1, HUE_RED, 1720, 1120, this.analyticSurfaceLayer);
      		       //     getLayer().addRenderable(grid);
              		//insertBeforeCompass(getWwd(), analyticSurfaceLayer);
      		   
                     // }
        		
          //  System.out.println("wedge detail hint set to " + hint);
        }

        protected void setWedgeDetailValue(int value) throws IOException, InvalidRangeException
        {
          
    		this.getLayer().removeAllRenderables();
        //	this.analyticSurfaceLayer.clearList();
         //   getWwd().clearList();
    		this.valuef=value;
        	createRandomColorSurface(this.hintf,this.valuef,1, HUE_RED, 1720, 1120, this.analyticSurfaceLayer);
      		       //     getLayer().addRenderable(grid);
              		//insertBeforeCompass(getWwd(), analyticSurfaceLayer);
      		   
                     // }
        		
          //  System.out.println("wedge detail hint set to " + hint);
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
						double maxla=location.get(2).getLatitude().degrees;
						double minlo=location.get(0).getLongitude().degrees;
						double maxlo=location.get(1).getLongitude().degrees;
						int minlaa=0,maxlaa=0,minloo=0,maxloo=0;
						 for(int i=0;i<logs.length;i++){
						        if(logs[i]<=minlo){
						        	minloo=i;
						        //	System.out.println(logs[i]);
						        	
						        }else{
						        	break;
						        }
						        }
							 for(int i=0;i<logs.length;i++){
							        if(logs[i]<=maxlo){
							        	maxloo=i;
							      //  	System.out.println(logs[i]);
							        }else
							        	break;
							        }
						//	 System.out.println(minloo+" "+logs[minloo]+" "+maxloo+" "+logs[maxloo]);
							 for(int i=0;i<lats.length;i++){
							        if(lats[i]<=minla){
							        	minlaa=i;
							        	
							        }else{
							        	break;
							        }
							        }
							 for(int i=0;i<lats.length;i++){
							        if(lats[i]<=maxla){
							        	maxlaa=i;
							        
							        }else{
							        	break;
							        }
							        }
							
								
								series.clear();
							//	System.out.println(lats.length+" "+logs.length);
								for (int i = 0; i < 366; i++) {
									double sum=0;
									
									Array x=discharge.read(i+":"+i+":1,"+minlaa+":"+maxlaa+":1,"+minloo+":"+maxloo+":1");
								
									/* double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
									for(int j=0;j<floatValues[0].length;j++){
										for(int l=0;l<floatValues[0][0].length;l++){
											if(floatValues[0][j][l]>0){
												sum+=floatValues[0][j][l];
												//System.out.println(lats[j]+" "+logs[l]+" "+-maxla+":"+-minla+":1,"+minlo+":"+maxlo+":1"+floatValues.length);
											}
										}	
									}*/
									for(int j=0;j<x.getSize();j++){
										if(x.getDouble(j)>0){
											sum+=x.getDouble(j);
										}
									}
									series.add(i+1, sum);
									//System.out.println(x);
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
					return new java.awt.Dimension(300, 200);
				}

				public double getScaleX() {
					return 366;
				}

			};

		}
    }
    
    protected static void createRandomColorSurface(int hint,int valuec,double minHue, double maxHue, int width, int height,
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
		//BufferFactory te = new BufferFactory.DoubleBufferFactory();
		//BufferWrapper firstBuffer = te.newBuffer(1926400);
		Array x = discharge.read(String.valueOf(hint)+",0:1119:1,0:1719:1");
		

	
		surface.setValues(
				createMixedColorGradientGridValues(1, x, minValue, maxValue, minHue, maxHue,valuec));

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
			double maxHue,int valuec) {
		ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();
		//double[] values = new double[1926400];
		double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
		// System.out.println(x.getSize());

		// this. positions = new ArrayList<Position>();
		// int coun=0;
		// long a=System.currentTimeMillis();
////double max=Double.MIN_VALUE;
		for (int i = 1119; i >= 0; i--) {
			for (int j = 0; j <= 1719; j++) {
				double value = -200;
				if (floatValues[0][i][j]> valuec) {
					// continue;
					value = floatValues[0][i][j];
				//	max=Math.max(max, floatValues[0][i][j]);
					//value =  WWMath.mixSmooth(a, floatValues[0][i][j],
							//floatValues[0][i][j]);
				} 
				attributesList
				.add(AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
				// System.out.println(floatValues[0][i][j]);
				// coun+=1;
			}
		}
		//System.out.println("???????????????????????"+attributesList.size());
//System.out.println(max);
		return attributesList;
	}

    public static void main(String[] args)
    {
   		
    //	Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        

        ApplicationTemplate.start("World Wind Point Grid", AppFrame.class);
    }
}
