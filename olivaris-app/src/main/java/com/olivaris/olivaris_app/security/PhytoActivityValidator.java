package com.olivaris.olivaris_app.security;

import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.exceptions.PhytoActArgumentException;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;

@Component("phytoActivityValidator")
public class PhytoActivityValidator {

    // It avoid to create a phyto activity if:
    //      - Activity is completed and body does not have necessary fields
    public boolean canCreatePhytoAct(Activity activity, CreatePhytoActReq body) {
        if(activity.getStatus().equals(ActivityStatus.COMPLETED) && !hasNecessaryFields(body)) {
            throw new PhytoActArgumentException(
                "Los parámetros dosis, unidad de dosis, nif del aplicador, area y cultivo son necesarios"
            );
        }

        return true;
    }

    private boolean hasNecessaryFields(CreatePhytoActReq body) {
        return body.getDose() != null && body.getDoseUnit() != null && body.getApplicatorNif() != null &&
                body.getArea() != null && body.getCrops() != null;
    }
}
