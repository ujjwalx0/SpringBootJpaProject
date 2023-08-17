package com.example.Test.Service;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.Test.Entity.Book;
import com.example.Test.Exception.BookValidationException;
import com.example.Test.Repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

@Autowired
   public BookRepository bookRepo;

	@Override
	public List <Book> getAllBooks(){

	     Sort sort = Sort.by(Sort.Direction.DESC, "id");
	        List<Book> allBooks = bookRepo.findAll(sort);
		
		return allBooks;
	}
	
	@Override
	public Book addBook(Book book) {
	
		validateBook(book);

		bookRepo.save(book);
		return book;
	}
	 private void validateBook(Book book) {
	        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
	            throw new BookValidationException("Author cannot be blank.");
	        }

	        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
	            throw new BookValidationException("Title cannot be blank.");
	        }

	        if (book.getPublicationYear() == null) {
	            throw new BookValidationException("Publication year cannot be null.");
	        }

	        int currentYear = Year.now().getValue();
	        if (book.getPublicationYear() > currentYear) {
	            throw new BookValidationException("Please input a valid publication year.");
	        }
	    }

	
	
	@Override
	public Optional<Book> getBookById(Long id) {
		
		return bookRepo.findById(id);
	}
	

 
    @Override
    public Book updateBookById(Long id, Book updatedBook) {
        Optional<Book> existingBookOptional = bookRepo.findById(id);

        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();

            if (updatedBook.getAuthor() != null && !updatedBook.getAuthor().isBlank()) {
                existingBook.setAuthor(updatedBook.getAuthor());
            }

            if (updatedBook.getTitle() != null && !updatedBook.getTitle().isBlank()) {
                existingBook.setTitle(updatedBook.getTitle());
            }

            int currentYear = Year.now().getValue();
            if (updatedBook.getPublicationYear() != null && updatedBook.getPublicationYear() <= currentYear) {
                existingBook.setPublicationYear(updatedBook.getPublicationYear());
            } else {
                throw new BookValidationException("Please input a valid publication year.");
            }

            return bookRepo.save(existingBook);
        } else {
            throw new IllegalArgumentException("Book with ID " + id + " not found.");
        }
    }

	    @Override
	    public void deleteBookById(Long id) {
	    	bookRepo.deleteById(id);
	    }

		
	
}
