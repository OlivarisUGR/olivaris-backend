package com.olivaris.olivaris_app.services;

import java.util.List;

import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.PhytoAct;

public interface PhytoActService {
    PhytoAct createPhytoActivity(Activity activity, CreatePhytoActReq body);
    List<PhytoAct> updatePhytoActivities(List<UpdatePhytoActReq> body);
    PhytoAct updatePhytoAct(UpdatePhytoActReq body);
}
