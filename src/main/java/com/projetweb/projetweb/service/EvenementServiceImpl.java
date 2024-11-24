package com.projetweb.projetweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetweb.projetweb.model.Evenement;
import com.projetweb.projetweb.repository.EvenementRepository;
@Service
public class EvenementServiceImpl implements EvenementService{
    @Autowired
    private EvenementRepository evenementRepository;
    
    @Override
    public List<Evenement> getAllEvenementss() {
        return evenementRepository.findAll();
    }

    @Override
    public void saveEvenement(Evenement evenement) {
        this.evenementRepository.save(evenement);
    }

    @Override
    public Evenement getEvenementById(Long idevent) {
        return evenementRepository.findById(idevent)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé pour l'ID : " + idevent));
    }

    @Override
    public void deleteEvenementById(Long idevent) {
        this.evenementRepository.deleteById(idevent);
    }
}