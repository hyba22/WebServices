package com.webservices.projectweb.controller;

import com.webservices.projectweb.dto.EvenementDto;
import com.webservices.projectweb.model.Notification;
import com.webservices.projectweb.repository.NotificationRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired
    private EvenementRepository evenementRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    // variable to change the check if the user is subscribed or not
    private boolean isSubscribed = false;


    // Reading data from database
    @GetMapping({"","/"})
    public String showEvenementList(Model model) {
        List<Evenement> evenements = evenementRepository.findAll(Sort.by(Sort.Direction.DESC,"idevent"));
        model.addAttribute("evenements",evenements);
        return "index";
    }

    //Show adding new events
    @GetMapping("/addEvent")
    public String showAddPage(Model model) {
        EvenementDto evenementDto = new EvenementDto();
        model.addAttribute("evenementDto",evenementDto);
        return "addEvent";
    }

    //Create method
    @PostMapping("/addEvent")
    public String addNewEvent (@Valid @ModelAttribute EvenementDto evenementDto, BindingResult result,Model model){
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
        model.addAttribute("success", true);

        // Send WebSocket notificationans storing in database
        String notificationMessage=" Un nouveau événement est ajouté: " + evenement.getTitre();
        messagingTemplate.convertAndSend("/topic/notifications", notificationMessage);
        Notification notification = new Notification();
        notification.setMessage(notificationMessage);
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);
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

  // Update Post method
    @PostMapping("/updateEvenement/{idevent}")
    public String updateEvenement(
            @PathVariable long idevent,
            @Valid @ModelAttribute EvenementDto evenementDto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "update_evenement";
        }

        Evenement evenement = evenementRepository.findById(idevent)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + idevent));
        evenement.setTitre(evenementDto.getTitre());
        evenement.setDate(evenementDto.getDate());
        evenement.setEtat(evenementDto.getEtat());
        evenement.setCategorie(evenementDto.getCategorie());
        evenement.setDescription(evenementDto.getDescription());
        evenement.setLocalisation(evenementDto.getLocalisation());
        evenementRepository.save(evenement);
        model.addAttribute("success", true);
        // Send WebSocket notification and saving in database
        String notificationMessage ="Event : " + evenement.getTitre() + "est à jour!";
        messagingTemplate.convertAndSend("/topic/notifications",notificationMessage );
        Notification notification = new Notification();
        notification.setMessage(notificationMessage);
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);
        return "redirect:/evenements";
    }

    // Delete method

    @GetMapping("/delete")
    public String delete(@RequestParam long idevent, Model model){

        try{
            Evenement evenement = evenementRepository.findById(idevent)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + idevent));
            // delete event
            evenementRepository.delete(evenement);
            model.addAttribute("success", true);
            // Send WebSocket notification
            String notificationMessage ="Event deleted: " + evenement.getTitre();
            messagingTemplate.convertAndSend("/topic/notifications",notificationMessage );
            Notification notification = new Notification();
            notification.setMessage(notificationMessage);
            notification.setTimestamp(LocalDateTime.now());
            notificationRepository.save(notification);
            }catch(Exception e){
            System.out.println("Exception : " + e.getMessage());
        }

        return "redirect:/evenements";
    }

    // to test if web socket messages are working in the backend
    @GetMapping("/test-notification")
    @ResponseBody
    public String sendTestNotification() {
        messagingTemplate.convertAndSend("/topic/notifications", "Test notification from the backend");
        return "Notification sent successfully!";
    }

    @GetMapping({"/eventsViews"})
    public String EvenementList(Model model) {
        List<Evenement> evenements = evenementRepository.findAll(Sort.by(Sort.Direction.DESC, "idevent"));
        model.addAttribute("evenements", evenements);
        model.addAttribute("isSubscribed", isSubscribed);
        return "view_events";
    }

    @GetMapping("/abonnerPage")
    public String abonnerPage(RedirectAttributes redirectAttributes) {
        isSubscribed = true; // Set subscription state
        redirectAttributes.addFlashAttribute("notification", "Vous êtes maintenant abonné !");
        return "redirect:/evenements/eventsViews"; // Redirect back to the events view
    }

    @GetMapping("/desabonnerPage")
    public String desabonnerPage(RedirectAttributes redirectAttributes) {
        isSubscribed = false; // Reset subscription state
        redirectAttributes.addFlashAttribute("notification", "Vous vous êtes désabonné.");
        return "redirect:/evenements/eventsViews"; // Redirect back to the events view
    }

}

