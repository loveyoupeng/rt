/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.scene.control.skin;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import com.sun.javafx.scene.control.behavior.TitledPaneBehavior;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TraversalEngine;
import com.sun.javafx.scene.traversal.TraverseListener;

public class TitledPaneSkin extends SkinBase<TitledPane, TitledPaneBehavior>  {

    public static final int MIN_HEADER_HEIGHT = 22;
    public static final Duration TRANSITION_DURATION = new Duration(350.0);

    private final HBox titleRegion;
    private final StackPane arrowRegion;
    private final Content contentRegion;
    private Timeline timeline;
    private double transitionStartValue;
    private Rectangle clipRect;
    private LabeledImpl label;

    public TitledPaneSkin(final TitledPane titledPane) {
        super(titledPane, new TitledPaneBehavior(titledPane));
        label = new LabeledImpl(titledPane);
        label.getStyleClass().add("text");

        clipRect = new Rectangle();
        setClip(clipRect);

        transitionStartValue = 0;
        titleRegion = new HBox();
        titleRegion.setFillHeight(false);
        titleRegion.setAlignment(Pos.CENTER_LEFT);
        titleRegion.getStyleClass().setAll("title");
        titleRegion.getChildren().clear();
        titleRegion.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                getBehavior().toggle();
             }
        });

        arrowRegion = new StackPane();
        arrowRegion.getStyleClass().setAll("arrow-button");

        StackPane arrow = new StackPane();
        arrow.getStyleClass().setAll("arrow");
        arrowRegion.getChildren().setAll(arrow);

        // title region consists of the title and the arrow regions
        updateTitleRegion();

        contentRegion = new Content(getSkinnable().getContent());
        contentRegion.getStyleClass().setAll("content");

        if (titledPane.isExpanded()) {
            setExpanded(titledPane.isExpanded());
        } else {
            setTransition(0.0f);
        }

        getChildren().setAll(contentRegion, titleRegion);

        registerChangeListener(titledPane.contentProperty(), "CONTENT");
        registerChangeListener(titledPane.expandedProperty(), "EXPANDED");
        registerChangeListener(titledPane.collapsibleProperty(), "COLLAPSIBLE");
    }

    public StackPane getContentRegion() {
        return contentRegion;
    }

    @Override protected void setWidth(double value) {
        super.setWidth(value);
        clipRect.setWidth(value);
    }

    @Override protected void setHeight(double value) {
        super.setHeight(value);
        clipRect.setHeight(value);
    }

    @Override
    protected void handleControlPropertyChanged(String property) {
        super.handleControlPropertyChanged(property);
        if (property == "CONTENT") {
            contentRegion.setContent(getSkinnable().getContent());
        } else if (property == "EXPANDED") {
            setExpanded(getSkinnable().isExpanded());
        } else if (property == "COLLAPSIBLE") {
            updateTitleRegion();
        }
    }

    private void updateTitleRegion() {
        titleRegion.getChildren().clear();

        if (getSkinnable().isCollapsible()) {
            titleRegion.getChildren().add(arrowRegion);
        }
        titleRegion.getChildren().add(label);
        titleRegion.setCursor(getSkinnable().isCollapsible() ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void setExpanded(boolean expanded) {
        if (! getSkinnable().isCollapsible()) {
            setTransition(1.0f);
            return;
        }

        // we need to perform the transition between expanded / hidden
        if (getSkinnable().isAnimated()) {
            transitionStartValue = getTransition();
            doAnimationTransition();
        } else {
            if (expanded) {
                setTransition(1.0f);
            } else {
                setTransition(0.0f);
            }
            contentRegion.setVisible(expanded);
            requestLayout();
        }
    }

    private DoubleProperty transition;
    private void setTransition(double value) { transitionProperty().set(value); }
    private double getTransition() { return transition == null ? 0.0 : transition.get(); }
    private DoubleProperty transitionProperty() {
        if (transition == null) {
            transition = new DoublePropertyBase() {
                @Override protected void invalidated() {
                    requestLayout();
                }

                @Override
                public Object getBean() {
                    return TitledPaneSkin.this;
                }

                @Override
                public String getName() {
                    return "transition";
                }
            };
        }
        return transition;
    }

    @Override protected void layoutChildren() {
        double w = snapSize(getWidth()) - (snapSpace(getInsets().getLeft()) + snapSpace(getInsets().getRight()));
        double h = snapSize(getHeight()) - (snapSpace(getInsets().getTop()) + snapSpace(getInsets().getBottom()));

        // header
        double headerHeight = Math.max(MIN_HEADER_HEIGHT, snapSize(titleRegion.prefHeight(-1)));

        titleRegion.resize(w, headerHeight);
        positionInArea(titleRegion, snapSpace(getInsets().getLeft()), snapSpace(getInsets().getTop()),
            w, headerHeight, 0, HPos.LEFT, VPos.CENTER);

        // content
        double contentWidth = w;
        double contentHeight = snapSize(contentRegion.prefHeight(-1));
        if (getSkinnable().getParent() != null && getSkinnable().getParent() instanceof AccordionSkin) {
            if (prefHeightFromAccordion != 0) {
                contentHeight = prefHeightFromAccordion - headerHeight;
            }
        }

        double y = snapSpace(getInsets().getTop() + headerHeight) - (contentHeight * (1 - getTransition()));

        ((Rectangle)contentRegion.getClip()).setY(contentHeight * (1 - getTransition()));
        contentRegion.resize(contentWidth, contentHeight);
        positionInArea(contentRegion, snapSpace(getInsets().getLeft()), snapSpace(y),
            w, contentHeight, /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computeMinWidth(double height) {
        return computePrefWidth(height);
    }

    @Override protected double computeMinHeight(double width) {
        return Math.max(MIN_HEADER_HEIGHT, snapSize(titleRegion.prefHeight(-1)));
    }

    @Override protected double computePrefWidth(double height) {
        double titleWidth = snapSize(titleRegion.prefWidth(height));
        double contentWidth = snapSize(contentRegion.prefWidth(height));

        return Math.max(titleWidth, contentWidth) + snapSpace(getInsets().getLeft()) + snapSpace(getInsets().getRight());
    }

    @Override protected double computePrefHeight(double width) {
        double headerHeight = Math.max(MIN_HEADER_HEIGHT, snapSize(titleRegion.prefHeight(-1)));
        double contentHeight = 0;
        if (getSkinnable().getParent() != null && getSkinnable().getParent() instanceof AccordionSkin) {
            contentHeight = contentRegion.prefHeight(-1);
        } else {
            contentHeight = contentRegion.prefHeight(-1) * getTransition();
        }
        return headerHeight + snapSize(contentHeight) + snapSpace(getInsets().getTop()) + snapSpace(getInsets().getBottom());
    }

    private double prefHeightFromAccordion = 0;
    void setPrefHeightFromAccordion(double height) {
        this.prefHeightFromAccordion = height;
    }

    double prefHeightFromAccordion() {
        double headerHeight = Math.max(MIN_HEADER_HEIGHT, snapSize(titleRegion.prefHeight(-1)));
        double contentHeight = (prefHeightFromAccordion - headerHeight) * getTransition();
        return headerHeight + snapSize(contentHeight) + snapSpace(getInsets().getTop()) + snapSpace(getInsets().getBottom());
    }

    private void doAnimationTransition() {
        Duration duration;

        if (contentRegion.getContent() == null) {
            return;
        }

        if (timeline != null && (timeline.getStatus() != Status.STOPPED)) {
            duration = timeline.getCurrentTime();
            timeline.stop();
        } else {
                duration = TRANSITION_DURATION;
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);

        KeyFrame k1, k2;

        if (getSkinnable().isExpanded()) {
            k1 = new KeyFrame(
                Duration.ZERO,
                new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        // start expand
                        contentRegion.getContent().setCache(true);
                        contentRegion.getContent().setVisible(true);
                    }
                },
                new KeyValue(transitionProperty(), transitionStartValue)
            );

            k2 = new KeyFrame(
                duration,
                    new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        // end expand
                        contentRegion.getContent().setCache(false);
                    }
                },
                new KeyValue(transitionProperty(), 1, Interpolator.EASE_OUT)

            );
        } else {
            k1 = new KeyFrame(
                Duration.ZERO,
                new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        // Start collapse
                        contentRegion.getContent().setCache(true);
                    }
                },
                new KeyValue(transitionProperty(), transitionStartValue)
            );

            k2 = new KeyFrame(
                duration,
                new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        // end collapse
                        contentRegion.getContent().setVisible(false);
                        contentRegion.getContent().setCache(false);
                    }
                },
                new KeyValue(transitionProperty(), 0, Interpolator.EASE_IN)
            );
        }

        timeline.getKeyFrames().setAll(k1, k2);
        timeline.play();
    }

    class Content extends StackPane implements TraverseListener {
        private Node content;
        private Rectangle clipRect;
        private TraversalEngine engine;
        private Direction direction;

        public Content(Node n) {
            this.clipRect = new Rectangle();
            setClip(clipRect);
            this.content = n;
            if (n != null) {
                getChildren().add(n);
            }

            engine = new TraversalEngine(this, false) {
                @Override public void trav(Node owner, Direction dir) {
                    direction = dir;
                    super.trav(owner, dir);
                }
            };
            engine.addTraverseListener(this);
            setImpl_traversalEngine(engine);
        }

        public void setContent(Node n) {
            this.content = n;
            if (n != null) {
                getChildren().setAll(getSkinnable().getContent());
            }
        }

        public Node getContent() {
            return content;
        }

        @Override protected void setWidth(double value) {
            super.setWidth(value);
            clipRect.setWidth(value);
        }

        @Override protected void setHeight(double value) {
            super.setHeight(value);
            clipRect.setHeight(value);
        }

        @Override
        public void onTraverse(Node node, Bounds bounds) {
            int index = engine.registeredNodes.indexOf(node);

            if (index == -1 && direction.equals(Direction.PREVIOUS)) {
                getSkinnable().requestFocus();
            }
            if (index == -1 && direction.equals(Direction.NEXT)) {
                // If the parent is an accordion we want to focus to go outside of the
                // accordion and to the next focusable control.
                if (getSkinnable().getParent() != null && getSkinnable().getParent() instanceof AccordionSkin) {
                    new TraversalEngine(getSkinnable(), false).trav(getSkinnable().getParent(), Direction.NEXT);
                }
            }
        }
    }
}
