package com.olivaris.olivaris_app.fixtures;

import com.olivaris.olivaris_app.models.EnabledEntity;

public class EntityFixtures {

    public static EnabledEntity createBasicEntity() {
        return new EnabledEntity(
            "los olivos", 
            "A1234567C", 
            null, 
            "losolivos@gmail.com",
            true
        );
    }
}
