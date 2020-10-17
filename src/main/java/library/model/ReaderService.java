package library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.*;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
public class ReaderService {
    @Value("${reader.default-page-size}")
    private int DEFAULT_PAGE_SIZE;
    @Value("${reader.default-load-size}")
    private int DEFAULT_LOAD_SIZE;

    private final Faker faker = new Faker();
    private final @NonNull ReaderRepository readerRepository;

    //private final @NonNull BookService bookService;
    
    public List<Reader> loadDatabase(Optional<Integer> nReaders) {
        val total = nReaders.orElse(DEFAULT_LOAD_SIZE);
        val readers = new ArrayList<Reader>(total);
        for(int i = 0; i < total; i++) {
            readers.add(
                Reader.of(
                    faker.name().firstName(), 
                    faker.name().lastName(),
                    //new SimpleDateFormat("dd-MM-yyyy").format(faker.date().birthday()),
                    faker.date().birthday(),
                    faker.address().streetAddress(), 
                    faker.phoneNumber().phoneNumber()
                )
            );
        }
        readerRepository.saveAll(readers);
        log.info(String.format("Reader database loaded with %d records", total));
        return readers;
    }

    public void cleanUpDatabase() {
        readerRepository.deleteAll();
        log.info("Reader database cleaned up...");        
    }

    public List<Reader> retrieveReaders(Optional<Integer> pageNum, Optional<Integer> pageSize) {
        val readers = new ArrayList<Reader>();
        if (pageNum.isPresent() ) {
            readerRepository
                .findAll(PageRequest.of(pageNum.get(), pageSize.orElse(DEFAULT_PAGE_SIZE)))
                .forEach(reader -> {
                    readers.add(reader);
                });
        }
        else {
            readerRepository
                .findAll()
                .forEach(reader -> {
                    readers.add(reader);
                });
        }
        return readers;
    }
    
    public Optional<Reader> retrieveReader(long id) {
        return readerRepository.findById(id);
    }

    /**************************************************\
                    Business Rules
    \**************************************************/
    @Value("${reader.max-allowed-borrowed-books}")
    private int MAXIMUM_ALLOWED_BORROWED_BOOKS; 

    public enum BorrowingErrors {
        MAX_BORROWED_BOOKS_EXCEEDED
        // Future error codes
    }

    public enum ReturningErrors {
        PLACEHOLDER
        // Future error codes
    }

    /**
     * Validate whether the list of books can be borrowed by the reader.
     * @param reader        The reader trying to borrow the books
     * @param booksToBorrow The list of books to borrow
     * @return              The set of validation failures, is any. Otherwise, an empty set.         
     */
    public Set<BorrowingErrors> bookBorrowingValidator(List<Book> booksToBorrow, Reader reader) {
        // List of all validation criteria to be applied
        Map<BorrowingErrors, Predicate<List<Book>>> validators = new HashMap<>();
        
        // Validation criterium: maximum borrowing books not to exceed 
        Predicate<List<Book>> maxBorrowingExceeded = 
            books -> books.size() + reader.getBooks().size() > MAXIMUM_ALLOWED_BORROWED_BOOKS;
        validators.put(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED, maxBorrowingExceeded);
        
        // Future additional criteria...
        // ...

        // Applying the validation criteria
        return validators.entrySet()
            .stream()
            .filter(map -> map.getValue().test(booksToBorrow)) // filter out the successful ones
            .map(map -> map.getKey())
            .collect(toSet());
        
    }
   
     /**
     * Validate whether the list of books can be returned by the reader.
     * @param reader        The reader trying to return the books
     * @param booksToBorrow The list of books to return
     * @return              The set of validation failures, if any. Otherwise, an empty set.        
     */
    public Set<ReturningErrors> bookReturningValidator(List<Book> booksToReturn, Reader reader) {
        return new HashSet<>();
    }
}