package com.fokandr.bcs.service.impl;

import com.fokandr.bcs.entity.Stock;
import com.fokandr.bcs.entity.StockCalc;
import com.fokandr.bcs.entity.Stocks;
import com.fokandr.bcs.service.RequestService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IEXCloudService implements RequestService {
    @Value("${uri_latest_price}")
    private String uri_latest_price;
    @Value("${uri_company}")
    private String uri_company;
    @Value("${publ_key}")
    private String publ_key;
    private RestTemplate restTemplate = new RestTemplate();
    private Cache<String, JSONObject> cacheCompany= CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();
    Logger logger = LoggerFactory.getLogger(IEXCloudService.class);

    @Override
    public Double getLatestPrice(Stock stock, RestTemplate restTemplate) {
        try {
            return restTemplate.getForObject(String.format(uri_latest_price, stock.getSymbol(), publ_key), Double.class);
        } catch (RestClientException e) {
            logger.error("getLatestPrice", e);
            return Double.NaN;
        }
    }

    @Override
    public String getSector(Stock stock, RestTemplate restTemplate) {
        JSONObject jsonCache = cacheCompany.getIfPresent(stock.getSymbol());
        if (jsonCache != null) {
            return jsonCache.getString("sector");
        }
        try {
            String obj = restTemplate.getForObject(String.format(uri_company, stock.getSymbol(), publ_key), String.class);
            JSONObject jsonObject = (new JSONObject(obj));
            cacheCompany.put(stock.getSymbol(), jsonObject);
            return jsonObject.getString("sector");
        } catch (RestClientException e) {
            logger.error("getLatestPrice", e);
            return "Sector not found";
        }
    }

    @Override
    public List<StockCalc> getRemoteAllocations(Stocks stocks) {
        return stocks.getStocks().parallelStream().map(this::getAllocation).collect(Collectors.toList());
    }


    private StockCalc getAllocation(Stock stock) {
        StockCalc stockCalc = new StockCalc();
        stockCalc.setStock(stock);
        stockCalc.setLatestPrice(this.getLatestPrice(stock, restTemplate));
        stockCalc.setSector(this.getSector(stock, restTemplate));
        return stockCalc;
    }

}
