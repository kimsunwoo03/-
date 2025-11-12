package com.example.gongnamul_project.words;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class WordSaving {
    @Id @GeneratedValue private Long id;
    @Column(nullable = false) private String word;
    @Column(nullable = false) private String meaning;

}
