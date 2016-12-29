/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.awt.*;
import static java.awt.Color.WHITE;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getTimeInstance;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class DateTimePicker extends JXDatePicker {
    private JSpinner timeSpinner;
    private JPanel timePanel;
    private DateFormat timeFormat;

    public DateTimePicker() {
        super();
        getMonthView().setSelectionModel(new SingleDaySelectionModel());
    }

    public DateTimePicker( Date d ) {
        this();
        setDate(d);
    }

    public void commitEdit() throws ParseException {
        commitTime();
        super.commitEdit();
    }

    public void cancelEdit() {
        super.cancelEdit();
        setTimeSpinners();
    }

    @Override
    public JPanel getLinkPanel() {
        super.getLinkPanel();
        if( timePanel == null ) {
            timePanel = createTimePanel();
        }
        setTimeSpinners();
        return timePanel;
    }

    private JPanel createTimePanel() {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new FlowLayout());
        //newPanel.add(panelOriginal);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(dateModel);
        if( timeFormat == null ) timeFormat = getTimeInstance(SHORT);
        updateTextFieldFormat();
        newPanel.add(new JLabel( "Time:" ) );
        newPanel.add(timeSpinner);
        newPanel.setBackground(WHITE);
        return newPanel;
    }

    private void updateTextFieldFormat() {
        if( timeSpinner == null ) return;
        JFormattedTextField tf = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
        DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
        // Change the date format to only show the hours
        formatter.setFormat( timeFormat );
    }

    private void commitTime() {
        Date date = getDate();
        if (date != null) {
            Date time = (Date) timeSpinner.getValue();
            GregorianCalendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime( time );

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(HOUR_OF_DAY, timeCalendar.get(HOUR_OF_DAY) );
            calendar.set(MINUTE, timeCalendar.get(MINUTE) );
            calendar.set(SECOND, 0);
            calendar.set(MILLISECOND, 0);

            Date newDate = calendar.getTime();
            setDate(newDate);
        }

    }

    private void setTimeSpinners() {
        Date date = getDate();
        if (date != null) {
            timeSpinner.setValue( date );
        }
    }

    public DateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(DateFormat timeFormat) {
        this.timeFormat = timeFormat;
        updateTextFieldFormat();
    }
}