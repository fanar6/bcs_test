package com.fokandr.bcs.controller;

import com.fokandr.bcs.entity.Allocations;
import com.fokandr.bcs.entity.Stocks;
import com.fokandr.bcs.service.CalcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class StockController {
    private CalcService calcService;

    @Autowired
    public void setQuoteService(CalcService calcService) {
        this.calcService = calcService;
    }

    @RequestMapping(value = "/allocations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public Allocations getAllocationsInfo(@RequestBody Stocks stocks) {
        return this.calcService.getAllocationsInfo(stocks);
    }

}


