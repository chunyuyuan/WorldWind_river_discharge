/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples.lineofsight;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.SectorSelector;
import gov.nasa.worldwindx.examples.util.ToolTipController;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.Wedge;
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
public class GridOfPoints3 extends ApplicationTemplate
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
        protected PointGrid grid ;
        protected List<Position> positions;
       // protected List<Position> positions1;
       // protected List<Position> positions2;
        protected List<Position> corners;
		static List<LatLon> location;
		private SectorSelector selector;
		private static int check = 1;
		
		
		
        public AppFrame()
        {
       
            super(false, false, false);
         //   getWwd().setView(new BasicFlyView());
             RenderableLayer layer; // layer to display the polygon and the intersection
            // Create the grid and its positions. This one is rectangular; it need not be, but must be four-sided.
            this. corners = new ArrayList<Position>();
            corners.add(Position.fromDegrees(5.0, -138.0, 10e3));
            corners.add(Position.fromDegrees(61.0, -138.0, 10e3));
            corners.add(Position.fromDegrees(61.0, -42.0, 10e3));
            corners.add(Position.fromDegrees(5.0, -42.0, 10e3));
          //  this.add(makeDetailHintControlPanel(), BorderLayout.NORTH);
           this. positions = new ArrayList<Position>();
          // this. positions1 = new ArrayList<Position>();
          // this. positions2 = new ArrayList<Position>();
        //    HashMap<Integer, List<Position>> map=new HashMap<>();
			this.selector = new SectorSelector(getWwd());
			this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
			this.selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
			this.selector.setBorderWidth(3);
           
            
            // Create a hash map to store the data associated with each position.
           // this.positionInfo = new HashMap<Position, Object>(NUM_POINTS_WIDE * NUM_POINTS_HIGH);

         	
       
            ///////////////////////////////////////////////////
            
            
    		try {
    			
    			 String workDir = "/home/yuanchunyu/Documents/Discharge_NCEP_Pristine_dTS2004.nc";
    		        //Reading netcdf run file
    		      //  Path runPath = runFile.getParentFile().toPath();
    		        NetcdfDataset netcdfRunFileDataset = new NetcdfDataset(NetcdfDataset.openFile(workDir, null));
    		 
    		        discharge = netcdfRunFileDataset.findVariable("discharge");
    		        Variable lat = netcdfRunFileDataset.findVariable("latitude");
    		        Variable lon = netcdfRunFileDataset.findVariable("longitude");
    		      //  ArrayList<String>location=new ArrayList<>();
    		        Array la=lat.read();
    		        lats=(double [])la.copyTo1DJavaArray();
    		       
    		        Array lo=lon.read();
    		        logs=(double [])lo.copyTo1DJavaArray();
    			 String pathname = "C:\\Users\\yuanchunyu\\Documents\\output.txt"; // Ã§Â»ï¿½Ã¥Â¯Â¹Ã¨Â·Â¯Ã¥Â¾â€žÃ¦Ë†â€“Ã§â€ºÂ¸Ã¥Â¯Â¹Ã¨Â·Â¯Ã¥Â¾â€žÃ©Æ’Â½Ã¥ï¿½Â¯Ã¤Â»Â¥Ã¯Â¼Å’Ã¨Â¿â„¢Ã©â€¡Å’Ã¦ËœÂ¯Ã§Â»ï¿½Ã¥Â¯Â¹Ã¨Â·Â¯Ã¥Â¾â€žÃ¯Â¼Å’Ã¥â€ â„¢Ã¥â€¦Â¥Ã¦â€“â€¡Ã¤Â»Â¶Ã¦â€”Â¶Ã¦Â¼â€�Ã§Â¤ÂºÃ§â€ºÂ¸Ã¥Â¯Â¹Ã¨Â·Â¯Ã¥Â¾â€ž  
                 
  			//   System.out.println(">>>>>>>>");
  			 
    			  Array x=discharge.read("0,0:1119:1,0:1719:1");
                  double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
                  this. positions = new ArrayList<Position>();
                //  int coun=0;
                //  long a=System.currentTimeMillis();
                  for(int i=0;i<1120;i++){
                  	for(int j=0;j<1720;j++){
                  		if(Double.isNaN(floatValues[0][i][j])){
                  			continue;
                  		}else if(floatValues[0][i][j]>1){
                  		//else
                  			this. positions.add(new Position(Angle.fromDegrees(lats[i]), Angle.fromDegrees(logs[j]),10));
                  			//System.out.println(floatValues[0][i][j]);
                  		}
                        
                  		// System.out.println(floatValues[0][i][j]);
                  		// coun+=1;
                  	}
                  }
   			 long  end=System.currentTimeMillis();
    			
    			//  System.out.println("Ã¨Â¿ï¿½Ã¨Â¡Å’Ã¦â€”Â¶Ã©â€”Â´Ã¦ËœÂ¯Ã¯Â¼Å¡"+(end-star));
    		//	  pathname = "C:\\Users\\yuanchunyu\\Documents\\output1.txt";
       		// filename = new File(pathname); // Ã¨Â¦ï¿½Ã¨Â¯Â»Ã¥ï¿½â€“Ã¤Â»Â¥Ã¤Â¸Å Ã¨Â·Â¯Ã¥Â¾â€žÃ§Å¡â€žinputÃ£â‚¬â€štxtÃ¦â€“â€¡Ã¤Â»Â¶  
         //  reader = new InputStreamReader(  
             //             new FileInputStream(filename)); // Ã¥Â»ÂºÃ§Â«â€¹Ã¤Â¸â‚¬Ã¤Â¸ÂªÃ¨Â¾â€œÃ¥â€¦Â¥Ã¦Âµï¿½Ã¥Â¯Â¹Ã¨Â±Â¡reader  
            ////    br = new BufferedReader(reader); // Ã¥Â»ÂºÃ§Â«â€¹Ã¤Â¸â‚¬Ã¤Â¸ÂªÃ¥Â¯Â¹Ã¨Â±Â¡Ã¯Â¼Å’Ã¥Â®Æ’Ã¦Å Å Ã¦â€“â€¡Ã¤Â»Â¶Ã¥â€ â€¦Ã¥Â®Â¹Ã¨Â½Â¬Ã¦Ë†ï¿½Ã¨Â®Â¡Ã§Â®â€”Ã¦Å“ÂºÃ¨Æ’Â½Ã¨Â¯Â»Ã¦â€¡â€šÃ§Å¡â€žÃ¨Â¯Â­Ã¨Â¨â‚¬  
              //    line = "";  
              //    line = br.readLine();  
              //    while (line != null) {  
               //       line = br.readLine(); // Ã¤Â¸â‚¬Ã¦Â¬Â¡Ã¨Â¯Â»Ã¥â€¦Â¥Ã¤Â¸â‚¬Ã¨Â¡Å’Ã¦â€¢Â°Ã¦ï¿½Â®  
                //      if(line!=null){
                //      String []loca=line.split(",");
                   //   System.out.println(line);
                  // positions.add(new Position(Angle.fromDegrees(Double.parseDouble(line.substring(0, line.indexOf(",")))), Angle.fromDegrees(Double.parseDouble(line.substring(line.indexOf(",")+1))),10));
                     
                    	//this. positions1.add(new Position(Angle.fromDegrees(Double.parseDouble(loca[0])), Angle.fromDegrees(Double.parseDouble(loca[1])),10));
                      //    }
                    //  System.out.println(line);
     			//System.out.println(ii);
                //  }
         
    		} catch (IOException | InvalidRangeException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
            //////////////////////////////////////////////////////
    		 
    		JPanel p = new JPanel(new BorderLayout(1, 1));

			p.add(makeDetailHintControlPanel(), BorderLayout.NORTH);
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
  			 // System.out.println("Ã¨Â¿ï¿½Ã¨Â¡Å’Ã¦â€”Â¶Ã©â€”Â´Ã¦ËœÂ¯Ã¯Â¼Å¡"+(end-star));
  			//System.out.println(rhData[1][0][2]);
  		
            // Create the PointGrid shape.
            this.grid = new PointGrid(corners, this.positions, NUM_POINTS_WIDE * NUM_POINTS_HIGH);

            // Set its attributes.
            PointGrid.Attributes attrs = new PointGrid.Attributes();
            System.out.println("getNumPositions"+this.grid.getNumPositions());
            attrs.setPointColor(Color.red);
            attrs.setPointSize(10d);
            this.grid.setAttributes(attrs);

            // Add the shape to the display layer.
         layer = new RenderableLayer();
            layer.addRenderable(this.grid);
            insertBeforeCompass(getWwd(), layer);
            getLayer().removeAllRenderables();
           
          //  System.out.println("mkkmlkmlmlmlml");
            getLayer().addRenderable(this.grid);

        
        }
        protected JPanel makeDetailHintControlPanel()
        {
            JPanel controlPanel = new JPanel(new BorderLayout(100,1));
            
           // controlPanel.setLocation(150,150);
            controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
                new TitledBorder("Detail Hint")));

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
          
        		//	String pathname="C:\\Users\\cyuan5\\Documents\\ncdata\\output"+String.valueOf(hint)+".txt";

       			/*   File filename = new File(pathname); // Ã¨Â¦ï¿½Ã¨Â¯Â»Ã¥ï¿½â€“Ã¤Â»Â¥Ã¤Â¸Å Ã¨Â·Â¯Ã¥Â¾â€žÃ§Å¡â€žinputÃ£â‚¬â€štxtÃ¦â€“â€¡Ã¤Â»Â¶  
                      InputStreamReader reader = new InputStreamReader(  
                              new FileInputStream(filename)); // Ã¥Â»ÂºÃ§Â«â€¹Ã¤Â¸â‚¬Ã¤Â¸ÂªÃ¨Â¾â€œÃ¥â€¦Â¥Ã¦Âµï¿½Ã¥Â¯Â¹Ã¨Â±Â¡reader  
*/                      Array x=discharge.read(String.valueOf(hint)+",0:1119:1,0:1719:1");
                      double[][][] floatValues = (double[][][]) x.copyToNDJavaArray();
                      this. positions = new ArrayList<Position>();
                    //  int coun=0;
                    //  long a=System.currentTimeMillis();
                      for(int i=0;i<1120;i++){
                      	for(int j=0;j<1720;j++){
                      		if(Double.isNaN(floatValues[0][i][j])){
                      			continue;
                      		}else if(floatValues[0][i][j]>1){
                      		//else
                      			this. positions.add(new Position(Angle.fromDegrees(lats[i]), Angle.fromDegrees(logs[j]),10));
                      			//System.out.println(floatValues[0][i][j]);
                      		}
                            
                      		// System.out.println(floatValues[0][i][j]);
                      		// coun+=1;
                      	}
                      }
                     
                
                         this.grid = new PointGrid(this.corners, this.positions, NUM_POINTS_WIDE * NUM_POINTS_HIGH);

      		          
      		            PointGrid.Attributes attrs = new PointGrid.Attributes();
      		          //  System.out.println("getNumPositions"+this.grid.getNumPositions());
      		            attrs.setPointColor(Color.red);
      		            attrs.setPointSize(10d);
      		            this.grid.setAttributes(attrs);

      		            // Add the shape to the display layer.
      		        
      		           // layer.addRenderable(this.grid);
      		          //  insertBeforeCompass(getWwd(), layer);
      		            getLayer().removeAllRenderables();
      		            getLayer().addRenderable(grid);
      		   
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
					return new java.awt.Dimension(300, 200);
				}

				public double getScaleX() {
					return 366;
				}

			};

		}
    }


    public static void main(String[] args)
    {
   		
    //	Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        

        ApplicationTemplate.start("World Wind Point Grid", AppFrame.class);
    }
}
