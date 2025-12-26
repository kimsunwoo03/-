package com.example.gongnamul_project.words;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WordService {
    WordSaving save(WordSaving w);
    List<WordSaving> findAll();
    WordSaving findById(long id);
    void deleteById(long id);

    Optional<WordSaving> findByWord(String queryWord);
    int bulkSaveWords(String rawTextContext);
}
