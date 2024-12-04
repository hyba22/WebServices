package com.projetweb.projetweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.projetweb.projetweb.model.Evenement;
import com.projetweb.projetweb.service.EvenementService;
@Controller
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listEvenementss", evenementService.getAllEvenementss());
        return "evenement";
    }
    @GetMapping("/showNewEvenementForm")
    public String showNewEvenementForm(Model model) {
        Evenement evenement = new Evenement();
        model.addAttribute("evenement", evenement);
        return "new_evenement";
    }

    @PostMapping("/saveEvenement")
    public String saveEvenement(@ModelAttribute("evenement") Evenement evenement){
        evenementService.saveEvenement(evenement);
        return "redirect:/";
    }
    @GetMapping("/showFormForUpdate/{idevent}")
    public String showFormForUpdate(@PathVariable("idevent") Long idevent, Model model) {
        // Récupérer l'événement par son ID
        Evenement evenement = evenementService.getEvenementById(idevent);

        // Ajouter l'événement au modèle pour le formulaire
        model.addAttribute("evenement", evenement);

        // Retourner la vue de mise à jour
        return "update_evenement";
    }
    @GetMapping("/deleteEvenement/{idevent}")
    public String deleteEvenement(@PathVariable ( value = "idevent") Long idevenet) {
        this.evenementService.deleteEvenementById(idevenet);
        return "redirect:/";
    }

    @GetMapping("/accueil.html")
    public String afficherAccueil() {
        return "accueil"; // Correspond au fichier accueil.html dans le dossier templates
    }
}