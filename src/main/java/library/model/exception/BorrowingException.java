package library.model.exception;

import java.util.Set;

import library.model.ReaderService.BorrowingErrors;

public class BorrowingException extends Exception {
    public Set<BorrowingErrors> errors;

    public BorrowingException(Set<BorrowingErrors> errors) {
        super();
        this.errors = errors;
    }
    
    private static final long serialVersionUID = 1L;
}