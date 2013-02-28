/*
 * Copyright 2013 Michael Heinrichs, http://netopyr.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netopyr.javafx.ik;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import java.util.List;

/**
 * The class {@code Bone} is the basic building block for inverse kinematics.
 * <p>
 * {@code Bone} objects need to be assembled in a tree-structure using the field {@link #children}.
 * <p>
 * A {@code Bone} has a head, which is a point in space around which it can be
 * rotated, and a {@link #length}. The current value of the head and rotation is stored in {@link #currentHead} and {@link #rotate}.
 * Head, rotation, and length define a second point in space, the tail. The current position of the tail is stored
 * in {@link #currentTail}.
 * <p>
 * The rotation can be limited using the fields {@link #minAngle} and
 * {@link #maxAngle}. The angle is measured in regard to the parent-bone.
 * It can be read from {@link #angle}.
 * <p>
 * The {@code Node} objects which need to be translated and rotated according
 * to the {@code Bone} can be set using {@link #content}. All {@code Bone}
 * objects need to be assigned to a {@link Skeleton}, the {@code Node} which
 * needs to be added to the scenegraph. All coordinates of a {@code Bone} are
 * based on the local coordinate-system of its {@link Skeleton}.
 */
public class Bone {

    private static final Point2D ZERO = new Point2D(0, 0);
    private static final double DEFAULT_ANGLE = 0.0;
    private static final double MIN_ANGLE = -180.0;
    private static final double MAX_ANGLE =  180.0;

    /**
     * The length of this {@code Bone}.
     */
    private final double length;

    public final double getLength() {return length;}

    /**
     * The lower bound of the {@link #angle} of this {@code Bone}.
     */
    private final double minAngle;
    public final double getMinAngle() {return minAngle;}

    /**
     * The upper bound of the {@link #angle} of this {@code Bone}.
     */
    private final double maxAngle;
    public final double getMaxAngle() {return maxAngle;}


    /**
     * The current position of the currentHead of this {@code Bone}.
     * <p>
     * The position of the currentHead is calculated in local coordinates of the {@link Skeleton}.
     * <p>
     * The currentHead's position can be altered using {@link #moveHead(double, double)} or {@link #moveHead(javafx.geometry.Point2D)}.
     */
    private final PrivatePoint2DProperty currentHead = new PrivatePoint2DProperty("currentHead");
    public final Point2D getCurrentHead() { return currentHead.get(); }
    public final ReadOnlyObjectProperty<Point2D> currentHeadProperty() { return currentHead; }

    /**
     * The current position of the currentTail of this {@code Bone}.
     * <p>
     * The currentTail is calculated in local coordinates of the {@link Skeleton}.
     * <p>
     * The currentTail's position can be altered using {@link #moveTail(double, double)} or {@link #moveTail(javafx.geometry.Point2D)}.
     */
    private final PrivatePoint2DProperty currentTail = new PrivatePoint2DProperty("currentTail");
    public final Point2D getCurrentTail() { return currentTail.get(); }
    public final ReadOnlyObjectProperty<Point2D> currentTailProperty() { return currentTail; }

    /**
     * The current angle of this {@code Bone}.
     * <p>
     * The angle is measured in degrees between this {@code Bone} and the
     * extension of its parent, an angle of null degrees results in a
     * straight line. An {@code angle} has a value in the range [-180..180].
     * <p>
     * The angle can be narrowed down using {@link #minAngle} and {@link #maxAngle}.
     */
    private final PrivateDoubleProperty angle = new PrivateDoubleProperty("angle");
    public final double getAngle() { return angle.get(); }
    public ReadOnlyDoubleProperty angleProperty() { return angle; }

    /**
     * The current rotation of this {@code Bone} in relation to the
     * {@link Skeleton}.
     */
    private final PrivateDoubleProperty rotate = new PrivateDoubleProperty("rotate");
    public final double getRotate() { return rotate.get(); }
    public ReadOnlyDoubleProperty rotateProperty() { return rotate; }

    /**
     * The {@code Node} objects which are controlled by this {@code Bone}.
     * <p>
     * If this {@code Bone} is translated or rotated, the {@code Node} objects in
     * {@code content} are updated accordingly.
     */
    private ListProperty<Node> content = new SimpleListProperty<>(this, "content", FXCollections.<Node>observableArrayList());
    public final ObservableList<Node> getContent() {return content.get();}
    public final void setContent(ObservableList<Node> list) {this.content.set(list);}


    /**
     * The {@link Skeleton} is the {@code Node} in the scenegraph, that
     * determines how {@code Bone} objects are rendered.
     * <p>
     * All connected {@code Bone} objects in a tree belong to one
     * {@link Skeleton}. (This is taken care of automatically. If the
     * {@code Skeleton} of one {@code Bone} is changed, the
     * {@code Skeleton}-values for all connected {@code Bone} objects are
     * updated.)
     * <p>
     * All coordinates of this {@code Bone} are based on the local
     * coordinate-system of this {@code Skeleton}.
     */
    private ObjectProperty<Skeleton> skeleton = new ObjectPropertyBase<Skeleton>() {
        private Skeleton oldSkeleton = null;

        @Override
        protected void invalidated() {
            final Skeleton newSkeleton = get();
            if ((newSkeleton == null)? oldSkeleton != null : !newSkeleton.equals(oldSkeleton)) {
                if (oldSkeleton != null) {
                    oldSkeleton.getBonesWritable().remove(Bone.this);
                }
                if (newSkeleton != null) {
                    newSkeleton.getBonesWritable().add(Bone.this);
                }
                final Bone parentBone = getParent();
                if (parentBone != null) {
                    parentBone.setSkeleton(newSkeleton);
                }
                for (final Bone child : children) {
                    if (child != null) {
                        child.setSkeleton(newSkeleton);
                    }
                }
                oldSkeleton = newSkeleton;
            }
        }

        @Override
        public Object getBean() {
            return Bone.this;
        }

        @Override
        public String getName() {
            return "skeleton";
        }
    };
    public final Skeleton getSkeleton() {return skeleton.get();}
    public final void setSkeleton(Skeleton skeleton) {this.skeleton.set(skeleton);}
    public final ObjectProperty<Skeleton> skeletonProperty() {return skeleton;}

    /**
     * The parent-{@code Bone} of this {@code Bone}.
     */
    private final PrivateParentProperty parent = new PrivateParentProperty();
    public final Bone getParent() {return parent.get();}
    public final ReadOnlyObjectProperty<Bone> parentProperty() {return parent;}

    /**
     * The children of this {@code Bone}.
     */
    private final ListProperty<Bone> children = new SimpleListProperty<>(this, "children", FXCollections.<Bone>observableArrayList());
    public final ObservableList<Bone> getChildren() {return children.get();}
    public final void setChildren(ObservableList<Bone> children) {this.children.set(children);}
    public final ListProperty<Bone> childrenProperty() {return children;}

    private final Group group = new Group();
    Node getGroup() {return group;}

    public Bone(double length, double angle, double minAngle, double maxAngle) {
        this.length   = length;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.angle.set(angle);

        final Rotate rotate = new Rotate(getRotate(), 0.0, 0.0);
        rotate.angleProperty().bind(rotateProperty());
        group.getTransforms().setAll(rotate);

        group.translateXProperty().bind(new DoubleBinding() {
            { super.bind(currentHeadProperty()); }
            @Override
            protected double computeValue() {
                return getCurrentHead().getX();
            }
        });
        group.translateYProperty().bind(new DoubleBinding() {
            { super.bind(currentHeadProperty()); }
            @Override
            protected double computeValue() {
                return getCurrentHead().getY();
            }
        });
        content.addListener(new ChangeListener<ObservableList<? extends Node>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<? extends Node>> observableValue, ObservableList<? extends Node> oldList, ObservableList<? extends Node> newList) {
                if (oldList != newList) {
                    Bindings.unbindContent(group.getChildren(), oldList);
                    Bindings.bindContent(group.getChildren(), newList);
                }
            }
        });
        Bindings.bindContent(group.getChildren(), content.get());
        children.addListener(new ListChangeListener<Bone>() {
            @Override
            public void onChanged(Change<? extends Bone> change) {
                while (change.next()) {
                    if (!change.wasPermutated()) {
                        for (final Bone child : change.getRemoved()) {
                            if (Bone.this.equals(child.getParent())) {
                                child.parent.set(null);
                            }
                        }
                        for (final Bone child : change.getAddedSubList()) {
                            if (!Bone.this.equals(child.getParent())) {
                                child.parent.set(Bone.this);
                            }
                        }
                    }
                }
            }
        });
        setup();
    }
    public Bone(double length, double minAngle, double maxAngle) {
        this(length, DEFAULT_ANGLE, minAngle, maxAngle);
    }
    public Bone(double length, double angle) {
        this(length, angle, MIN_ANGLE, MAX_ANGLE);
    }
    public Bone(double length) {
        this(length, DEFAULT_ANGLE, MIN_ANGLE, MAX_ANGLE);
    }

    public void moveHead(Point2D p) {
        moveHead(p, null);
    }
    public void moveHead(double x, double y) {
        moveHead(new Point2D(x, y), null);
    }

    public void moveTail(Point2D p) {
        moveTail(p, null);
    }
    public void moveTail(double x, double y) {
        moveTail(new Point2D(x, y), null);
    }

    private void moveHead(Point2D point, Bone initiator) {
        final Bone parent = getParent();
        assert initiator == null || initiator.equals(parent);

        if (!getCurrentHead().equals(point)) {
            currentHead.set(point);
            final double alpha = getAngle(point, getCurrentTail());
            final double rotateValue = 180 * alpha / Math.PI;
            final double minAngle = getMinAngle();
            final double maxAngle = getMaxAngle();
            if ((initiator != null) && ((minAngle > -180) || (maxAngle < 180))) {
                final double initiatorRotate = initiator.getRotate();
                final double angle = Math.max(minAngle, Math.min(borderAngle(rotateValue - initiatorRotate), maxAngle));
                this.angle.set(angle);
                rotate.set(borderAngle(initiatorRotate + angle));
                currentTail.set(createPoint2D(point, getRotate() * Math.PI / 180.0, getLength()));
            } else {
                rotate.set(rotateValue);
                currentTail.set(createPoint2D(point, alpha, getLength()));
                if ((initiator == null) && (parent != null)) {
                    angle.set(borderAngle(rotateValue - parent.getRotate()));
                    parent.moveTail(point, this);
                }
            }
            updateChildren(null);
        }
    }

    private void moveTail(Point2D point, Bone initiator) {
        final Bone parent = getParent();
        assert initiator == null || !initiator.equals(parent);

        if (!getCurrentTail().equals(point)) {
            currentTail.set(point);
            double alpha = getAngle(point, getCurrentHead());
            double rotateValue = borderAngle(180.0 + 180 * alpha / Math.PI);
            if (initiator != null) {
                final double minAngle = initiator.getMinAngle();
                final double maxAngle = initiator.getMaxAngle();
                if ((minAngle > -180) || (maxAngle < 180)) {
                    final double initiatorRotate = initiator.getRotate();
                    final double childAngle = Math.max(minAngle, Math.min(borderAngle(initiatorRotate - rotateValue), maxAngle));
                    rotateValue = borderAngle(initiatorRotate - childAngle);
                    alpha = (rotateValue - 180) * Math.PI / 180.0;
                }
            }
            rotate.set(rotateValue);
            currentHead.set(createPoint2D(point, alpha, getLength()));
            if (parent != null) {
                angle.set(borderAngle(rotateValue - parent.getRotate()));
                parent.moveTail(getCurrentHead(), this);
            }
            updateChildren(initiator);
        }
    }

    private void resetFromParent() {
        final Bone parent = getParent();
        setSkeleton(parent.getSkeleton());
        currentHead.set(parent.getCurrentTail());
        setup();
    }

    private void setup() {
        final Bone parent = getParent();
        final double rotateValue = (parent == null)? getAngle() : borderAngle (parent.getRotate() + getAngle());
        rotate.set(rotateValue);
        currentTail.set(createPoint2D(getCurrentHead(), Math.PI * rotateValue / 180.0, getLength()));
        for (final Bone child : children) {
            child.resetFromParent();
        }
    }

    private void updateChildren(Bone initiator) {
        final Point2D currentTail = getCurrentTail();
        for (final Bone bone : getChildren()) {
            if (!bone.equals(initiator)) {
                bone.moveHead(currentTail, this);
            }
        }
    }

    private double borderAngle(double value) {
        if (value <= -180) {
            return value + 360;
        }
        if (value > 180) {
            return value - 360;
        }
        return value;
    }

    private static Point2D createPoint2D(Point2D origin, double angle, double length) {
        return new Point2D(origin.getX() + Math.cos(angle) * length, origin.getY() + Math.sin(angle) * length);
    }

    private static double getAngle (Point2D p1, Point2D p2) {
        return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
    }

    private class PrivatePoint2DProperty extends ReadOnlyObjectPropertyBase<Point2D> {

        private final String name;
        private Point2D value = ZERO;

        @Override
        public Point2D get() {
            return value;
        }

        protected void set(Point2D value) {
            this.value = value;
            fireValueChangedEvent();
        }

        @Override
        public Object getBean() {
            return Bone.this;
        }

        @Override
        public String getName() {
            return name;
        }

        private PrivatePoint2DProperty(String name) {
            this.name = name;
        }
    }

    private class PrivateDoubleProperty extends ReadOnlyDoublePropertyBase {

        private final String name;
        private double value;

        @Override
        public double get() {
            return value;
        }

        protected void set(double value) {
            this.value = value;
            fireValueChangedEvent();
        }

        @Override
        public Object getBean() {
            return Bone.this;
        }

        @Override
        public String getName() {
            return name;
        }

        private PrivateDoubleProperty(String name) {
            this.name = name;
        }
    }

    private class PrivateParentProperty extends ReadOnlyObjectPropertyBase<Bone> {

        private Bone value;

        @Override
        public Bone get() {
            return value;
        }

        private void set(Bone newValue) {
            final Bone oldValue = value;
            if ((newValue == null)? oldValue != null : !newValue.equals(oldValue)) {
                value = newValue;
                if (oldValue != null) {
                    oldValue.getChildren().remove(Bone.this);
                }
                if (newValue != null) {
                    final List<Bone> children = value.getChildren();
                    if (!children.contains(Bone.this)) {
                        children.add(Bone.this);
                    }
                    resetFromParent();
                }
                fireValueChangedEvent();
            }
        }

        @Override
        public Object getBean() {
            return Bone.this;
        }

        @Override
        public String getName() {
            return "parent";
        }

    }
}
