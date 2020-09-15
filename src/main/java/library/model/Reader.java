package library.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@ToString @Getter @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Reader {
    private @GeneratedValue @Id long id;
    private @NonNull String firstName;
    private @NonNull String lastName;
    @Temporal(TemporalType.DATE)
    private @NonNull Date dob;
    private @NonNull String address;
    private @NonNull String phone;
    

    // JPA JOIN (Monolithic)
    //The problem with collections is that we can only use them when the number of child records is rather limited.
    //@OneToMany is practical only when many means few.
    //https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
    @OneToMany(mappedBy = "reader") // No need for CascadeType.ALL as this relationship is an Aggregation rather than Composition.
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        if (books.contains(book))
            return;
        books.add(book);
        book.setReader(this);
    }
    public void removeBook(Book book){
        if (!books.contains(book))
            return;
        books.remove(book);
        book.setReader(null);
    }
    
}