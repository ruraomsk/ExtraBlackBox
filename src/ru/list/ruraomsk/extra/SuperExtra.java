/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataTable;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import java.awt.Dimension;
import java.awt.DisplayMode;
import static java.awt.EventQueue.invokeLater;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.min;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jfree.chart.ChartPanel;
import ru.list.ruraomsk.editDT.EditDT;

/**
 *
 * @author rura
 */
public class SuperExtra {

    static public JTextArea Message;
    static JFrame frame;
    public static JPanel central;
    static JMenuBar jmenu;
    static String namefileservers = "servers.xml";
    static String namefileresult = "result.xml";
    static String namefilesetup = "setup.xml";
    static String startmessage;
    public static MainTree jtree;
    static JPanel pan;
    static JMenuBar menu = new JMenuBar();
    public static DataTable choiceTable = null;
    public static Date datefrom;
    public static Date dateto;
    public static String headerFrame;
    public static final long DEF_INTERVAL = 15 * 60 * 1000L;
    public static DataTable resultTable = null;
    public static DataTable setupTable = null;
    static EditDT editsetup = null;
    static EditDT editservers = null;
    public static DataTable serversTable = null;
    static ChartPanel chp=null;
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        Long now = currentTimeMillis();
        dateto = new Date(now);
        datefrom = new Date(now - DEF_INTERVAL);
        headerFrame = "Работа с данными за период с " + Util.dateToStr(datefrom.getTime()) + " по " + Util.dateToStr(dateto.getTime());
        frame = new JFrame();
        frame.setTitle(headerFrame);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setMinimumSize(getScreenSize());

        makeNeedTables();
        initScreen();
        initMenu();

        frame.add(pan);
        frame.setLocation(25, 25);
        frame.pack();
        appendMessage(startmessage);
        appendMessage("Программа к работе готова");
        invokeLater(() -> {
            frame.setVisible(true);
        });
        while (true) {
            sleep(1000L);
            if (editsetup != null) {
                if (editsetup.isFinished()) {
                    setupTable = editsetup.getDataTable();
                    appendMessage("Настройки графиков сохранены");
                }
            }
            if (editservers != null) {
                if (editservers.isFinished()) {
                    serversTable = editservers.getDataTable();
                    NeedsTables.saveTables(serversTable, namefileservers);
                    appendMessage("Настройки серверов сохранены");
                }
            }
        }

    }

    public static void appendMessage(String message) {
        Date date = new Date(System.currentTimeMillis());
        Message.append(Util.dateToStr(date.getTime()) + "\t" + message + "\n");
    }

    private static void makeNeedTables() {
        serversTable = NeedsTables.loadTables(namefileservers);
        if (serversTable == null) {
            serversTable = NeedsTables.mkServersTable();
            NeedsTables.saveTables(serversTable, namefileservers);
            startmessage = "Описание серверов было создано...";
        } else {
            startmessage = "Описание серверов загружено...";
        }
    }

    private static void initScreen() {
        Message = new JTextArea();
        Message.setColumns(1);
        Message.setRows(4);
        Message.setEditable(false);
        JScrollPane jScrollMessage = new JScrollPane();
        jScrollMessage.setViewportView(Message);
        jtree = new MainTree(serversTable);
        JScrollPane jScrollTree = new JScrollPane(jtree.getJTree());
        central = new JPanel();
        pan = new JPanel(new BorderLayout());
        pan.add(jScrollMessage, SOUTH);
        pan.add(jScrollTree, WEST);
        pan.add(central, CENTER);
        editservers = new EditDT(central, serversTable, false);
    }

    private static Dimension getScreenSize() {

        GraphicsEnvironment environment = getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = environment.getScreenDevices();
        // Get size of each screen
        int screenWidth = MAX_VALUE;
        int screenHeight = MAX_VALUE;
        for (GraphicsDevice device : devices) {
            DisplayMode dmode = device.getDisplayMode();
            screenWidth = min(screenWidth, dmode.getWidth());
            screenHeight = min(screenHeight, dmode.getHeight());
        }
        return new Dimension(screenWidth - 50, screenHeight - 50);
    }

    private static void initMenu() {
        menu = new JMenuBar();
        JMenu workVars = new JMenu("Переменные");
        JMenuItem choiceMenu = new JMenuItem("Принять выбор");
        choiceMenu.addActionListener((ActionEvent evt) -> {
            choiceTable = jtree.getChoiceTable();
            String message;
            if (choiceTable.getRecordCount() > 0) {
                message = "Выбрано полей=" + Integer.toString(choiceTable.getRecordCount());
                central.removeAll();
            } else {
                message = "Ничего не выбрано!";
                choiceTable = null;
            }
            appendMessage(message);
        });
        workVars.add(choiceMenu);
        JMenuItem viewChoiceMenu = new JMenuItem("Посмотреть выбранные переменные");
        viewChoiceMenu.addActionListener((ActionEvent evt) -> {
            if (choiceTable == null) {
                return;
            }
            if (choiceTable.getRecordCount() == 0) {
                return;
            }
//            removeTab("Выбор")
            frame.setVisible(false);
            central.removeAll();
            new EditDT(central, choiceTable, true);
            frame.setVisible(true);
//            pan.add(central,CENTER);
//            frame.repaint();
        });
        workVars.add(viewChoiceMenu);

        menu.add(workVars);
        JMenu workDataBase = new JMenu("Базы данных");
        JMenuItem setTimeMenu = new JMenuItem("Задать период и выбрать данные");
        setTimeMenu.addActionListener((ActionEvent evt) -> {
            new ChangeInterval(frame, 400, 200);
            headerFrame = "Работа с данными за период с " + Util.dateToStr(datefrom.getTime()) + " по " + Util.dateToStr(dateto.getTime());
            frame.setTitle(headerFrame);
        });
        workDataBase.add(setTimeMenu);

        JMenuItem viewResult = new JMenuItem("Посмотреть результат");
        viewResult.addActionListener((ActionEvent evt) -> {
            if (resultTable == null) {
                return;
            }
            if (resultTable.getRecordCount() == 0) {
                return;
            }
            central.removeAll();
            frame.setVisible(false);
            new EditDT(central, resultTable, true);
            frame.setVisible(true);
        });
        workDataBase.add(viewResult);
        JMenuItem viewSetup = new JMenuItem("Посмотреть сводную таблицу");
        viewSetup.addActionListener((ActionEvent evt) -> {
            if (setupTable == null) {
                return;
            }
            if (setupTable.getRecordCount() == 0) {
                return;
            }
            central.removeAll();
            frame.setVisible(false);
            editsetup = new EditDT(central, setupTable, false);
            frame.setVisible(true);
        });
        workDataBase.add(viewSetup);
        JMenuItem saveData = new JMenuItem("Сохранить результат");
        saveData.addActionListener((ActionEvent evt) -> {
            if (setupTable == null) {
                return;
            }
            if (setupTable.getRecordCount() == 0) {
                return;
            }
            NeedsTables.saveTables(resultTable, namefileresult);
            NeedsTables.saveTables(setupTable, namefilesetup);
            appendMessage("Таблицы сохранены");
        });
        workDataBase.add(saveData);
        JMenuItem loadData = new JMenuItem("Загрузить результат");
        loadData.addActionListener((ActionEvent evt) -> {
            resultTable = NeedsTables.loadTables(namefileresult);
            setupTable = NeedsTables.loadTables(namefilesetup);
            appendMessage("Таблицы загружены");
        });
        workDataBase.add(loadData);
        menu.add(workDataBase);
        JMenu reportMenu = new JMenu("Отчеты");
        JMenuItem graphMenu = new JMenuItem("Графики");
        graphMenu.addActionListener((ActionEvent evt) -> {
            if(resultTable==null) return;
            SerialChart sc = new SerialChart(resultTable, setupTable);
            while(sc.isWorking()){
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            chp = new ChartPanel(sc.chart);
            central.removeAll();
            frame.setVisible(false);
            central.add(chp);
            central.repaint();
            frame.setVisible(true);
        });
        reportMenu.add(graphMenu);
        JMenuItem printMenu = new JMenuItem("Таблица");
        printMenu.addActionListener((ActionEvent evt) -> {
            if(resultTable==null) return;
            frame.setVisible(false);
            central.removeAll();
            TextReport txtr=new TextReport(central,resultTable, setupTable, datefrom, dateto);
            while(txtr.isWorking()){
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            frame.setVisible(true);
        });
        reportMenu.add(printMenu);
        menu.add(reportMenu);
        JMenu progMenu = new JMenu("Программа");
        JMenuItem exitMenu = new JMenuItem("Выход");
        exitMenu.addActionListener((ActionEvent evt) -> {
//            NeedsTables.saveTables(serversTable, namefileservers);
            exit(0);
        });
        progMenu.add(exitMenu);
        menu.add(progMenu);
        frame.setJMenuBar(menu);
    }

}
