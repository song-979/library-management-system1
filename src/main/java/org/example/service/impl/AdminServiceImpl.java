package org.example.service.impl;

import org.example.dao.AdminDAO;
import org.example.service.AdminService;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    public boolean login(String username, String password) {
        return adminDAO.validateAdmin(username, password) != null;
    }
}