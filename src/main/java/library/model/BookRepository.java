package library.model;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    public List<Book> findByName(String name);

    @Query(value = "select b from Book b where b.reader.id = :readerId")
    public List<Book> findByReader(@Param("readerId") long readerId);
}