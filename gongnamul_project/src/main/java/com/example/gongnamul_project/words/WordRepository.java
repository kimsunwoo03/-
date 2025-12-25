package com.example.gongnamul_project.words;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface WordRepository extends JpaRepository<WordSaving, Long> {
    Optional<WordSaving> findByWord(String word);
}