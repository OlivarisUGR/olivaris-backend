package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.Plot;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserPlot;

@Repository
public interface UserPlotRepository extends CrudRepository<UserPlot, Long>{

	Optional<UserPlot> findByUserAndPlot(User user, Plot plot);
}
