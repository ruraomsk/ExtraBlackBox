/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import static com.tibbo.aggregate.common.datatable.FieldFormat.create;
import com.tibbo.aggregate.common.datatable.TableFormat;
import static ru.list.ruraomsk.extra.Util.loadXML;
import static ru.list.ruraomsk.extra.Util.tableToXML;

/**
 * Класс по созданию загрузке и прочему необходимых таблиц для программы
 *
 * @author Yury Rusinov <ruraomsk@list.ru Automatics-A Omsk>
 */
public class NeedsTables {

//    DataTable serversTable = null;
//    DataTable databasesTable = null;

    /**
     * Создание таблицы описаний серверов и таблицы описаний баз данных сервера
     * не забываем что в в базе три таблицы XXX_data XXX_head XXX_header
     */
    public static final DataTable mkServersTable() {
        TableFormat VFT_Servers = new TableFormat();
        VFT_Servers.addField(create("<server><S><D=Имя Сервера>"));
        VFT_Servers.addField(create("<description><S><D=Описание Сервера>"));
        VFT_Servers.addField(create("<JDBC><S><D=Драйвер базы данных сервера>"));
        VFT_Servers.addField(create("<url><S><D=Адрес базы данных сервера>"));
        VFT_Servers.addField(create("<user><S><D=Пользователь>"));
        VFT_Servers.addField(create("<password><S><D=Пароль>"));
        DataTable serversTable = new DataTable(VFT_Servers);
        DataRecord dr = serversTable.addRecord();
        dr.setValue("server","local");
        dr.setValue("description", "по умолчанию");
        dr.setValue("JDBC", "org.postgresql.Driver");
        dr.setValue("url", "jdbc:postgresql://127.0.0.1:5433/testbase");
        dr.setValue("user", "postgres");
        dr.setValue("password", "162747");
        return serversTable;
    }
    public static final DataTable mkChoiceTable() {
        TableFormat VFT_Table = new TableFormat();
        VFT_Table.addField(create("<server><S><D=Имя Сервера>"));
        VFT_Table.addField(create("<base><S><D=Таблица>"));
        VFT_Table.addField(create("<name><S><D=Переменная>"));
        return new DataTable(VFT_Table);
    }
    public static final DataTable mkResultDataTable(){
        TableFormat VFT_Table = new TableFormat();
        VFT_Table.addField(create("<name><S><D=Переменная>"));
        VFT_Table.addField(create("<time><D><D=Время>"));
        VFT_Table.addField(create("<value><F><D=Значение>"));
        return new DataTable(VFT_Table);
    }
    public static final DataTable mkSetupDataTable(){
        TableFormat VFT_Table = new TableFormat();
        VFT_Table.addField(create("<name><S><D=Имя>"));
        VFT_Table.addField(create("<max><F><D=Максимум>"));
        VFT_Table.addField(create("<min><F><D=Минимум>"));
        VFT_Table.addField(create("<choice><B><A=true><D=Выбор>"));
        VFT_Table.addField(create("<color><C><D=Цвет>"));
        return new DataTable(VFT_Table);
    }
    /**
     * Загрузка таблицs
     *
     * @param nameServers - имя файла с описанием серверов
     * @return истина если загрузка прошла удачно
     */
    public static final DataTable loadTables(String nameServers) {
        DataTable serversTable = loadXML(nameServers);
        return serversTable;
    }

    /**
     * Сохранение всех таблиц
     *
     * @param nameServers - имя файла с описанием серверов
     * @return истина если все базы сохранены
     */
    public static final boolean saveTables(DataTable serversTable,String nameServers) {
        return tableToXML(serversTable, nameServers);
    }
}
