package com.olivaris.olivaris_app.fixtures;

import com.olivaris.olivaris_app.models.EnabledEntity;

public class EntityFixtures {

    public static EnabledEntity createBasicEntity() {
        return new EnabledEntity(
            "los olivos", 
            "A1234567A", 
            null, 
            "losolivos@gmail.com",
            true
        );
    }

    public static EnabledEntity createBasicEntity2() {
        return new EnabledEntity(
            "las rateras",
            "A1234567B",
            null,
            "lasrateras@gmail.com",
            true
        );
    }
}
