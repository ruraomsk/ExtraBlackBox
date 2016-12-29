/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.util.Enumeration;
import javax.swing.tree.*;
import ruraomsk.list.ru.strongsql.DescrValue;
import ruraomsk.list.ru.strongsql.ParamSQL;
import ruraomsk.list.ru.strongsql.StrongSql;

/**
 *
 * @author rura
 */
public class MainTree {

    DefaultTreeModel treeModel;
    CheckBoxTree tree;

    public MainTree(DataTable servers) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Выбор переменных");

        for (DataRecord dr : servers) {
            DefaultMutableTreeNode level1 = new DefaultMutableTreeNode(dr.getString("server"));
            root.add(level1);
            ParamSQL param = new ParamSQL();
            param.JDBCDriver = dr.getString("JDBC");
            param.url = dr.getString("url");
            param.user = dr.getString("user");
            param.password = dr.getString("password");
            StrongSql tsql=new StrongSql(param, true);
            if(!tsql.isconnected()) continue;
            for (String base : tsql.getBases().keySet()) {
                param.myDB = base;
                DefaultMutableTreeNode level2 = new DefaultMutableTreeNode(param.myDB);
                level1.add(level2);
                StrongSql sql = new StrongSql(param);
                if (sql.isconnected()) {
                    for (DescrValue dv : sql.getNames().values()) {
                        DefaultMutableTreeNode level3 = new DefaultMutableTreeNode(new CheckBoxElement(false, dv.getName()));
                        level2.add(level3);
                    }
                }
            }
        }
        treeModel = new DefaultTreeModel(root, true);
        tree = new CheckBoxTree(treeModel);
    }

    public CheckBoxTree getJTree() {
        return tree;
    }

    /**
     *
     */
    public DataTable getChoiceTable() {
        DataTable result = NeedsTables.mkChoiceTable();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration servers = root.children();
        while (servers.hasMoreElements()) {
            DefaultMutableTreeNode server = (DefaultMutableTreeNode) servers.nextElement();
            String tserver = (String) server.getUserObject();
//            System.err.println(tserver);
            Enumeration bases = server.children();
            while (bases.hasMoreElements()) {
                DefaultMutableTreeNode base = (DefaultMutableTreeNode) bases.nextElement();
                String tbase = (String) base.getUserObject();
                Enumeration names = base.children();
//                System.err.println(tbase);

                while (names.hasMoreElements()) {
                    DefaultMutableTreeNode name = (DefaultMutableTreeNode) names.nextElement();
                    Object data = ((DefaultMutableTreeNode) name).getUserObject();
                    // Проверка, являются ли данные CheckBoxElement
                    if (data instanceof CheckBoxElement) {
                        CheckBoxElement element = (CheckBoxElement) data;
                        if (element.selected) {
                            DataRecord dr = result.addRecord();
                            dr.setValue("server", tserver);
                            dr.setValue("base", tbase);
                            dr.setValue("name", element.text);

                        }
                    }
                }
            }
        }
        return result;
    }
}
