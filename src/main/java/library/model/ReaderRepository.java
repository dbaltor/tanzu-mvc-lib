package library.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReaderRepository extends PagingAndSortingRepository<Reader, Long> {
    
    public List<Reader> findByLastName(String lastName);
    public List<Reader> findByFirstName(String firstName);
    public List<Reader> findByDob(Date dob);    
}