package library.controller;

import library.LibraryApplication;
import library.model.ReaderService;
import library.model.BookService;
import library.model.Reader;
import library.model.exception.BorrowingException;
import library.model.exception.ReturningException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.ToString;

@Controller
@RequiredArgsConstructor
public class ReaderController {
    public static final String READERS_MODEL_NAME = "readers";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String READERS_TEMPLATE = "ReadersList";
    public static final String READERS_MSG_TEMPLATE = "ReadersMsg";
    public static final String READERS_LOADED = "readersLoaded";
    private final @NonNull ReaderService readerService;
    private final @NonNull BookService bookService;

    @GetMapping("/readers/list")
    public String listReaders(
        @RequestParam(name = "page") Optional<Integer> pageNum,
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Integer> readerId,
        Model model) {
            // Set background color of response page
            model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());

            if (readerId.isPresent()) {
                model.addAttribute(READER_MODEL_NAME, readerId.get());
                //retrieve reader and add them to the Model object being returned to ViewResolver
                val readers = new ArrayList<Reader>();
                val reader = readerService.retrieveReader(readerId.get());
                if (reader.isPresent()) {
                    readers.add(reader.get());
                }
                // Adds readers to the Model object being returned to ViewResolve
                model.addAttribute(READERS_MODEL_NAME, readerId.get());
                model.addAttribute(READERS_MODEL_NAME, readers);
            }
            else {
                // Retrieve readers and add them to the Model object being returned to ViewResolver
                model.addAttribute(READERS_MODEL_NAME, readerService.retrieveReaders(pageNum, pageSize));
            }
            // Returns the name of the template view to reply this request
            return READERS_TEMPLATE;
    }

    @PostMapping("/readers/load")
    @ResponseBody
    public String loadDatabase(@RequestParam Optional<Integer> count) {
        // load database
        val readers = readerService.loadDatabase(count);
        return String.format("Reader database loaded with %d records", readers.size());
    }

    @PostMapping("/readers/{id}/borrowBooks")
    @ResponseBody
    public String borrowBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest) {
            val reader = readerService.retrieveReader(readerId);
            if (reader.isPresent()){
                val booksToBorrow = bookService.findBooksByIds(Arrays.asList(booksRequest.bookIds));
                try{ 
                    val borrowedBooks = bookService.borrowBooks(booksToBorrow, reader.get());
                    return String.format("The reader ID %d has borrowed %d book(s).", readerId, borrowedBooks.size());
                } catch(BorrowingException e) {
                    val errorMsg = new StringBuilder("Errors found:");
                    for(BookService.BorrowingErrors error : e.errors) {
                        switch (error) {
                            case MAX_BORROWED_BOOKS_EXCEEDED:
                                errorMsg.append(" *Maximum allowed borrowed books exceeded.");
                                break;
                            default:
                                errorMsg.append(" *Unexpected error.");
                        }
                    }
                    return errorMsg.toString();
                }
            }
            return String.format("No reader with ID %d has been found.", readerId);
    }

    @PostMapping("/readers/{id}/returnBooks")
    @ResponseBody
    public String returnBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest) {
            val reader = readerService.retrieveReader(readerId);
            if (reader.isPresent()){
                val booksToReturn = bookService.findBooksByIds(Arrays.asList(booksRequest.bookIds));
                try{ 
                    val returnedBooks = bookService.returnBooks(booksToReturn, reader.get());
                    return String.format("The reader ID %d has returned %d book(s).", readerId, returnedBooks.size());
                } catch(ReturningException e) {
                    val errorMsg = new StringBuilder("Errors found:");
                    for(BookService.ReturningErrors error : e.errors) {
                        switch (error) {
                            // Reserved for future usage
                            //case PLACEHOLDER:
                            //    errorMsg.append(" *....");
                            //    break;
                            default:
                                errorMsg.append(" *Unexpected error.");
                        }
                    }
                    return errorMsg.toString();
                }
            }
            return String.format("No reader with ID %d has been found.", readerId);
    }
}

@ToString
class BooksRequest {
    public Long bookIds[];
}