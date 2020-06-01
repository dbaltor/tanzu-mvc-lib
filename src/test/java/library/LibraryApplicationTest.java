package library;

import library.model.Book;
import library.model.BookRepository;
import library.model.BookService;

import lombok.val;
import com.github.javafaker.Faker;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LibraryApplication.class)
@AutoConfigureMockMvc
public class LibraryApplicationTest{

	private final Faker faker = new Faker();

	@Autowired
    private MockMvc mockMvc;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private BookService bookService;

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
	public void shouldLoadHomePage() throws Exception {
		mockMvc.perform(get("/"))
			.andDo(print())
			.andExpect(view().name("Index"));
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
		Optional<Book> book = bookService.retrieveBook(testBooks.get(1).getId());
		//Then
		assertTrue(book.isPresent());
	}

	@Test
	public void shouldFindByIds() {
			//Given
			val ids = Stream.iterate(0, e -> e + 1)
					.limit(NUM_TEST_BOOKS)
					.map(e -> Book.of(
							faker.book().title(),
							faker.book().author(),
							faker.book().genre(),
							faker.book().publisher()))
					.map(book -> bookRepository.save(book))
					.map(book -> book.getId())
					.collect(toList());
			//When
			testBooks = bookService.findBooksByIds(ids);
			//Then
			assertThat(testBooks.size(), is(NUM_TEST_BOOKS));
	}
	
	@Test
	public void shouldRetrieveAllBooks() {
		//Given
		bookService.loadDatabase(Optional.of(NUM_TEST_BOOKS), Optional.empty());
		//When
		testBooks = bookService.retrieveBooks(Optional.empty(), Optional.empty());
		//Then
		assertThat(testBooks.size(), is(NUM_TEST_BOOKS));
	}
}
