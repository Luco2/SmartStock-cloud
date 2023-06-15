package com.techelevator.controller;

import com.techelevator.api.model.StockModel;
import com.techelevator.dao.GameDao;
import com.techelevator.dao.PortfolioDao;
import com.techelevator.model.Portfolio;
import com.techelevator.model.PortfolioStocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/portfolios")
@PreAuthorize("isAuthenticated()")
public class PortfolioController {

    private PortfolioDao portfolioDao;

    public PortfolioController(PortfolioDao portfolioDao, GameDao gameDao) {
        this.portfolioDao = portfolioDao;
    }

    @GetMapping("/all/{gameId}")
    public List<Portfolio> getPortfoliosByGameId(@PathVariable int gameId) {
        return portfolioDao.getPortfoliosByGameId(gameId);
    }

    @GetMapping("/{gameId}")
    public Portfolio getPortfolioByUser(Principal principal, @PathVariable int gameId) {
        String username = principal.getName();
        return portfolioDao.getPortfolioByUser(username, gameId);
    }

    @GetMapping("/stocks/{gameId}")
    public List<PortfolioStocks> getPortfolioStocks(Principal principal, @PathVariable int gameId) {
        String username = principal.getName();
        return portfolioDao.getPortfolioStocks(username, gameId);
    }

    @PostMapping("/stocks/buy/{cost}/{gameId}")
    public void buy(@RequestBody PortfolioStocks stock, @PathVariable BigDecimal cost, @PathVariable int gameId, Principal principal) {
        String username = principal.getName();
        portfolioDao.buy(stock, cost, username, gameId);
    }

    @DeleteMapping("/stocks/sell/{cost}/{gameId}/{stockId}")
    public void sell(@PathVariable BigDecimal cost, @PathVariable int gameId, @PathVariable int stockId, Principal principal) {
        String username = principal.getName();
        portfolioDao.sell(cost, username, gameId, stockId);
    }
    
}



