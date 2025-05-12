package com.example.ResumeParser.repository;

import java.util.List;
import com.example.ResumeParser.entity.Resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Resumerepository extends JpaRepository<Resume, Long>{

       // Optional: Find resumes that have a specific skill
       @Query("SELECT r FROM Resume r JOIN r.skills s WHERE LOWER(s.name) = LOWER(:skillName)")
       List<Resume> findBySkillName(@Param("skillName") String skillName);
   
       // Optional: Sort resumes by years of experience (descending)
       List<Resume> findAllByOrderByYearsOfExperienceDesc();



    
}
