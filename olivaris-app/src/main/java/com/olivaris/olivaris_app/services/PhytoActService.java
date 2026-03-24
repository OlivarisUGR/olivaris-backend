package com.olivaris.olivaris_app.services;

import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.PhytoAct;

public interface PhytoActService {
    PhytoAct createPhytoActivity(Activity activity, CreatePhytoActReq body);
    PhytoAct updatePhytoAct(Long phytoActId, UpdatePhytoActReq body);
}
