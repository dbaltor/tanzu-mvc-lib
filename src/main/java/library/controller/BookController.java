package library.controller;

import library.LibraryApplication;
import library.model.BookService;
import library.model.ReaderService;
import library.model.exception.BorrowingException;
import library.model.exception.ReturningException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Arrays;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class BookController {
    public static final String BOOKS_MODEL_NAME = "books";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String BOOKS_TEMPLATE = "BooksList";
    
    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;

    @GetMapping("/listbooks")
    public String listbooks(
        @RequestParam("page") Optional<Integer> pageNum, 
        @RequestParam("size") Optional<Integer> pageSize,
        @RequestParam("reader") Optional<Integer> readerId,
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

    @PostMapping("/loadbooks")
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

    @PostMapping("/borrowbooks")
    @ResponseBody
    public ResponseEntity<String> borrowBooks(@RequestBody BooksRequest booksRequest) {
        val bookIds = booksRequest.bookIds;
        if (bookIds == null || bookIds.length == 0) {
            return ResponseEntity
                .badRequest()
                .body("No books provided. Nothing to do.");
        }
        val reader = readerService.retrieveReader(booksRequest.readerId);
        if (!reader.isPresent()){
            return ResponseEntity
                .badRequest()
                .body(String.format(
                    "No reader with ID %d has been found.", 
                    booksRequest.readerId));
        }
        val booksToBorrow = bookService.findBooksByIds(Arrays.asList(bookIds));
        try{ 
            val borrowedBooks = bookService.borrowBooks(booksToBorrow, reader.get());
            return ResponseEntity.ok(String.format(
                "The reader ID %d has borrowed %d book(s).", 
                booksRequest.readerId, 
                borrowedBooks.size()));
        } catch(BorrowingException e) {
            val errorMsg = new StringBuilder("Errors found:");
            for(ReaderService.BorrowingErrors error : e.errors) {
                switch (error) {
                    case MAX_BORROWED_BOOKS_EXCEEDED:
                        errorMsg.append(" *Maximum allowed borrowed books exceeded.");
                        break;
                    default:
                        errorMsg.append(" *Unexpected error.");
                }
            }
            return ResponseEntity
                .badRequest()
                .body(errorMsg.toString());
        }
    }

    @PostMapping("/returnbooks")
    @ResponseBody
    public  ResponseEntity<String> returnBooks(@RequestBody BooksRequest booksRequest) {
        val bookIds = booksRequest.bookIds;
        if (bookIds == null || bookIds.length == 0) {
            return ResponseEntity
                .badRequest()
                .body("No books provided. Nothing to do.");
        }
        val reader = readerService.retrieveReader(booksRequest.readerId);
        if (!reader.isPresent()){
            return ResponseEntity
                .badRequest()
                .body(String.format(
                    "No reader with ID %d has been found.", 
                    booksRequest.readerId));
        }
        val booksToReturn = bookService.findBooksByIds(Arrays.asList(bookIds));
        try{ 
            val returnedBooks = bookService.returnBooks(booksToReturn, reader.get());
            return ResponseEntity.ok(String.format(
                "The reader ID %d has returned %d book(s).", 
                booksRequest.readerId, 
                returnedBooks.size()));
        } catch(ReturningException e) {
            val errorMsg = new StringBuilder("Errors found:");
            for(ReaderService.ReturningErrors error : e.errors) {
                switch (error) {
                    // Reserved for future usage
                    //case PLACEHOLDER:
                    //    errorMsg.append(" *....");
                    //    break;
                    default:
                        errorMsg.append(" *Unexpected error.");
                }
            }
            return ResponseEntity
                .badRequest()
                .body(errorMsg.toString());
        }
    }
}

@ToString
class BooksRequest {
    public Long readerId;
    public Long bookIds[];
}
