package library.controller;

import library.LibraryApplication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    public static final String BOOKS_TEMPLATE = "Index";
    
    @GetMapping("/")
    String index(Model model) {
        // Set background color of response page
        model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());
        return BOOKS_TEMPLATE;
    }    
}