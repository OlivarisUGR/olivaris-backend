package com.olivaris.olivaris_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.Plot;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserPlot;

@Repository
public interface UserPlotRepository extends CrudRepository<UserPlot, Long>{

	Optional<UserPlot> findByUserAndPlot(User user, Plot plot);
	@Query("""
		SELECT up FROM UserPlot up
		WHERE up.user.id = :userId AND up.plot.id = :plotId 
		""")
	Optional<UserPlot> findByUserIdAndPlotId(Long userId, Long plotId);

	@Query("""
		SELECT up FROM UserPlot up
		WHERE up.user.id = :userId	
	""")
	List<UserPlot> getUserPlots(Long userId);

	@Query("""
		SELECT up.plotName FROM UserPlot up
		WHERE up.user.id = :userId AND up.plot.id = :plotId
	""")
	Optional<String> getPlotNameByUserIdAndPlotId(Long userId, Long plotId);

	@Query("""
		SELECT en.id FROM UserPlot up 
		JOIN Enclosure en ON en.plot = up.plot
		WHERE up.user.id = :userId 
			AND up.plotName = :plotName
			AND en.name = :enclosureName	
	""")
	Optional<Long> getEnclosureIdByUserIdAndPlotNameAndEnclosureName(
		String plotName, 
		String enclosureName, 
		Long userId
	);
}
