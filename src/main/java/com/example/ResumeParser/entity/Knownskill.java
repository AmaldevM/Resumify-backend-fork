package com.example.ResumeParser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "known_skills")
@Getter
@Setter
public class Knownskill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Knownskill() {}

    public Knownskill(String name) {
        this.name = name.toLowerCase();
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }
}
