package library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javafaker.Faker;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
public class ReaderService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_LOAD_SIZE = 10;
    private final Faker faker = new Faker();
    private final @NonNull ReaderRepository readerRepository;

    private final @NonNull BookService bookService;
    
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
                    reader.setBooks(bookService.findBooksByReaderId(reader.getId()));
                    readers.add(reader);
                });
        }
        else {
            readerRepository
                .findAll()
                .forEach(reader -> {
                    reader.setBooks(bookService.findBooksByReaderId(reader.getId()));
                    readers.add(reader);
                });
        }
        return readers;
    }
    
    public Optional<Reader> retrieveReader(long id) {
        val reader = readerRepository.findById(id);
        if (reader.isPresent()) {
            reader.get().setBooks(
                bookService.findBooksByReaderId(
                    reader.get().getId()
                    )
            );
        }
        return reader;
    }
}