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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Parent {

    private final ObservableList<Bone> bones = FXCollections.observableArrayList();
    ObservableList<Bone> getBonesWritable() {return bones;}

    private final ObservableList<Bone> bonesView = FXCollections.unmodifiableObservableList(bones);
    public ObservableList<Bone> getBones() {return bonesView;}

    public Skeleton() {
        bones.addListener(new ListChangeListener<Bone>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Bone> change) {
                final List<Node> children = getChildren();
                while (change.next()) {
                    children.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                    final List<Node> nodes = new ArrayList<>(change.getAddedSize());
                    for (final Bone bone : change.getAddedSubList()) {
                        nodes.add(bone.getGroup());
                    }
                    children.addAll(change.getFrom(), nodes);
                }
            }
        });
    }

}
