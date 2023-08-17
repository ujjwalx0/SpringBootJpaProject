package com.example.Test.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	@Id
	 @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(name = "book_seq", sequenceName = "book_seq", allocationSize = 1)

	private Long id;
	 @NotBlank(message = "Author cannot be blank")
	    private String author;

	    @NotBlank(message = "Title cannot be blank")
	    private String title;

	    @NotNull(message = "Publication year cannot be null")
	    private Long publicationYear;
	

}
