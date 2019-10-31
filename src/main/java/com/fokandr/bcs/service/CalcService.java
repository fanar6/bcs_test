package com.fokandr.bcs.service;

import com.fokandr.bcs.entity.Allocations;
import com.fokandr.bcs.entity.Stocks;

public interface CalcService {
    Allocations getAllocationsInfo(Stocks stocks);
}
