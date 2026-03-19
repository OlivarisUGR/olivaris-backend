package com.olivaris.olivaris_app.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.enums.ActivityType;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {

    Optional<Activity> findByDateAndEnclosureIdAndUserIdAndType(
        LocalDate date, Long enclosureId, Long userId, ActivityType type
    );
}
