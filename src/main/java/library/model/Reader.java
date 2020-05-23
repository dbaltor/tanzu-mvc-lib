package library.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    
    // DDD Aggregate
    @ElementCollection(targetClass=Book.class)
    private @Setter List<Book> books = new ArrayList<>();
}