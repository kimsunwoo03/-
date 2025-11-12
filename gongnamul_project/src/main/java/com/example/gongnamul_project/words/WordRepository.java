package com.example.gongnamul_project.words;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<WordSaving, Long> {
}
