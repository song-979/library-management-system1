package org.example.service.impl;

import org.example.dao.ReaderDAO;
import org.example.model.Reader;
import org.example.service.ReaderService;
import java.util.List;

public class ReaderServiceImpl implements ReaderService {
    private final ReaderDAO readerDAO = new ReaderDAO();

    @Override
    public List<Reader> getAllReaders() {
        return readerDAO.getAllReaders();
    }

    @Override
    public boolean addReader(Reader reader) {
        return readerDAO.addReader(reader);
    }

    @Override
    public boolean updateReader(Reader reader) {
        return readerDAO.updateReader(reader);
    }

    @Override
    public boolean deleteReader(int readerId) {
        return readerDAO.deleteReader(readerId);
    }
}
