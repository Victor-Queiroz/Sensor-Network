package org.knowm.xchart.demo.charts.realtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.demo.charts.RealtimeExampleChart;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * Real-time XY Chart
 *
 * <p>Demonstrates the following:
 *
 * <ul>
 *   <li>real-time chart updates with SwingWrapper
 *   <li>Matlab Theme
 */
public class RealtimeChart01 implements ExampleChart<XYChart>, RealtimeExampleChart {
    
    static String myIP;
    
    static String dataDevice1 = "0";
    static String dataDevice2 = "0";
    static String dataDevice3 = "0";
    
    static   double media ;
   
    
    static Socket s;
    static ServerSocket ss;
    static InputStreamReader isr;
    static BufferedReader br;
    static String message;
    
    static Socket socket;
    static ServerSocket serverSocket;
    static InputStreamReader inputStreamReader;
    static BufferedReader bufferedReader;
    static ObjectInputStream in;

  private XYChart xyChart, xyChart2, xyChart3;
  private List<Double> yData, yData2, yData3;
  public static final String SERIES_NAME = "MEDIA";

  public static void main(String[] args) {
      
     try {
            InetAddress inetAddress = InetAddress.getLocalHost();
           myIP = inetAddress.getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(RealtimeChart01.class.getName()).log(Level.SEVERE, null, ex);
        }

    // Setup the panel
    final RealtimeChart01 realtimeChart01 = new RealtimeChart01();
    realtimeChart01.go();
    
     Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                getData(4800);
            }
        });
        t1.start();
  }
  
  
  /************** SOCKET *******************/
  public static void getData(int port){
        
        int tot=0;
        int test=0;
        List<String> list = null;
        
        
        String ipDevice1 = "";
        String ipDevice2 = "";
        String ipDevice3 = "";

        
         List<Device> devices;
         
         devices = new ArrayList<Device>();
        
        try {
            serverSocket = new ServerSocket(port);

            while(true){
                socket = serverSocket.accept();
                in = new ObjectInputStream(socket.getInputStream());
                list=(ArrayList<String>) in.readObject();
                
                if(devices.size() != 0){
                    
                    for(int i=0; i<devices.size(); i++){
                        if(devices.get(i).getIp().equals(list.get(1))){
                            test=1;                            
                        }
                    }
                    
                    
                    
                    if(test == 1){
                        System.out.println("Já cadastrado");
                        test=0;
                    } else{
                        
                        if(tot == 1){
                            Device d = new Device();
                            d.setData(list.get(0));
                            d.setIp(list.get(1));
                            devices.add(d);
                            
                            dataDevice2 = list.get(0);
                            ipDevice2 = list.get(1);
                            tot++;
                        } else{
                            Device d = new Device();
                            d.setData(list.get(0));
                            d.setIp(list.get(1));
                            devices.add(d);
                            
                            dataDevice3 = list.get(0);
                            ipDevice3 = list.get(1);
                            tot++;
                        }
                        
                    }
                    
                } else{
                    Device d = new Device();
                    d.setData(list.get(0));
                    d.setIp(list.get(1));
                    devices.add(d);
                    
                    dataDevice1 = list.get(0);
                    ipDevice1 = list.get(1);
                    tot++;
                }

                
                for(int i=0; i<devices.size(); i++){
                    System.out.println(devices.get(i).getIp());
                }
                
                if(ipDevice1 != ""){
                    String msg = "IP: "+ ipDevice1 +" - Data: "+ dataDevice1;
                    //lbDevice1.setText(msg);
                }
                if(ipDevice2 != ""){
                    String msg = "IP: "+ ipDevice2 +" - Data: "+ dataDevice2;
                    //lbDevice2.setText(msg);
                }
                if(ipDevice3 != ""){
                    String msg = "IP: "+ ipDevice3 +" - Data: "+ dataDevice3;
                    //lbDevice3.setText(msg);
                }
                
               // devices.clear();
                if(tot == 3){
                    System.out.println("3 dispositivos cadastrados!");
                    tot = 0;
                    devices.clear();
                    
                   media = (Double.parseDouble(dataDevice1) +  Double.parseDouble(dataDevice2) + Double.parseDouble(dataDevice3)) / 3;
                    
                    //lbAverage.setText(""+media);
                    
                }
                
            }

        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
        }
        
    }


  private void go() {

    final SwingWrapper<XYChart> swingWrapper = new SwingWrapper<XYChart>(getChart());
    swingWrapper.displayChart();

    // Simulate a data feed
    TimerTask chartUpdaterTask =
        new TimerTask() {

          @Override
          public void run() {

            updateData();

            javax.swing.SwingUtilities.invokeLater(
                new Runnable() {

                  @Override
                  public void run() {

                    swingWrapper.repaintChart();
                  }
                });
          }
        };

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(chartUpdaterTask, 0, 500);
  }

  @Override
  public XYChart getChart() {
      
       List<Double> data = new CopyOnWriteArrayList<Double>();
       data.add(0.0);

    yData = getRandomData(1);


    // Create Chart
    xyChart =
        new XYChartBuilder()
            .width(500)
            .height(400)
            .theme(ChartTheme.Matlab)
            .title("MEDIA LUMENS")
            .build();
    
    
    xyChart.addSeries(SERIES_NAME, null, yData);

    return xyChart;
  }

  public void updateData() {

    // Get some new data
   
    yData.add(media);
    

    // Limit the total number of points
    while (yData.size() > 20) {
      yData.remove(0);
    }

    xyChart.updateXYSeries(SERIES_NAME, null, yData, null);

  }

  private List<Double> getRandomData(int numPoints) {

    List<Double> data = new CopyOnWriteArrayList<Double>();
    for (int i = 0; i < numPoints; i++) {
      data.add(Math.random() * 100);
    }
    return data;
  }
}
