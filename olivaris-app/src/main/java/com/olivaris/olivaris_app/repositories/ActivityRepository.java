package com.olivaris.olivaris_app.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.enums.ActivityType;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {

    Optional<Activity> findByDateAndEnclosureIdAndUserIdAndType(
        LocalDate date, Long enclosureId, Long userId, ActivityType type
    );

    @Query("""
        SELECT act.user.id FROM Activity act
        WHERE act.id = :activityId    
    """)
    Optional<Long> getUserIdByActId(Long activityId);

    @Query("""
       SELECT act.entity.id FROM Activity act  
       WHERE act.id = :activityId   
    """)
    Optional<Long> getEntityIdByActId(Long activityId);
}
