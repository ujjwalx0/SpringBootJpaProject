package com.example.Test.Service;
import java.util.List;
import java.util.Optional;

import com.example.Test.Entity.Book;

public interface BookService {

	List <Book> getAllBooks();
	
	Book addBook(Book book);
	
	Optional<Book> getBookById(Long id);
	
    Book updateBookById(Long id, Book updatedBook);

    void deleteBookById(Long id);
}
