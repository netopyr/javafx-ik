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
import javafx.animation.Transition;
import javafx.util.Duration;

public abstract class AbstractIKTransition extends Transition {

    /**
     * The {@link com.netopyr.javafx.ik.Bone} which is affected by this transition.
     */
    private final Bone bone;
    public final Bone getBone() {return bone;}

    /**
     * The duration of this {@code Transition}.
     */
    private final Duration duration;
    public final Duration getDuration() {return duration;}

    /**
     * Specifies the start X coordinate value of this transition.
     */
    private final double fromX;
    public final double getFromX() {return fromX;}

    /**
     * Specifies the start Y coordinate value of this transition.
     */
    private final double fromY;
    public final double getFromY() {return fromY;}

    /**
     * Specifies the delta of the X coordinate of this transition.
     */
    private final double byX;
    public final double getByX() {return byX;}

    /**
     * Specifies the delta of the Y coordinate of this transition.
     */
    private final double byY;
    public final double getByY() {return byY;}

    /**
     * Specifies the end X coordinate value of this transition.
     */
    public final double getToX() {return fromX + byX;}

    /**
     * Specifies the stop Y coordinate value of this transition.
     */
    public final double getToY() {return fromY + byY;}

    /**
     * The constructor of {@code AbstractIKTransition}
     *
     * @param bone The {@link Bone} affected by this transition
     * @param duration The duration
     * @param fromX The start X coordinate
     * @param fromY The start Y coordinate
     * @param byX The delta of the X coordinate
     * @param byY The delta of the Y coordinate
     */
    public AbstractIKTransition(Bone bone, Duration duration, double fromX, double fromY, double byX, double byY) {
        this.bone = bone;
        this.duration = duration;
        this.fromX = fromX;
        this.fromY = fromY;
        this.byX = byX;
        this.byY = byY;
        setCycleDuration(duration);
    }

}
