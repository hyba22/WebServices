package com.projetweb.projetweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetweb.projetweb.model.Evenement;
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long>{
    
}