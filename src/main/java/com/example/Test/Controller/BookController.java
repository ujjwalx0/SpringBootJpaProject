package com.example.Test.Controller;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import com.example.Test.Entity.Book;
import com.example.Test.Service.BookServiceImpl;
import org.thymeleaf.context.Context;


@RequestMapping(value = "/Books")
@RestController
public class BookController {

private final BookServiceImpl bookServiceImpl;

private final TemplateEngine templateEngine;

@Autowired
public BookController(BookServiceImpl bookServiceImpl, TemplateEngine templateEngine) {
    this.bookServiceImpl = bookServiceImpl;
    this.templateEngine = templateEngine;
}

@GetMapping("/books")
public List<Book> getAll() throws Exception {
	return bookServiceImpl.getAllBooks();
}
@GetMapping("/books-json")
public ResponseEntity<List<Book>> getAllBooksJson() {
    List<Book> books = bookServiceImpl.getAllBooks();
    return ResponseEntity.ok(books);
}
@PostMapping("/book")

public Book addBook(@RequestBody Book book) {
	return bookServiceImpl.addBook(book);
}


@GetMapping("/book/{id}")
public ResponseEntity<Object> getBookById(@PathVariable("id") long id) {
	Optional<Book> existingBook = bookServiceImpl.getBookById(id);

	if (existingBook.isPresent()) {
		return ResponseEntity.ok(existingBook.get());
	} else {
		String errorMessage = "Book with ID " + id + " not found.";
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
	}
}

//@GetMapping("/load-book-list")
//public ResponseEntity<String> loadBookList(Model model) {
//    try {
//        Context context = new Context();
//        // Add any necessary data to the context if needed
//        String content = templateEngine.process("booklist", context);
//
//        return ResponseEntity.ok(content);
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while loading the content.");
//    }
//}

@PutMapping("/book/{id}")
public ResponseEntity<Object> updateBookById(@PathVariable Long id, @RequestBody Book updatedBook) {
	Optional<Book> existingBook = bookServiceImpl.getBookById(id);

	if (existingBook.isPresent()) {
		Book updatedBookEntity = bookServiceImpl.updateBookById(id, updatedBook);
		return new ResponseEntity<>(updatedBookEntity, HttpStatus.OK);
	} else {
		String errorMessage = "Book with ID " + id + " not found.";
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
	}
}

@DeleteMapping(value = "/book/{id}")

public String deleteViaId(@PathVariable(value = "id") Long id) {
Optional<Book> existingBook = bookServiceImpl.getBookById(id);
if (existingBook.isPresent()) {
	bookServiceImpl.deleteBookById(id);
	return "Deleted the Book with Id :" + id;
}
	return "Book with id :" + id + " not found";
}
}
