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
import gov.nasa.worldwindx.examples.util.ToolTipController;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.Wedge;

import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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
public class GridOfPoints extends ApplicationTemplate
{
    protected static final int NUM_POINTS_WIDE = 2000;
    protected static final int NUM_POINTS_HIGH = 2000;
	//static String fileName = "C:\\Users\\yuanchunyu\\Documents\\Discharge_NCEP_Pristine_dTS2004.nc";
	static double[]logs;
	static double[] lats;
	static Variable discharge;
	
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
       // protected RenderableLayer layer; // layer to display the polygon and the intersection
        protected HashMap<Position, Object> positionInfo;
        protected PointGrid grid ;
        protected List<Position> positions;
       // protected List<Position> positions1;
       // protected List<Position> positions2;
        protected List<Position> corners;
        public AppFrame()
        {
       
            super(true, true, false);
             RenderableLayer layer; // layer to display the polygon and the intersection
            // Create the grid and its positions. This one is rectangular; it need not be, but must be four-sided.
            this. corners = new ArrayList<Position>();
            corners.add(Position.fromDegrees(5.0, -138.0, 10e3));
            corners.add(Position.fromDegrees(61.0, -138.0, 10e3));
            corners.add(Position.fromDegrees(61.0, -42.0, 10e3));
            corners.add(Position.fromDegrees(5.0, -42.0, 10e3));
            this.add(makeDetailHintControlPanel(), BorderLayout.SOUTH);
           this. positions = new ArrayList<Position>();
          // this. positions1 = new ArrayList<Position>();
          // this. positions2 = new ArrayList<Position>();
            HashMap<Integer, List<Position>> map=new HashMap<>();
           
            
            // Create a hash map to store the data associated with each position.
            this.positionInfo = new HashMap<Position, Object>(NUM_POINTS_WIDE * NUM_POINTS_HIGH);

         	
       
            ///////////////////////////////////////////////////
            
            
    		try {
    			
    			 String workDir = "C:\\Users\\cyuan5\\Documents\\Discharge_NCEP_Pristine_dTS2004.nc";
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
    			 String pathname = "C:\\Users\\cyuan5\\Documents\\output.txt"; // ç»�å¯¹è·¯å¾„æˆ–ç›¸å¯¹è·¯å¾„éƒ½å�¯ä»¥ï¼Œè¿™é‡Œæ˜¯ç»�å¯¹è·¯å¾„ï¼Œå†™å…¥æ–‡ä»¶æ—¶æ¼”ç¤ºç›¸å¯¹è·¯å¾„  
                 
  			   System.out.println(">>>>>>>>");
  			 
  			   File filename = new File(pathname); // è¦�è¯»å�–ä»¥ä¸Šè·¯å¾„çš„inputã€‚txtæ–‡ä»¶  
                 InputStreamReader reader = new InputStreamReader(  
                         new FileInputStream(filename)); // å»ºç«‹ä¸€ä¸ªè¾“å…¥æµ�å¯¹è±¡reader  
                 BufferedReader br = new BufferedReader(reader); // å»ºç«‹ä¸€ä¸ªå¯¹è±¡ï¼Œå®ƒæŠŠæ–‡ä»¶å†…å®¹è½¬æˆ�è®¡ç®—æœºèƒ½è¯»æ‡‚çš„è¯­è¨€  
                 String line = "";  
                 line = br.readLine();  
                 while (line != null) {  
                     line = br.readLine(); // ä¸€æ¬¡è¯»å…¥ä¸€è¡Œæ•°æ�®  
                     if(line!=null){
                     String []loca=line.split(",");
                  //   System.out.println(line);
                 // positions.add(new Position(Angle.fromDegrees(Double.parseDouble(line.substring(0, line.indexOf(",")))), Angle.fromDegrees(Double.parseDouble(line.substring(line.indexOf(",")+1))),10));
                    
                   	this. positions.add(new Position(Angle.fromDegrees(Double.parseDouble(loca[0])), Angle.fromDegrees(Double.parseDouble(loca[1])),10));
                         }
                   //  System.out.println(line);
    			//System.out.println(ii);
                 }
    			 long  end=System.currentTimeMillis();
    			
    			//  System.out.println("è¿�è¡Œæ—¶é—´æ˜¯ï¼š"+(end-star));
    			  pathname = "C:\\Users\\cyuan5\\Documents\\output1.txt";
       		 filename = new File(pathname); // è¦�è¯»å�–ä»¥ä¸Šè·¯å¾„çš„inputã€‚txtæ–‡ä»¶  
           reader = new InputStreamReader(  
                          new FileInputStream(filename)); // å»ºç«‹ä¸€ä¸ªè¾“å…¥æµ�å¯¹è±¡reader  
                br = new BufferedReader(reader); // å»ºç«‹ä¸€ä¸ªå¯¹è±¡ï¼Œå®ƒæŠŠæ–‡ä»¶å†…å®¹è½¬æˆ�è®¡ç®—æœºèƒ½è¯»æ‡‚çš„è¯­è¨€  
                  line = "";  
                  line = br.readLine();  
                  while (line != null) {  
                      line = br.readLine(); // ä¸€æ¬¡è¯»å…¥ä¸€è¡Œæ•°æ�®  
                      if(line!=null){
                      String []loca=line.split(",");
                   //   System.out.println(line);
                  // positions.add(new Position(Angle.fromDegrees(Double.parseDouble(line.substring(0, line.indexOf(",")))), Angle.fromDegrees(Double.parseDouble(line.substring(line.indexOf(",")+1))),10));
                     
                    	//this. positions1.add(new Position(Angle.fromDegrees(Double.parseDouble(loca[0])), Angle.fromDegrees(Double.parseDouble(loca[1])),10));
                          }
                    //  System.out.println(line);
     			//System.out.println(ii);
                  }
         
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
            //////////////////////////////////////////////////////
    		 
  			
  			 // System.out.println("è¿�è¡Œæ—¶é—´æ˜¯ï¼š"+(end-star));
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
            JPanel controlPanel = new JPanel(new BorderLayout(120, 10));
            
           // controlPanel.setLocation(150,150);
            controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9),
                new TitledBorder("Detail Hint")));

            JPanel elevationSliderPanel = new JPanel(new BorderLayout(0, 5));
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

            JPanel sliderPanel = new JPanel(new GridLayout(2, 0));
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

       			/*   File filename = new File(pathname); // è¦�è¯»å�–ä»¥ä¸Šè·¯å¾„çš„inputã€‚txtæ–‡ä»¶  
                      InputStreamReader reader = new InputStreamReader(  
                              new FileInputStream(filename)); // å»ºç«‹ä¸€ä¸ªè¾“å…¥æµ�å¯¹è±¡reader  
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
        		
            System.out.println("wedge detail hint set to " + hint);
        }
    }

/*    *//** Generates positions forming a lat/lon grid. *//*
    protected static class PositionIterator implements Iterator<Position>
    {
        protected int numWide = NUM_POINTS_WIDE;
        protected int numHigh = NUM_POINTS_HIGH;
        protected int w;
        protected int h;

        protected List<Position> corners;
        protected Position sw;
        protected Position se;
        protected Position ne;
        protected Position nw;

        public PositionIterator(List<Position> corners, int numPointsWide, int numPointsHigh)
        {
            this.corners = corners;
            this.sw = corners.get(0);
            this.se = corners.get(1);
            this.ne = corners.get(2);
            this.nw = corners.get(3);

            this.numWide = numPointsWide;
            this.numHigh = numPointsHigh;
        }

        public boolean hasNext()
        {
            return this.h < this.numHigh;
        }

        public Position next()
        {
            if (this.h >= this.numHigh)
                throw new NoSuchElementException("PointGridIterator");

            return this.computeNextPosition();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("PointGridIterator");
        }

        protected Position computeNextPosition()
        {
            Position left, right;

            if (h == 0)
            {
                left = sw;
                right = se;
            }
            else if (h == numHigh - 1)
            {
                left = nw;
                right = ne;
            }
            else
            {
                double t = h / (double) (numHigh - 1);

                left = Position.interpolate(t, sw, nw);
                right = Position.interpolate(t, se, ne);
            }

            Position pos;

            if (w == 0)
            {
                pos = left;
                ++w;
            }
            else if (w == numWide - 1)
            {
                pos = right;

                w = ++h < numHigh ? 0 : w + 1;
            }
            else
            {
                double s = w / (double) (numWide - 1);
                pos = Position.interpolate(s, left, right);
                ++w;
            }

            return pos;
        }
    }*/

    public static void main(String[] args)
    {
   		//try {
			
        // Configure the initial view parameters so that the balloons are immediately visible.
     /*   Configuration.setValue(AVKey.INITIAL_LATITUDE, 40.5);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -120.4);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 2000e3);
        Configuration.setValue(AVKey.INITIAL_HEADING, 27);
        Configuration.setValue(AVKey.INITIAL_PITCH, 30);*/
   

        ApplicationTemplate.start("World Wind Point Grid", AppFrame.class);
    }
}
