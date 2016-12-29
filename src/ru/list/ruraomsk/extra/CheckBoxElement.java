/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.list.ruraomsk.extra;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class CheckBoxElement {
    // Данные узла

    public boolean selected;
    public String text;
    // Конструктор

    public CheckBoxElement(boolean selected, String text) {
        this.selected = selected;
        this.text = text;
    }
}
