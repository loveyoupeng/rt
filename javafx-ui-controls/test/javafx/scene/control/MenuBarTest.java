/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;

import com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.MenuBarMenuButtonRetriever;
import com.sun.javafx.scene.control.skin.MenuBarSkin;
import com.sun.javafx.tk.Toolkit;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author lubermud
 */
public class MenuBarTest {
    private MenuBar menuBar;
    private Toolkit tk;
    private Scene scene;
    private Stage stage;

    @Before public void setup() {
        tk = (StubToolkit)Toolkit.getToolkit();
        menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(false);
    }
    
    protected void startApp(Parent root) {
        scene = new Scene(root,800,600);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
        tk.firePulse();
    }

    @Test public void defaultConstructorHasFalseFocusTraversable() {
        assertFalse(menuBar.isFocusTraversable());
    }

    @Test public void defaultConstructorButSetTrueFocusTraversable() {
            menuBar.setFocusTraversable(true);
        assertTrue(menuBar.isFocusTraversable());
    }

    @Test public void getMenusHasSizeZero() {
        assertEquals(0, menuBar.getMenus().size());
    }

    @Test public void getMenusIsAddable() {
        menuBar.getMenus().add(new Menu(""));
        assertTrue(menuBar.getMenus().size() > 0);
    }

    @Test public void getMenusIsClearable() {
        menuBar.getMenus().add(new Menu(""));
        menuBar.getMenus().clear();
        assertEquals(0, menuBar.getMenus().size());
    }
    
     @Test public void getMenusIsRemovable() {
           menuBar.getMenus().add(new Menu("blah"));
           menuBar.getMenus().add(new Menu("foo"));
           menuBar.getMenus().remove(0);
           assertEquals(1, menuBar.getMenus().size());
     }
     
    @Test public void testMenuShowHideWithMenuBarWithXYTranslation() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        AnchorPane root = new AnchorPane();
        Menu menu = new Menu("Menu");
        menu.getItems().add(new MenuItem("MenuItem"));
        menuBar.getMenus().add(menu);
        menuBar.setLayoutX(100);
        menuBar.setLayoutY(100);
        root.getChildren().add(menuBar);
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        
        double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
        double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
   
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        assertTrue(menu.isShowing());
        
    }

    @Test public void testSubMenuDismissalWithKeyNavigation() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        AnchorPane root = new AnchorPane();
        Menu menu = new Menu("Menu");
        Menu menu1 = new Menu("Menu With SubMenu");
        menu.getItems().add(menu1);

        MenuItem menuItem1 = new MenuItem("MenuItem1");
        MenuItem menuItem2 = new MenuItem("MenuItem2");
        menu1.getItems().addAll(menuItem1, menuItem2);
        
        menuBar.getMenus().add(menu);
        menuBar.setLayoutX(100);
        menuBar.setLayoutY(100);

        root.getChildren().add(menuBar);
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        
        double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
        double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
   
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        assertTrue(menu.isShowing());
         /* ------------------------------------------------------------------ */
        
        // Show subMenu
        ContextMenuContent menuContent = MenuBarMenuButtonRetriever.getMenuContent(mb); // ContextMenuContent
        Node displayNode = MenuBarMenuButtonRetriever.getDisplayNodeForMenuItem(menuContent, 0); // MenuItemContainer
        
        displayNode.getScene().getWindow().requestFocus();
        assertTrue(displayNode.getScene().getWindow().isFocused());
        
        displayNode.requestFocus(); // requestFocus on 1st Menu
        assertTrue(displayNode.isFocused());
        // update currentFocusedIndex
        MenuBarMenuButtonRetriever.setCurrentFocusedIndex(menuContent, 0);
        
        // fire KeyEvent (Enter) on menu1 to show submenu
        KeyEventFirer keyboard = new KeyEventFirer(menuContent);
        keyboard.doKeyPress(KeyCode.ENTER);
        tk.firePulse();     
        assertTrue(menu1.isShowing()); // subMenu is showing
        /* ------------------------------------------------------------------ */
        
        // Get 1st MenuItem from the submenu
        ContextMenuContent subMenuContent = MenuBarMenuButtonRetriever.getSubMenuContent(menuContent);
        subMenuContent.getScene().getWindow().requestFocus(); // requestFocus on submenu
        assertTrue(subMenuContent.getScene().getWindow().isFocused());
        
        displayNode = MenuBarMenuButtonRetriever.getDisplayNodeForMenuItem(subMenuContent, 0);
        displayNode.requestFocus();
        assertTrue(displayNode.isFocused());
        
        MenuBarMenuButtonRetriever.setCurrentFocusedIndex(subMenuContent, 0);
        // fire KeyEvent (Enter) on menuItem1 to hide all menus
        keyboard = new KeyEventFirer(subMenuContent);
        keyboard.doKeyPress(KeyCode.ENTER);
        tk.firePulse();
        
        // confirm all menus are closed. 
        assertTrue(!menu1.isShowing());
        assertTrue(!menu.isShowing());
    }
        
    @Test public void checkMenuBarMenusSelectionResetAfterMenuItemIsSelected() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        AnchorPane root = new AnchorPane();
        Menu menu = new Menu("Menu");
        MenuItem menuItem = new MenuItem("MenuItem");
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);
        menuBar.setLayoutX(100);
        menuBar.setLayoutY(100);

        root.getChildren().addAll(menuBar);
        
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        
        double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
        double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
   
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        assertTrue(menu.isShowing());
         /* ------------------------------------------------------------------ */
        
        // Show Menu
        ContextMenuContent menuContent = MenuBarMenuButtonRetriever.getMenuContent(mb); // ContextMenuContent
        Node displayNode = MenuBarMenuButtonRetriever.getDisplayNodeForMenuItem(menuContent, 0); // MenuItemContainer
        
        displayNode.getScene().getWindow().requestFocus();
        assertTrue(displayNode.getScene().getWindow().isFocused());
        
        displayNode.requestFocus(); // requestFocus on 1st Menu
        assertTrue(displayNode.isFocused());
        
        // fire KeyEvent (Enter) on menuitem 
        KeyEventFirer keyboard = new KeyEventFirer(menuContent);
        keyboard.doKeyPress(KeyCode.ENTER);
        tk.firePulse();     
        
        // confirm menu is closed. 
        assertTrue(!menu.isShowing());
        keyboard.doKeyPress(KeyCode.LEFT);
        tk.firePulse();
        
        // check if focusedMenuIndex is reset to -1 so navigation happens.
        int focusedIndex = MenuBarMenuButtonRetriever.getFocusedIndex(skin);
        assertEquals(focusedIndex, -1);
    }
    
    @Test public void testMenuOnShownEventFiringWithKeyNavigation() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        VBox root = new VBox();
        Menu menu = new Menu("Menu");

        MenuItem menuItem1 = new MenuItem("MenuItem1");
        MenuItem menuItem2 = new MenuItem("MenuItem2");
        menu.getItems().addAll(menuItem1, menuItem2);
        
        menuBar.getMenus().add(menu);
        menuBar.setLayoutX(100);
        menuBar.setLayoutY(100);
        
        final CheckBox cb = new CheckBox("showing");
        
        root.getChildren().addAll(cb,menuBar);
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        assertTrue(mb.getScene().getWindow().isFocused());
        

        KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
        keyboard.doKeyPress(KeyCode.TAB);
        tk.firePulse(); 
        mb.requestFocus();
        assertTrue(mb.isFocused());
        
        keyboard = new KeyEventFirer(mb);
        keyboard.doDownArrowPress();
        tk.firePulse();
        assertEquals(menu.showingProperty().get(), true);
    }
    
    @Test public void testKeyNavigationWithDisabledMenuItem() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        VBox root = new VBox();
        Menu menu1 = new Menu("Menu1");
        Menu menu2 = new Menu("Menu2");
        Menu menu3 = new Menu("Menu3");
        
        MenuItem menuItem1 = new MenuItem("MenuItem1");
        MenuItem menuItem2 = new MenuItem("MenuItem2");
        MenuItem menuItem3 = new MenuItem("MenuItem3");

        menu1.getItems().add(menuItem1);
        menu2.getItems().add(menuItem2);
        menu3.getItems().add(menuItem3);
        
        menuBar.getMenus().addAll(menu1, menu2, menu3);
        menu2.setDisable(true);
        
        root.getChildren().addAll(menuBar);
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        
        double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
        double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
   
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        assertTrue(menu1.isShowing());
        
        KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
        keyboard.doKeyPress(KeyCode.RIGHT);
        tk.firePulse(); 
        assertTrue(menu3.isShowing());
    }
    
    
     @Test public void testMenuOnShowingEventFiringWithMenuHideOperation() {
        final MouseEventGenerator generator = new MouseEventGenerator();
        VBox root = new VBox();
        Menu menu = new Menu("Menu");

        MenuItem menuItem1 = new MenuItem("MenuItem1");
        menu.getItems().addAll(menuItem1);
        
        menuBar.getMenus().add(menu);
        menuBar.setLayoutX(100);
        menuBar.setLayoutY(100);
        
        root.getChildren().addAll(menuBar);
        startApp(root);
        tk.firePulse();
        
        MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
        assertTrue(skin != null);
        double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
        double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
        
        boolean click = true;
        final Boolean firstClick = new Boolean(click);
        
        menu.setOnShowing(new EventHandler<Event>() {
            @Override public void handle(Event t) {
                // we should not get here when the menu is hidden
                assertEquals(firstClick.booleanValue(), true);
            }
        });
        
        MenuButton mb = MenuBarMenuButtonRetriever.getNodeForMenu(skin, 0);
        mb.getScene().getWindow().requestFocus();
        mb.requestFocus();
        assertTrue(mb.isFocused());
        // click on menu to show 
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        tk.firePulse(); 
        assertEquals(menu.showingProperty().get(), true);
        click = false;
        // click on menu to hide
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
        scene.impl_processMouseEvent(
            generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
        tk.firePulse(); 
        assertEquals(menu.showingProperty().get(), false);
    }
    
//    static final class MouseEventTracker {
//        private Node node;
//        
//        public MouseEventTracker(final Node node) {
//            this.node = node;
//            
//            node.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent t) {
//                    // println here to check if node received mouse event
//                }
//            });
//        }
//    }

}
