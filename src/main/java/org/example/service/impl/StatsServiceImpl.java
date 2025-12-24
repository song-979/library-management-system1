package org.example.service.impl;

import org.example.dao.StatsDAO;
import org.example.model.Statistics;
import org.example.service.StatsService;

public class StatsServiceImpl implements StatsService {
    private final StatsDAO statsDAO = new StatsDAO();
    @Override
    public Statistics getStatistics() {
        return statsDAO.fetch();
    }
}
