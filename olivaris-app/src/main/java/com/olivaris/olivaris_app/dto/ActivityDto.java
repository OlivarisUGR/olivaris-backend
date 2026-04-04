package com.olivaris.olivaris_app.dto;

import java.time.LocalDate;
import java.util.List;

import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ActivityDto {

	private Long id;
	private Long userId;
	private Long enclosureId;
	private String enclosureName;
	private String plotName;
	private Long entityId;
	private ActivityType type;
	private LocalDate date;
	private String description;
	private String season;
	private ActivityStatus status;
	private List<PhytoActDto> phytoActIds;

	public static ActivityDto fromEntity(Activity activity, String plotName) {
		return new ActivityDto(
			activity.getId(),
			activity.getUser() != null ? activity.getUser().getId() : null,
			activity.getEnclosure() != null ? activity.getEnclosure().getId() : null,
			activity.getEnclosure().getName(),
			plotName != null ? plotName: null,
			activity.getEntity() != null ? activity.getEntity().getId() : null,
			activity.getType(),
			activity.getDate(),
			activity.getDescription(),
			activity.getSeason(),
			activity.getStatus(),
			activity.getPhytoAct() != null
				? activity.getPhytoAct().stream().map(PhytoActDto::fromEntity).toList()
				: List.of()
		);
	}

}
