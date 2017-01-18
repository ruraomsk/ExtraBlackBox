/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.awt.Color;
import static java.awt.Color.WHITE;
import static java.awt.Color.lightGray;
import static java.awt.Color.white;
import static java.lang.Math.abs;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class SerialChart extends ApplicationFrame
{

    DataTable data;
    DataTable setup;
    ChartPanel chartPanel;
    JFreeChart chart;
    HashMap<String,TimeSeriesCollection> datasets=new HashMap<>();
    boolean work=true;
    public SerialChart(DataTable data, DataTable setup)
    {

        super("Графики");
        this.data = data;
        this.setup = setup;
        loadData();
        SuperExtra.appendMessage("Данные готовы к построению графиков");
//        XYDataset dataset = loadData();
        chart = createChart();
        work=false;
    }
    public boolean isWorking(){
        return work;
    }
            
    private void loadData()
    {
        for (DataRecord dr : setup)
        {
            if (!dr.getBoolean("choice"))
            {
                continue;
            }
            TimeSeriesCollection dataset=new TimeSeriesCollection();
            TimeSeries ts = new TimeSeries(dr.getString("name"), FixedMillisecond.class);
            for (DataRecord dd : data)
            {
                if (!dr.getString("name").equalsIgnoreCase(dd.getString("name")))
                {
                    continue;
                }
                if(dd.getDate("time").getTime()==0L) continue;
                
                ts.addOrUpdate(new FixedMillisecond(dd.getDate("time")), dd.getFloat("value"));
            }
            dataset.addSeries(ts);
            dataset.setDomainIsPointsInTime(true);
            datasets.put(dr.getString("name"), dataset);
            SuperExtra.appendMessage("Подгружен набор");
            
        }
        
        return;
    }

    private JFreeChart createChart()
    {
        JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Время", "Значения", null, true, true, false);
        chart.setBackgroundPaint(WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(lightGray);
        plot.setDomainGridlinePaint(white);
        plot.setRangeGridlinePaint(white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        int count = 0;
        for (DataRecord dr : setup)
        {
            if (!dr.getBoolean("choice"))
            {
                continue;
            }
            NumberAxis axis = new NumberAxis();
            if(abs(dr.getFloat("min")-dr.getFloat("max"))<0.0000001F){
                dr.setValue("max", dr.getFloat("max")+1);
            }
            axis.setRange(dr.getFloat("min"), dr.getFloat("max"));
            axis.setAxisLinePaint(dr.getColor("color"));
            plot.setRangeAxis(count, axis);
            plot.setRangeAxisLocation(count, AxisLocation.TOP_OR_LEFT);
            plot.setDataset(count, datasets.get(dr.getString("name")));
            plot.setRenderer(count, new XYLineAndShapeRenderer());
            plot.mapDatasetToRangeAxis(count, count);
            plot.getRenderer(count).setSeriesPaint(0,dr.getColor("color") );
            count++;
        }
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer)
        {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            //renderer.set(dr.getColor("color"));
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        return chart;
    }
}
