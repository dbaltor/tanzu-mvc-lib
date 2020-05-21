package library.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Book {
    //@GeneratedValue(strategy = GenerationType.AUTO) // default approach equivalent to the below   
    private @Id @GeneratedValue long id;
    private @NonNull String name;
    private @NonNull String author;
    private @NonNull String genre;
    private @NonNull String publisher;

    // DDD aggregate reference
    private @Setter long readerId;
}