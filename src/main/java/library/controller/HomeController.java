package library.controller;

import library.LibraryApplication;
import library.model.BookService;
import library.model.ReaderService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HomeController {
    public static final String BOOKS_TEMPLATE = "Index";

    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;
    
    @GetMapping("/")
    String index(Model model) {
        // Set background color of response page
        model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());
        return BOOKS_TEMPLATE;
    }
    
    @PostMapping("/cleanup")
    @ResponseBody
    String cleanUp() {
        // Books must be firstly removed due to referential constraint.
        // BookService disassociate books before deleting them.
        bookService.cleanUpDatabase();
        readerService.cleanUpDatabase();
        return "The data have been removed";
    }
}