/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.control.cell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComboBoxTreeTableCellTest {
    
    private StringConverter<Object> converter;
    
    @Before public void setup() {
        converter = new StringConverter<Object>() {
            @Override public String toString(Object object) {
                return null;
            }

            @Override public Object fromString(String string) {
                return null;
            }
        };
    }
    
    /**************************************************************************
     * 
     * Test for public static Callback<TreeTableColumn<T>, TreeTableCell<T>> forTreeTableColumn(T... items)
     * 
     **************************************************************************/

    
    @Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryIsNotNull() {
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();
        assertNotNull(cellFactory);
    }
    
    @Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryCreatesCells() {
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();
        
        TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
        ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
        assertNotNull(cell);
    }

    @Test public void testStatic_forTreeTableColumn_noArgs_ensureCellHasNonNullStringConverter() {
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();

        TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
        ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
        assertNotNull(cell.getConverter());
    }
    
    
    
    /**************************************************************************
     * 
     * Test for public static <T> Callback<TreeTableColumn<T>, TreeTableCell<T>> forTreeTableColumn(
     *       final ObservableList<T> items)
     * 
     **************************************************************************/

    @Test public void testStatic_forTreeTableColumn_items_ensureSuccessWhenItemsIsNull() {
        ObservableList<String> items = null;
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
        assertNotNull(cellFactory);
    }

    @Test public void testStatic_forTreeTableColumn_items_ensureCellFactoryIsNotNull() {
        ObservableList<String> items = FXCollections.emptyObservableList();
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
        assertNotNull(cellFactory);
    }

    @Test public void testStatic_forTreeTableColumn_items_ensureCellFactoryCreatesCells() {
        ObservableList<String> items = FXCollections.emptyObservableList();
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);

        TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
        ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
        assertNotNull(cell);
    }

    @Test public void testStatic_forTreeTableColumn_items_ensureCellHasNonNullStringConverter() {
        ObservableList<String> items = FXCollections.emptyObservableList();
        Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);

        TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
        ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
        assertNotNull(cell.getConverter());
    }

    /**************************************************************************
     * 
     * Constructor tests for default constructor
     * 
     **************************************************************************/

    @Test public void testConstructor_noArgs_defaultStringConverterIsNotNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        assertNotNull(cell.getConverter());
    }
    
    @Test public void testConstructor_noArgs_defaultStyleClass() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        assertTrue(cell.getStyleClass().contains("combo-box-tree-table-cell"));
    }
    
    @Test public void testConstructor_noArgs_defaultGraphicIsNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        assertNull(cell.getGraphic());
    }
    
    
    /**************************************************************************
     * 
     * Constructor tests for one-arg constructor
     * 
     **************************************************************************/
    
    @Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
        assertNotNull(cell.getConverter());
    }
    
    @Test public void testConstructor_converter_defaultStyleClass() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
        assertTrue(cell.getStyleClass().contains("combo-box-tree-table-cell"));
    }
    
    @Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
        assertNull(cell.getGraphic());
    }
    
    
    /**************************************************************************
     * 
     * updateItem tests
     * 
     **************************************************************************/

    @Test public void test_updateItem_isEmpty_graphicIsNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        cell.updateItem("TEST", true);
        assertNull(cell.getGraphic());
    }
    
    @Test public void test_updateItem_isEmpty_textIsNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        cell.updateItem("TEST", true);
        assertNull(cell.getText());
    }
    
    @Test public void test_updateItem_isNotEmpty_textIsNotNull() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        cell.updateItem("TEST", false);
        assertNotNull(cell.getText());
        assertEquals("TEST", cell.getText());
    }
    
    @Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        cell.setConverter(null);
        cell.updateItem("TEST", false);
        assertNotNull(cell.getText());
        assertEquals("TEST", cell.getText());
    }
    
    @Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
        ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
        cell.setConverter(
                new StringConverter<Object>() {
                    @Override public Object fromString(String string) {
                        return null;
                    }

                    @Override public String toString(Object object) {
                        return "CONVERTED";
                    }
                });
        cell.updateItem("TEST", false);
        assertNotNull(cell.getText());
        assertEquals("CONVERTED", cell.getText());
    }


//    /**************************************************************************
//     *
//     * editing tests
//     *
//     **************************************************************************/
//
//    // --- is Empty
//    @Test public void test_startEdit_cellEditableIsFalse_isEmpty() {
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.setEditable(false);
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    @Test public void test_startEdit_listViewEditableIsFalse_isEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(false);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateTableView(listView);
//
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void test_startEdit_cellEditableIsTrue_listViewIsNull_isEmpty() {
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.setEditable(true);
//        cell.startEdit();
//    }
//
//    @Test public void test_startEdit_listViewEditableIsTrue_isEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(true);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateTableView(listView);
//
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    @Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(true);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.setEditable(true);
//        cell.updateTableView(listView);
//
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    // --- is Not Empty
//    @Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateItem("TEST", false);
//        cell.setEditable(false);
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    @Test public void test_startEdit_listViewEditableIsFalse_isNotEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(false);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateTableView(listView);
//        cell.updateItem("TEST", false);
//
//        cell.startEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void test_startEdit_cellEditableIsTrue_listViewIsNull_isNotEmpty() {
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateItem("TEST", false);
//        cell.setEditable(true);
//        cell.startEdit();
//    }
//
//    @Test public void test_startEdit_listViewEditableIsTrue_isNotEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(true);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateTableView(listView);
//        cell.updateItem("TEST", false);
//
//        cell.startEdit();
//        assertTrue(cell.isEditing());
//        assertNotNull(cell.getGraphic());
//    }
//
//    @Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
//        TableView listView = new TableView();
//        listView.setEditable(true);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.setEditable(true);
//        cell.updateTableView(listView);
//        cell.updateItem("TEST", false);
//
//        cell.startEdit();
//        assertTrue(cell.isEditing());
//        assertNotNull(cell.getGraphic());
//    }
//
//    // --- cancel edit
//    @Test public void test_cancelEdit() {
//        TableView listView = new TableView();
//        listView.setEditable(true);
//        ComboBoxTreeTableCell<Object> cell = new ComboBoxTreeTableCell<>();
//        cell.updateTableView(listView);
//        cell.updateItem("TEST", false);
//
//        cell.startEdit();
//        assertTrue(cell.isEditing());
//        assertNotNull(cell.getGraphic());
//
//        cell.cancelEdit();
//        assertFalse(cell.isEditing());
//        assertNull(cell.getGraphic());
//    }
}
