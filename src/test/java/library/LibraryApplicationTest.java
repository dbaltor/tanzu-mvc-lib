package library;

import library.model.Book;
import library.model.BookRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LibraryApplication.class)
public class LibraryApplicationTest{

	@Autowired
	private BookRepository bookRepository;
	
	private final Faker faker = new Faker();	

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
		Optional<Book> book = bookRepository.findById(testBooks.get(1).getId());
		//Then
		assertTrue(book.isPresent());
	}

	@Test
	public void shouldFindByName() {
		//Given
		testBooks = List.of(
			Book.of("Java","","",""),
			Book.of("Node","","",""),
			Book.of("Go","","",""));
		testBooks = (List<Book>) bookRepository.saveAll(testBooks);
		//When
		List<Book> books = bookRepository.findByName("Java");
		//Then
		assertThat(books.size(), is(1));
	}
	
	@Test
	public void shouldStoreAllBooks() {
		//Given
		testBooks = new ArrayList<>();
		List<Long> ids = Stream.iterate(0, e -> e + 1)
			.limit(NUM_TEST_BOOKS)
			.map(e -> Book.of(
				faker.book().title(),
				faker.book().author(),
				faker.book().genre(),
				faker.book().publisher()))
			.peek(book -> bookRepository.save(book))
			.peek(testBooks::add)
			.map(book -> book.getId())
			.collect(toList());
		//When
		List<Book> books = (List<Book>)bookRepository.findAllById(ids);
		//Then
		assertThat(books.size(), is(NUM_TEST_BOOKS));
	}
}
