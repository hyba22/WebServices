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




        // Reading data from database
    @GetMapping({"","/userstab"})
    public String showEvenementList(Model model) {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("users",users);
        return "userstab";
    }

    //Adding new user
    @GetMapping("/addUser")
    public String showAddPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("userDto",userDto);
        return "addUser";
    }

    
    @PostMapping("/addUser")
    public String addNewEvent(@Valid @ModelAttribute UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "addUser";
        }
    
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFullname(userDto.getFullname());
        user.setRole(userDto.getRole());
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    
        userRepository.save(user);
    
        return "redirect:/userstab";
    }
    
    // Show form for updating an existing event
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(Model model, @PathVariable long id){
        try{
            User user = userRepository.findById(id).get();
            model.addAttribute("user", user);
            UserDto userDto = new UserDto();
			user.setEmail(userDto.getEmail());
			user.setPassword(userDto.getPassword());
			user.setRole(userDto.getRole());
			user.setFullname(userDto.getFullname());
	
            model.addAttribute("userDto",userDto);
        }catch(Exception e){
            System.out.println("Expection :" +e.getMessage());
            return "redirect:/userstab";
        }
        return "update_user";
    }


    @PostMapping("/updateUser/{id}")
    public String updateUser(
            @PathVariable long id,
            @Valid @ModelAttribute UserDto userDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "update_user";
        }

        // Fetch the existing user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
				user.setEmail(userDto. getEmail());
				user.setPassword(userDto.getPassword());
				user.setRole(userDto.getRole());
				user.setFullname(userDto.getFullname());
        // Encode the password before updating
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user);



        return "redirect:/userstab";
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

}
