package com.techelevator.api.service;

import com.techelevator.api.model.ResultsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.api.model.StockModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApiStockService {

    @Value("${polygon.api.url}")
    private String apiUrl;

    @Value("${polygon.api.key}")
    private String apiKey;

    public StockModel getSearchResults(String stockTicker) {

        String url = apiUrl + "/aggs/ticker/" + stockTicker + "/prev?adjusted=false&apiKey=" + apiKey;
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        JsonNode jsonNode;
        List<ResultsModel> resultsList = new ArrayList<>();

        try {
            jsonNode = objectMapper.readTree(response.getBody());
            JsonNode resultsNode = jsonNode.path("results");

            for (JsonNode resultNode : resultsNode) {
                BigDecimal closePrice = resultNode.path("c").decimalValue();
                int transactions = resultNode.path("n").asInt();
                String status = resultNode.path("status").asText();
                String ticker = resultNode.path("ticker").asText();

                ResultsModel resultsModel = new ResultsModel(closePrice, transactions, status, ticker);
                resultsList.add(resultsModel);
            }

            boolean adjusted = jsonNode.path("adjusted").asBoolean();
            int queryCount = jsonNode.path("queryCount").asInt();
            String requestId = jsonNode.path("request_id").asText();
            int resultsCount = jsonNode.path("resultsCount").asInt();
            String status = jsonNode.path("status").asText();
            String ticker = jsonNode.path("ticker").asText();

            StockModel stockModel = new StockModel(adjusted, queryCount, requestId, resultsCount, status, ticker, resultsList);
            return stockModel;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
