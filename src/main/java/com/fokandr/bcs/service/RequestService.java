package com.fokandr.bcs.service;

import com.fokandr.bcs.entity.Stock;
import com.fokandr.bcs.entity.StockCalc;
import com.fokandr.bcs.entity.Stocks;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface RequestService {
    Double getLatestPrice(Stock stock, RestTemplate restTemplate);
    String getSector(Stock stock, RestTemplate restTemplate);
    List<StockCalc> getRemoteAllocations(Stocks stocks);
}
