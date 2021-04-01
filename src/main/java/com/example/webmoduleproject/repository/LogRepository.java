package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, String> {

    @Query("SELECT l FROM LogEntity l ORDER BY l.dateTime DESC")
    List<LogEntity> getAllLogs();

    @Query("SELECT COUNT(l) FROM LogEntity l WHERE l.action = :action")
    Long getCountByAction(String action);
}
