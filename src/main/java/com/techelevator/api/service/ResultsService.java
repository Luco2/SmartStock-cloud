package com.techelevator.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.api.model.ResultsModel;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ResultsService {

    @Value("${polygon.api.url}")
    private String apiUrl;

    @Value("${polygon.api.key}")
    private String apiKey;

    @Autowired
    private ApiStockService stockService;

    public List<ResultsModel> getSearchResults(String ticker) {

        String url = apiUrl + "/aggs/ticker/" + ticker + "/prev?adjusted=false&apiKey=" + apiKey;

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
            JsonNode statusNode = jsonNode.path("status");
            JsonNode tickerNode = jsonNode.path("ticker");
            JsonNode resultsNode = jsonNode.path("results");

            String status = statusNode.asText();
            String resultTicker = tickerNode.asText();

            for (JsonNode resultNode : resultsNode) {
                BigDecimal closePrice = resultNode.path("c").decimalValue();
                int transactions = resultNode.path("n").asInt();

                ResultsModel resultsModel = new ResultsModel(closePrice, transactions, status, resultTicker);
                resultsList.add(resultsModel);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return resultsList;
    }

    public ResultsModel getResultObject(String ticker) {
        String url = apiUrl + "/aggs/ticker/" + ticker + "/prev?adjusted=false&apiKey=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        JsonNode jsonNode;
        ResultsModel resultsModel = new ResultsModel();
        try {
            jsonNode = objectMapper.readTree(response.getBody());
            JsonNode statusNode = jsonNode.path("status");
            JsonNode tickerNode = jsonNode.path("ticker");
            JsonNode resultsNode = jsonNode.path("results");
            resultsModel.setStatus(statusNode.asText());

            resultsModel.setSymbol(tickerNode.asText());
            for(JsonNode result : resultsNode) {
                resultsModel.setClose(result.path("c").decimalValue());
                resultsModel.setVolume(result.path("n").asInt());
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultsModel;
    }
}