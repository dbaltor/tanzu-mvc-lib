package library.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import library.model.exception.BorrowingException;
import library.model.exception.ReturningException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.*;

import com.github.javafaker.Faker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_LOAD_SIZE = 100;
    private static final int DEFAULT_BORROWED_BOOKS = 2 * DEFAULT_PAGE_SIZE;

    private final @NonNull BookRepository bookRepository;

    private final Faker faker = new Faker();

    public List<Book> loadDatabase(Optional<Integer> nBooks, Optional<List<Reader>> readers) { 
        val NON_READERS = 3;
        val hasReaders = readers.isPresent() && readers.get().size() > 0;
        var nReaders = 0;
        if (hasReaders) {
             // Radndomly remove one reader who will not have any book assigned
            nReaders = readers.get().size();
            for (int i = 0; i < NON_READERS; i++){
                readers.get().remove((int)(Math.random() * nReaders));
                nReaders = readers.get().size();
            }
        }
        val total = nBooks.orElse(DEFAULT_LOAD_SIZE);
        val books = new ArrayList<Book>(total);   
        for(int i = 0; i < total; i++) {
            var book = Book.of(
                faker.book().title(), 
                faker.book().author(),
                faker.book().genre(),
                faker.book().publisher()
            );
            // Try to borrow each book to a differnt reader through all readers
            // or at least for DEFAULT_BORROWED_BOOKS books
            if (hasReaders && (i < nReaders || i < DEFAULT_BORROWED_BOOKS)) {
                try {
                    borrowBooks(
                        List.of(book), 
                        readers.get().get(i % nReaders)
                    );
                } catch(BorrowingException e) {
                    // book could not be borrowed but need to be saved
                    book = bookRepository.save(book);
                }
            }
            else { // save book without being borrowed
                book = bookRepository.save(book);
            }
            books.add(book);
        }
        log.info(String.format("Book database loaded with %d records.", total));
        return books;
    }

    public void cleanUpDatabase() {
        bookRepository.deleteAll();
        log.info("Book database cleaned up...");
    }

    public Optional<Book> retrieveBook(long id) {
        return bookRepository.findById(id);
    }

    public List<Book> retrieveBooks(Optional<Integer> pageNum, Optional<Integer> pageSize) {
        val books = new ArrayList<Book>();
        if (pageNum.isPresent()) {
            bookRepository
                .findAll(PageRequest.of(pageNum.get(), pageSize.orElse(DEFAULT_PAGE_SIZE)))
                .forEach(books::add);
        }
        else {
            bookRepository
                .findAll()
                .forEach(books::add);
        }
        return books;
    }

    public List<Book> findBooksByIds(List<Long> ids) {
       val books = new ArrayList<Book>();
        bookRepository.findAllById(ids).forEach(books::add);
        return books;
    }    

    public List<Book> findBooksByReaderId(long id) {
        return bookRepository.findByReader(id);
    }

    public List<Book> borrowBooks(List<Book> booksToBorrow, Reader reader) throws BorrowingException {
        // filter out books already borrowed
        val books = booksToBorrow
            .stream()
            .filter(book -> book.getReaderId() == 0)
            .collect(toList());
        // Validate list as per business rules
        val errors = bookBorrowingValidator(books, reader);
        if (!errors.isEmpty()){
            throw new BorrowingException(errors);
        }
        val borrowedBooks = books.stream()
            .map(book -> {
                book.setReaderId(reader.getId()); // associate the book to the reader
                return book;
            })
            .collect(toList());
        bookRepository.saveAll(borrowedBooks);
        reader.getBooks().addAll(borrowedBooks);
        return borrowedBooks; // return list of borrowed books
   }

    public List<Book> returnBooks(List<Book> booksToReturn, Reader reader) throws ReturningException {
        // filter out books not borrowed by this reader
        val books = booksToReturn
            .stream()
            .filter(book -> book.getReaderId() == reader.getId())
            .collect(toList());
        // Validate list as per business rules
        val errors = bookReturningValidator(books, reader);
        if (!errors.isEmpty()){
            throw new ReturningException(errors);
        }
        val returnedBooks = books.stream()
            .map(book -> {
                book.setReaderId(0); // disassociate the book from the reader
                return book;
            })
            .collect(toList());
        bookRepository.saveAll(returnedBooks);
        reader.getBooks().removeAll(returnedBooks);
        return returnedBooks; // return list of returned books
    }

     /**************************************************\
                    Business Rules
    \**************************************************/    
    private static final int MAXIMUM_BORROWED_BOOKS = 6;

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
        Predicate<List<Book>> maxBorrowingNotExceeded = 
            books -> books.size() + reader.getBooks().size() <= MAXIMUM_BORROWED_BOOKS;
        validators.put(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED, maxBorrowingNotExceeded);
        // Future additional criteria...
        
        Set<BorrowingErrors> errors = new HashSet<>();
        if (!validators.get(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED).test(booksToBorrow)) {
            errors.add(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED);
        }
        // Future additional tests...

        return errors;
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