package library;

import library.model.Book;
import library.model.BookRepository;
import library.model.BookService;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LibraryApplication.class)
public class LibraryApplicationTest{

	@Autowired
	private BookService BookService;
	@Autowired
	private BookRepository bookRepository;
	
	// List of books created during the test to be removed right after
	private List<Book> testBooks = new ArrayList<>();
	private static final int NUM_TEST_BOOKS = 20;
	
    @After
    public void teardown() {
		//Delete all books created for the tests
		bookRepository.deleteAll(testBooks);
    }	

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldFindById() {
		//Given
		testBooks = List.of(
			Book.of("Java","","",""),
			Book.of("Node","","",""),
			Book.of("Go","","",""));
		testBooks = (List<Book>)bookRepository.saveAll(testBooks);
		//When
		Optional<Book> book = BookService.retrieveBook(testBooks.get(1).getId());
		//Then
		assertTrue(book.isPresent());
	}
	
	@Test
	public void shouldStoreAllBooks() {
		//Given
		testBooks = new ArrayList<>();
		BookService.loadDatabase(Optional.of(NUM_TEST_BOOKS), Optional.empty());
		//When
		List<Book> books = BookService.retrieveBooks(Optional.empty(), Optional.empty());
		//Then
		assertThat(books.size(), is(NUM_TEST_BOOKS));
	}
}
