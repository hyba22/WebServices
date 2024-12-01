package com.webservices.projectweb.repository;

import com.webservices.projectweb.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

}
