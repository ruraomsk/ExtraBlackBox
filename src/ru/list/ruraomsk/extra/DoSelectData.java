/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.awt.Color;
import static java.awt.Color.*;
import static java.lang.Float.MAX_VALUE;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import ruraomsk.list.ru.strongsql.DescrValue;
import ruraomsk.list.ru.strongsql.ParamSQL;
import ruraomsk.list.ru.strongsql.SetValue;
import ruraomsk.list.ru.strongsql.StrongSql;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class DoSelectData extends SwingWorker<Integer, Integer> {

    DataTable result = null;
    DataTable setup = null;
    DataTable serveses;
    DataTable choiceTable;
    JProgressBar progBar;
    private final Color[] colordefault
            = {
                RED, BLUE, CYAN, GRAY, GREEN, MAGENTA, ORANGE, PINK, YELLOW};
    int color = -1;

    public DoSelectData(DataTable serveses, DataTable choiceTable, JProgressBar progBar) {
        this.serveses = serveses;
        this.choiceTable = choiceTable;
        this.progBar = progBar;

    }

    public DataTable getResult() {
        return result;
    }

    public DataTable getSetup() {
        return setup;
    }

    private Color nextColor() {
        color++;
        if (color >= colordefault.length) {
            color = 0;
        }
        return colordefault[color];
    }

    @Override
    protected Integer doInBackground() throws Exception {

        result = NeedsTables.mkResultDataTable();
        setup = NeedsTables.mkSetupDataTable();
        ParamSQL param = new ParamSQL();
        String fullname = "";
        progBar.setIndeterminate(false);
        progBar.setMinimum(0);
        progBar.setMaximum(choiceTable.getRecordCount());
        int count = 0;
        for (DataRecord chrec : choiceTable) {
            progBar.setValue(count++);
            StrongSql sql = null;
            for (DataRecord srvrec : serveses) {
                if (chrec.getString("server").equals(srvrec.getString("server"))) {
                    param.JDBCDriver = srvrec.getString("JDBC");
                    param.url = srvrec.getString("url");
                    param.user = srvrec.getString("user");
                    param.password = srvrec.getString("password");
                    param.myDB = chrec.getString("base");
                    sql = new StrongSql(param);
                    fullname =  chrec.getString("name")+ "/" + chrec.getString("base") + "/" +chrec.getString("server") ;
                    break;
                }
            }
            if (sql == null) {
                break;
            }
            if (!sql.isconnected()) {
                break;
            }
            DescrValue dsValue = sql.getNames().get(chrec.getString("name"));
            if (dsValue == null) {
                break;
            }
            ArrayList<SetValue> svm = sql.seekData(new Timestamp(SuperExtra.datefrom.getTime()), new Timestamp(SuperExtra.dateto.getTime()), dsValue.getId());
            float max = -MAX_VALUE;
            float min = MAX_VALUE;
            DataRecord recset = setup.addRecord();
            recset.setValue("name", fullname);
            recset.setValue("choice",false);
            for (SetValue sv : svm) {
                DataRecord res = result.addRecord();
                res.setValue("name", fullname);
                float value = 0.0f;
                switch (dsValue.getType()) {
                    case 0:
                        value = (boolean) sv.getValue() ? 1.0f : 0.0f;
                        break;
                    case 1:
                        value = (float) ((int) sv.getValue());//&0xFFFF);
                        break;
                    case 2:
                        value = (float) sv.getValue();
                        break;
                    case 3:
                        value = (float) ((long) sv.getValue());
                        break;
                    case 4:
                        value=(float)( (byte)sv.getValue()&0xff);
                        break;
                }
                res.setValue("value", value);
                res.setValue("time", new Date(sv.getTime()));
                max = max<value?value:max;
                min = min>value?value:min;
                recset.setValue("choice",true);
                
            }
            recset.setValue("max", max);
            recset.setValue("min", min);
            recset.setValue("color", nextColor());
            sql.disconnect();
        }
        progBar.setValue(progBar.getMaximum());
        return 1;
    }

}
