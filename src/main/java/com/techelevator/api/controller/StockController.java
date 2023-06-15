package com.techelevator.api.controller;

import com.techelevator.api.model.ResultsModel;
import com.techelevator.api.model.StockModel;
import com.techelevator.api.service.ResultsService;
import com.techelevator.api.service.ApiStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class StockController {

    @Autowired
    ResultsService resultsService;


    @GetMapping("/resultsObject")
    public ResponseEntity<?> getResultObject(@RequestParam(required = false) String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            return ResponseEntity.badRequest().body("Ticker parameter is missing");
        }
        ResultsModel resultsModel = resultsService.getResultObject(ticker);
        if (resultsModel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultsModel);
    }
}

// postman url: http://localhost:9000/stock?ticker={ticker}

