package com.webservices.projectweb.controller;


import com.webservices.projectweb.dto.EvenementDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import com.webservices.projectweb.model.Evenement;
import com.webservices.projectweb.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired
    private EvenementRepository evenementRepository;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Reading data from database
    @GetMapping({"","/"})
    public String showEvenementList(Model model) {
        List<Evenement> evenements = evenementRepository.findAll(Sort.by(Sort.Direction.DESC,"idevent"));
        model.addAttribute("evenements",evenements);
        return "index";
    }

    //Adding new events
    @GetMapping("/addEvent")
    public String showAddPage(Model model) {
        EvenementDto evenementDto = new EvenementDto();
        model.addAttribute("evenementDto",evenementDto);
        return "addEvent";
    }

    @PostMapping("/addEvent")
    public String addNewEvent (@Valid @ModelAttribute EvenementDto evenementDto, BindingResult result){

        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "addEvent";
        }

        Evenement evenement = new Evenement();
        evenement.setTitre(evenementDto.getTitre());
        evenement.setDate(evenementDto.getDate());
        evenement.setEtat(evenementDto.getEtat());
        evenement.setCategorie(evenementDto.getCategorie());
        evenement.setDescription(evenementDto.getDescription());
        evenement.setLocalisation(evenementDto.getLocalisation());

        evenementRepository.save(evenement);
        // Send WebSocket notification
        messagingTemplate.convertAndSend("/topic/notifications", "New event added: " + evenement.getTitre());
        return "redirect:/evenements";
    }

    // Show form for updating an existing event
    @GetMapping("/showFormForUpdate/{idevent}")
    public String showFormForUpdate(Model model, @PathVariable long idevent){
        try{
            Evenement evenement = evenementRepository.findById(idevent).get();
            model.addAttribute("evenement", evenement);
            EvenementDto evenementDto = new EvenementDto();
            evenementDto.setTitre(evenement.getTitre());
            evenementDto.setDate(evenement.getDate());
            evenementDto.setEtat(evenement.getEtat());
            evenementDto.setCategorie(evenement.getCategorie());
            evenementDto.setDescription(evenement.getDescription());
            evenementDto.setLocalisation(evenement.getLocalisation());
            model.addAttribute("evenementDto",evenementDto);
        }catch(Exception e){
            System.out.println("Expection :" +e.getMessage());
            return "redirect:/evenements";
        }
        return "update_evenement";
    }


    @PostMapping("/updateEvenement/{idevent}")
    public String updateEvenement(
            @PathVariable long idevent,
            @Valid @ModelAttribute EvenementDto evenementDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "update_evenement";
        }

        // Fetch the existing event
        Evenement evenement = evenementRepository.findById(idevent)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + idevent));
        evenement.setTitre(evenementDto.getTitre());
        evenement.setDate(evenementDto.getDate());
        evenement.setEtat(evenementDto.getEtat());
        evenement.setCategorie(evenementDto.getCategorie());
        evenement.setDescription(evenementDto.getDescription());
        evenement.setLocalisation(evenementDto.getLocalisation());

        evenementRepository.save(evenement);
        // Send WebSocket notification
        messagingTemplate.convertAndSend("/topic/notifications", "Event updated: " + evenement.getTitre());


        return "redirect:/evenements";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam long idevent){
        try{
            Evenement evenement = evenementRepository.findById(idevent)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + idevent));
            // delete event
            evenementRepository.delete(evenement);
            // Send WebSocket notification
            messagingTemplate.convertAndSend("/topic/notifications", "Event deleted: " + evenement.getTitre());
        }catch(Exception e){
            System.out.println("Exception : " + e.getMessage());
        }

        return "redirect:/evenements";
    }

}
