package com.example.Test.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Test.Entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

}
