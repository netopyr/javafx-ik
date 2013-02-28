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
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

public class Dummy extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage stage) throws Exception {
        final Parent root = createDummy();
        root.setTranslateX(WIDTH / 2);
        root.setTranslateY(HEIGHT / 4);

        final Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Dummy Sample");
        stage.setScene(scene);
        stage.show();
    }

    private static Parent createDummy() {
        final Skeleton skeleton = new Skeleton();

        final Bone hook = new Bone(110, 90);
        hook.setSkeleton(skeleton);

        final Bone torso = new Bone(80, 180, 180, 180);
        torso.getContent().add(new Ellipse(40, 0, 50, 20));
        hook.getChildren().add(torso);

        final Bone head = new Bone(30, 0, -30, 30);
        final Node headNode = new Ellipse(30, 0, 20, 15);
        headNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                head.moveTail(skeleton.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        });
        head.getContent().setAll(headNode);
        torso.getChildren().add(head);

        final Bone[] upperArm = new Bone[2];
        final Bone[] upperLeg = new Bone[2];

        for (int i=0; i<2; i++) {
            upperArm[i] = new Bone(60, 150 - 270 * i);
            upperArm[i].getContent().setAll(new Ellipse(22.5, 0, 30, 12.5));

            final Bone lowerArm = new Bone(60, -90, -135, 0);
            upperArm[i].getChildren().add(lowerArm);
            final Node elbow = new Circle(12.5);
            elbow.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    lowerArm.moveHead(skeleton.sceneToLocal(event.getSceneX(), event.getSceneY()));
                }
            });
            final Node hand = new Circle(60, 0, 12.5);
            hand.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    lowerArm.moveTail(skeleton.sceneToLocal(event.getSceneX(), event.getSceneY()));
                }
            });
            lowerArm.getContent().setAll(elbow, new Ellipse(30, 0, 20, 12.5), hand);

            upperLeg[i] = new Bone(60, 30 - 90*i, -90, 45);
            upperLeg[i].getContent().setAll(new Ellipse(20, 0, 30, 15));

            final Bone lowerLeg = new Bone(75, 90, 0, 135);
            upperLeg[i].getChildren().add(lowerLeg);
            final Node knee = new Circle(15);
            knee.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    lowerLeg.moveHead(skeleton.sceneToLocal(event.getSceneX(), event.getSceneY()));
                }
            });
            final Node foot = new Ellipse(75, -10, 10, 22.5);
            foot.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    lowerLeg.moveTail(skeleton.sceneToLocal(event.getSceneX(), event.getSceneY()));
                }
            });
            lowerLeg.getContent().setAll(knee, new Ellipse(40, 0, 30, 15), foot);
        }
        torso.getChildren().addAll(upperArm);
        hook.getChildren().addAll(upperLeg);

        return skeleton;
    }

    public static void main(String... args) {
        launch(args);
    }

}
