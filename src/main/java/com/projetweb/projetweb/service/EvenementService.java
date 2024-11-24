package com.projetweb.projetweb.service;
import java.util.List;

import com.projetweb.projetweb.model.Evenement;

public interface EvenementService {
    List<Evenement> getAllEvenementss();
    void saveEvenement(Evenement evenement);
    Evenement getEvenementById(Long idevent);
    void deleteEvenementById(Long idevent);
    
}