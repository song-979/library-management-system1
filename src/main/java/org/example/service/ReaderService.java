package org.example.service;

import org.example.model.Reader;
import java.util.List;

public interface ReaderService {
    List<Reader> getAllReaders();
    boolean addReader(Reader reader);
    boolean updateReader(Reader reader);
    boolean deleteReader(int readerId);
}
