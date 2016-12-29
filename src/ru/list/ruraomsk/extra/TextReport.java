/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.awt.Font;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.FontUIResource;
import static ru.list.ruraomsk.extra.SuperExtra.central;
import static ru.list.ruraomsk.extra.SuperExtra.frame;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class TextReport
{

    final int withTime = 23;
    final int withValue = 15;
    final int withCount = 8;
    HashMap<String, Float> line = new HashMap<>();
    DataTable data;
    DataTable setup;
    StringBuilder rezult = new StringBuilder(Short.MAX_VALUE);
    Date time;
    int count=1;
    boolean work=true;

    public TextReport(JPanel central,DataTable data, DataTable setup, Date from, Date to)
    {
        this.data = data;
        this.setup = setup;
        data.sort("time", true);
        rezult.append("Отчет о состоянии переменных\n");
        rezult.append("за период с " + Util.dateToStr(from.getTime()) + " по " + Util.dateToStr(to.getTime()) + "\n");
        //rezult.append("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\n");
        rezult.append(colon("Счетчик", withCount));
        rezult.append(colon("         Время               ", withTime));
        for (DataRecord ds : setup)
        {
            if (!ds.getBoolean("choice"))
            {
                continue;
            }
            line.put(ds.getString("name"), Float.NaN);
            rezult.append(colon(ds.getString("name"), withValue));
        }
        rezult.append("\n");
        time = from;
        int istart = 0;
        DataRecord rd;
        while (true)
        {
            if(istart>=data.getRecordCount()) break;
            rd = data.getRecord(istart++);
            if (time.getTime() != rd.getDate("time").getTime())
            {
                writeLine();
                time = new Date(rd.getDate("time").getTime());
            }
            if (line.containsKey(rd.getString("name")))
            {
                line.put(rd.getString("name"), rd.getFloat("value"));
            }

        }
        writeLine();
        time=new Date(System.currentTimeMillis());
        rezult.append("Отчет создан "+time.toString()+"\n");
            JTextArea txta=new JTextArea();
            txta.setEditable(true);
            txta.setFont(new Font("Dialog", Font.PLAIN, 12));
            txta.setColumns(1);
            txta.setRows(100);
            txta.setText(rezult.toString());
            JScrollPane txts=new JScrollPane();
            txts.setViewportView(txta);
            central.add(txts);
        work=false;
    }
    public boolean isWorking(){
        return work;
    }
    private void writeLine()
    {
        rezult.append(colon(Integer.toString(count++), withCount));
        rezult.append(colon(Util.dateToStr(time.getTime()), withTime));
        for (DataRecord ds : setup)
        {
            if (!ds.getBoolean("choice"))
            {
                continue;
            }
            Float f = line.get(ds.getString("name"));
            rezult.append(colon(f.toString(), withValue));
        }
        rezult.append("\n");
    }

    private String colon(String str, int len)
    {
        if (str.length() > len)
        {
            str=str.substring(0, len);
            return str + "|";
        }
        while (str.length() < len)
        {
            str += " ";
        }
        return str + "|";
    }

    public String getText()
    {
        return rezult.toString();
    }
}

