/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.datatable.DataTable;
import static com.tibbo.aggregate.common.datatable.EncodingUtils.decodeFromXML;
import static com.tibbo.aggregate.common.datatable.EncodingUtils.encodeToXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.err;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * Разные утилиты и константы для работы
 *
 * @author Yury Rusinov <ruraomsk@list.ru Automatics-A Omsk>
 */
public class Util {

    /**
     * Сохраняет DataTable в файл формата XML
     *
     * @param table
     * @param namefile
     * @return истина если удачно загрузилось
     */
    public static boolean tableToXML(DataTable table, String namefile) {
        FileOutputStream rezFile = null;
        try {
            String srez = encodeToXML(table);
            rezFile = new FileOutputStream(new File(namefile));
            rezFile.write(srez.getBytes());
            rezFile.close();
            return true;
        } catch (IOException | ParserConfigurationException | ContextException | DOMException ex) {
            err.println(ex.getMessage());
        } finally {
            try {
                rezFile.close();
            } catch (IOException ex) {
                err.println(ex.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Загружает файл формата XML DataTable
     *
     * @param xmlfile
     * @return
     */
    public static DataTable loadXML(String xmlfile) {
        InputStreamReader rezFile = null;
        try {
            char[] brez = new char[MAX_BUFFER];
            rezFile = new InputStreamReader(new FileInputStream(new File(xmlfile)));
            StringBuilder sb = new StringBuilder();
            int len;
            while ((len = rezFile.read(brez, 0, brez.length)) > 0) {
                sb.append(brez, 0, len);
                //System.err.print("=");
            }
            String ss = sb.toString();
            return decodeFromXML(ss);
        } catch (IOException | ParserConfigurationException | ContextException | DOMException | IllegalArgumentException | SAXException ex) {
            err.println(ex.getMessage());
        } finally {
            try {
                if (rezFile != null) {
                    rezFile.close();
                }
            } catch (IOException ex) {
                err.println(ex.getMessage());
            }
        }
        return null;
    }

    public static final String dateToStr(long date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.S ");
        return df.format(new Date(date));
    }
    static int MAX_BUFFER = 32000;
}
