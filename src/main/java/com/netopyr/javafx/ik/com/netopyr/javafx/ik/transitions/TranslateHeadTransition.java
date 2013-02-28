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

package com.netopyr.javafx.ik.com.netopyr.javafx.ik.transitions;

import com.netopyr.javafx.ik.Bone;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class TranslateHeadTransition extends AbstractIKTransition {

    /**
     * The constructor of {@code TranslateHeadTransition}
     *
     * @param bone The {@link Bone} affected by this transition
     * @param duration The duration
     * @param fromX The start X coordinate
     * @param fromY The start Y coordinate
     * @param byX The delta of the X coordinate
     * @param byY The delta of the Y coordinate
     */
    public TranslateHeadTransition(Bone bone, Duration duration, double fromX, double fromY, double byX, double byY) {
        super(bone, duration, fromX, fromY, byX, byY);
    }

    /**
     * The constructor of {@code TranslateHeadTransition}
     *
     * @param bone The {@link Bone} affected by this transition
     * @param duration The duration
     * @param from The start X coordinate
     * @param by The delta
     */
    public TranslateHeadTransition(Bone bone, Duration duration, Point2D from, Point2D by) {
        super(bone, duration, from.getX(), from.getY(), by.getX(), by.getY());
    }

    /**
     * The constructor of {@code TranslateHeadTransition}
     *
     * @param bone The {@link Bone} affected by this transition
     * @param duration The duration
     * @param byX The delta of the X coordinate
     * @param byY The delta of the Y coordinate
     */
    public TranslateHeadTransition(Bone bone, Duration duration, double byX, double byY) {
        super(bone, duration, bone.getCurrentHead().getX(), bone.getCurrentHead().getY(), byX, byY);
    }

    /**
     * The constructor of {@code TranslateHeadTransition}
     *
     * @param bone The {@link Bone} affected by this transition
     * @param duration The duration
     * @param by The delta
     */
    public TranslateHeadTransition(Bone bone, Duration duration, Point2D by) {
        super(bone, duration, bone.getCurrentHead().getX(), bone.getCurrentHead().getY(), by.getX(), by.getY());
    }

    @Override
    protected void interpolate(double v) {
        final double x = getFromX() + v * getByX();
        final double y = getFromY() + v * getByY();
        getBone().moveHead(x, y);
    }
}
