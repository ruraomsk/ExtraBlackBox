/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package ru.list.ruraomsk.extra;

import java.awt.BorderLayout;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class ChangeInterval extends JDialog{
DoSelectData doSelectData;
    public ChangeInterval(JFrame owner,int posx,int posy) {
        super(owner,"Измените период",true);
        this.setSize(400, 100);
        JPanel panel = new JPanel(new BorderLayout());
        JProgressBar progress=new JProgressBar();
        panel.add(progress, NORTH);
        DateTimePicker dateTimePickerFrom=new DateTimePicker();
        dateTimePickerFrom.setFormats(getDateTimeInstance(SHORT, MEDIUM) );
        dateTimePickerFrom.setTimeFormat(getTimeInstance(MEDIUM) );
        dateTimePickerFrom.setDate(SuperExtra.datefrom);

        DateTimePicker dateTimePickerTo=new DateTimePicker();
        dateTimePickerTo.setFormats(getDateTimeInstance(SHORT, MEDIUM) );
        dateTimePickerTo.setTimeFormat(getTimeInstance(MEDIUM) );
        dateTimePickerTo.setDate(SuperExtra.dateto);

        panel.add(dateTimePickerFrom, WEST);
        panel.add(dateTimePickerTo, EAST);
        JPanel butons=new JPanel(new FlowLayout());
        JButton bReady=new JButton("Готово");
        bReady.addActionListener((ActionEvent e) -> {
            SuperExtra.resultTable=doSelectData.getResult();
            SuperExtra.setupTable=doSelectData.getSetup();
            SuperExtra.appendMessage("Данные выбраны из базы данных");
            this.setVisible(false);
        });
        bReady.setVisible(false);
        JButton bOk=new JButton("Принять");
        bOk.addActionListener((ActionEvent e) -> {
            SuperExtra.datefrom=dateTimePickerFrom.getDate();
            SuperExtra.dateto=dateTimePickerTo.getDate();
            doSelectData=new DoSelectData(SuperExtra.serversTable,SuperExtra.choiceTable,progress);
            doSelectData.execute();
            bOk.setVisible(false);
            while (!doSelectData.isDone()){
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    break;
                }
            }
                    
            bReady.setVisible(true);
            
            SuperExtra.appendMessage("Изменен интервал даты и времени");
        });
        JButton bCancel=new JButton("Отменить");
        bCancel.addActionListener((ActionEvent e) -> {
            this.setVisible(false);
        });
        
        butons.add(bOk);
        butons.add(bReady);
        butons.add(bCancel);
        panel.add(butons, SOUTH);
        this.add(panel);
        this.setLocation(posx, posy);
        this.setVisible(true);
    }
    
}
