package library.controller;

import library.LibraryApplication;
import library.model.ReaderService;
import library.model.BookService;
import library.model.Reader;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

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

    @GetMapping("/listreaders")
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

    @PostMapping("/loadreaders")
    @ResponseBody
    public String loadDatabase(@RequestParam Optional<Integer> count) {
        // load database
        val readers = readerService.loadDatabase(count);
        return String.format("Reader database loaded with %d records", readers.size());
    }
}