package library.controller;

import library.LibraryApplication;
import library.model.BookService;
import library.model.ReaderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class BookController {
    public static final String BOOKS_MODEL_NAME = "books";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String BOOKS_TEMPLATE = "BooksList";
    
    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;

    @GetMapping("/books/list")
    public String listbooks(
        @RequestParam(name = "page") Optional<Integer> pageNum, 
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Integer> readerId,
        Model model) {
            // Set background color of response page
            model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());

            if (readerId.isPresent()) {
                model.addAttribute(READER_MODEL_NAME, readerId.get());
                //retrieve books borrowed by reader and add them to the Model object being returned to ViewResolver
                model.addAttribute(BOOKS_MODEL_NAME, bookService.findBooksByReaderId(readerId.get()));

            }
            else {
                // Retrieve books and add them to the Model object being returned to ViewResolver
                model.addAttribute(BOOKS_MODEL_NAME, bookService.retrieveBooks(pageNum, pageSize));
            }
            
            // Returns the name of the template view to reply this request
            return BOOKS_TEMPLATE;
    }

    @PostMapping("/books/load")
    @ResponseBody
    public String reqLoadDatabase(@RequestParam Optional<Integer> count) {
        val books =  bookService.loadDatabase(
            count,
            Optional.of(
                readerService.retrieveReaders(
                    Optional.empty(),
                    Optional.empty()
                )
            )
        );
        return String.format("Book database loaded with %d records", books.size());
    }
}