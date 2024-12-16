package com.example.admin_user.controller;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;
import com.example.admin_user.dto.UserDto;
import com.example.admin_user.model.User;
import com.example.admin_user.repositories.UserRepository;
import com.example.admin_user.service.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/")
public class UserController {
	
	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private UserService userService;


 @Autowired
    private PasswordEncoder passwordEncoder;




    @GetMapping({"", "/userstab"})
public String showEvenementList(Model model, Principal principal) {
    String username = principal.getName();
    User currentUser = userRepository.findByEmail(username);

    List<User> users;

    if ("PERSONNEL".equals(currentUser.getRole())) {
        users = userRepository.findByRole("CLIENT");
        model.addAttribute("dashboardUrl", "/personnel-page"); 
    } else {
        users = userRepository.findAll(); 
        model.addAttribute("dashboardUrl", "/admin-page");
    }

    model.addAttribute("users", users);
    return "userstab";
}


    
@GetMapping("/addUser")
public String showAddPage(Model model, Principal principal) {
    UserDto userDto = new UserDto();

    String username = principal.getName();
    User currentUser = userRepository.findByEmail(username);

    if ("PERSONNEL".equals(currentUser.getRole())) {
       
        userDto.setRole("CLIENT");
    }
    
    model.addAttribute("userDto", userDto);
    return "addUser";
}

@PostMapping("/addUser")
public String addNewUser(@Valid @ModelAttribute UserDto userDto, BindingResult result, Principal principal) {
    if (result.hasErrors()) {
        System.out.println(result.getAllErrors());
        return "addUser";
    }

    // Vérifier si l'utilisateur connecté est un PERSONNEL
    String username = principal.getName();
    User currentUser = userRepository.findByEmail(username);

    if ("PERSONNEL".equals(currentUser.getRole())) {
        userDto.setRole("CLIENT");
    }

    User user = new User();
    user.setEmail(userDto.getEmail());
    user.setFullname(userDto.getFullname());
    user.setRole(userDto.getRole());
    user.setPassword(passwordEncoder.encode(userDto.getPassword())); 

    userRepository.save(user);

    return "redirect:/userstab";
}

     

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(Model model, @PathVariable long id, Principal principal) {
        try {

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
    
            UserDto userDto = new UserDto();
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPassword());
            userDto.setRole(user.getRole());
            userDto.setFullname(user.getFullname());
    
            model.addAttribute("user", user);
            model.addAttribute("userDto", userDto);
            String currentUserRole = getCurrentUserRole(principal); 
            model.addAttribute("isPersonnel", currentUserRole.equals("PERSONNEL") && user.getRole().equals("CLIENT"));
    
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/userstab";
        }
        return "update_user";
    }
    
    @PostMapping("/updateUser/{id}")
    public String updateUser(
            @PathVariable long id,
            @Valid @ModelAttribute UserDto userDto,
            BindingResult result,
            Principal principal) {
    
        if (result.hasErrors()) {
            return "update_user";
        }
    
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
    
        // Vérifier si l'utilisateur connecté est "PERSONNEL" et la cible est un CLIENT
        String currentUserRole = getCurrentUserRole(principal);
        if (currentUserRole.equals("PERSONNEL") && user.getRole().equals("CLIENT")) {
            userDto.setRole(user.getRole()); // Ne pas autoriser le changement de rôle
        }
    
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullname(userDto.getFullname());
        user.setRole(userDto.getRole());
    
        userRepository.save(user);
    
        return "redirect:/userstab";
    }
    
    private String getCurrentUserRole(Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        return currentUser.getRole();
    }
    
     


    @GetMapping("/delete")
    public String delete(@RequestParam long id){
        try{
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
            // delete user
            userRepository.delete(user);
          
        }catch(Exception e){
            System.out.println("Exception : " + e.getMessage());
        }

        return "redirect:/userstab";
    }


	
	
	@GetMapping("/registration")
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
		return "register";
	}
	
	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
		userService.save(userDto);
		model.addAttribute("message", "Registered Successfuly!");
		return "register";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("user-page")
	public String userPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		return "user";
	}
	
	@GetMapping("admin-page")
	public String adminPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		return "admin";
	}
    @GetMapping("personnel-page")
	public String personnelPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		return "personnel";
	}

}
