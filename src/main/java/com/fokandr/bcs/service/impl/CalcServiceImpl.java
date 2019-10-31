package com.fokandr.bcs.service.impl;

import com.fokandr.bcs.entity.Allocation;
import com.fokandr.bcs.entity.Allocations;
import com.fokandr.bcs.entity.StockCalc;
import com.fokandr.bcs.entity.Stocks;
import com.fokandr.bcs.service.CalcService;
import com.fokandr.bcs.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalcServiceImpl implements CalcService {

    private RequestService iexCloudService;

    @Autowired
    public void setQuoteService(IEXCloudService iexCloudService) {
        this.iexCloudService = iexCloudService;
    }


    @Override
    public Allocations getAllocationsInfo(Stocks stocks) {
        List<StockCalc> allocationList = iexCloudService.getRemoteAllocations(stocks);
        return calcAllocations(allocationList);
    }

    private Allocations calcAllocations(List<StockCalc> stockCalcList) {
        Allocations allocations = new Allocations();
        List<Allocation> allocationList = new ArrayList<>();
        allocations.setAllocations(allocationList);

        Map<String, BigDecimal> sectorsInfo = new HashMap<>();
        BigDecimal sumValue = BigDecimal.ZERO;
        for (StockCalc stockCalc : stockCalcList) {
            if (Double.isNaN(stockCalc.getLatestPrice()) || Double.isNaN(stockCalc.getStock().getVolume())) {
                continue;
            }
            BigDecimal stocksValue = multiply(stockCalc.getLatestPrice(), stockCalc.getStock().getVolume());
            BigDecimal sumInSector = sectorsInfo.get(stockCalc.getSector());
            sectorsInfo.put(stockCalc.getSector(), sumInSector == null ? stocksValue : sumInSector.add(stocksValue));
            sumValue = sumValue.add(stocksValue);
        }
        if (sectorsInfo.size() > 0) {
            fillAllocations(allocationList, sectorsInfo, sumValue);
        }
        allocations.setValue(sumValue.doubleValue());
        return allocations;
    }

    private void fillAllocations(List<Allocation> allocationList, Map<String, BigDecimal> sectorsInfo, BigDecimal summ) {
        for (Map.Entry<String, BigDecimal> sectorInfo : sectorsInfo.entrySet()) {
            Allocation allocation = new Allocation();
            allocation.setSector(sectorInfo.getKey());
            allocation.setAssetValue(sectorInfo.getValue().doubleValue());
            allocation.setProportion(sectorInfo.getValue().divide(summ, 10, RoundingMode.HALF_UP).doubleValue());
            allocationList.add(allocation);
        }
    }

    private BigDecimal multiply(double latestPrice, double volume) {
        if (Double.isNaN(latestPrice) || Double.isNaN(volume)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(latestPrice).multiply(BigDecimal.valueOf(volume));
    }
}
