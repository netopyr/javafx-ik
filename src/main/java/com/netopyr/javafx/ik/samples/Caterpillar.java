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

package com.netopyr.javafx.ik.samples;

import com.netopyr.javafx.ik.Bone;
import com.netopyr.javafx.ik.Skeleton;
import com.netopyr.javafx.ik.com.netopyr.javafx.ik.transitions.TranslateHeadTransition;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradientBuilder;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Caterpillar extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;
    private static final double SLOWDOWN = 2;

    private Animation runningAnimation;

    @Override
    public void start(Stage stage) throws Exception {
        final Skeleton caterpillar = createCaterpillar();
        final Bone head = caterpillar.getBones().get(0);

        final Node background = new Rectangle(WIDTH, HEIGHT, Color.BLACK);
        background.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                final Point2D current = caterpillar.localToScene(head.getCurrentHead());
                final double targetX = mouseEvent.getSceneX();
                final double targetY = mouseEvent.getSceneY();
                final Duration duration = Duration.millis(SLOWDOWN * current.distance(targetX, targetY));
                if (runningAnimation != null) {
                    runningAnimation.stop();
                }
                runningAnimation = new TranslateHeadTransition(caterpillar.getBones().get(0), duration, targetX - current.getX(), targetY - current.getY());
                runningAnimation.play();
            }
        });
        final Parent root = new Group(
                background,
                caterpillar
        );

        final Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Caterpillar Sample");
        stage.setScene(scene);
        stage.show();
    }

    private static Skeleton createCaterpillar() {
        final Skeleton skeleton = new Skeleton();
        skeleton.setTranslateX(WIDTH / 4);
        skeleton.setTranslateY(HEIGHT / 2);

        final Bone head = new Bone(20);
        head.setSkeleton(skeleton);
        head.getContent().add(new Circle(10,
                RadialGradientBuilder.create()
                        .centerX(0.25)
                        .centerY(0.25)
                        .radius(1)
                        .stops(new Stop(0.0, Color.RED), new Stop(1.0, Color.DARKRED))
                .build()));

        Bone iterator = head;

        final Paint fill =
                RadialGradientBuilder.create()
                        .centerX(0.25)
                        .centerY(0.25)
                        .radius(1)
                        .stops(new Stop(0.0, Color.LIGHTGREEN), new Stop(1.0, Color.GREEN))
                .build();


        for (int i=0; i<15; i++) {
            final Bone bone = new Bone(20, -60, 60);
            bone.getContent().add(new Circle(10, fill));

            iterator.getChildren().add(bone);
            iterator = bone;
        }

        return skeleton;
    }

    public static void main(String... args) {
        launch(args);
    }
}
