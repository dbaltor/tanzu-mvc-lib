package library.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Book {
    //@GeneratedValue(strategy = GenerationType.AUTO) // default approach equivalent to the below   
    private @Id @GeneratedValue long id;
    private @NonNull String name;
    private @NonNull String author;
    private @NonNull String genre;
    private @NonNull String publisher;

    // JPA Join (Monolithic)
    @ManyToOne(fetch = FetchType.LAZY) // otherwise, we’d fall back to EAGER fetching which is bad for performance.
    private Reader reader;
}