package com.bookstore.book;

import com.bookstore.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;
    private final UserRepository userRepository;

    public void save(Book book){
        repository.save(book);
    }

    public List<Book> getAllBooks(){
      return repository.findAll();
    }

    public Book getBookById(Integer id){
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteBookById(Integer id) {
        Optional<Book> bookOptional = repository.findById(id);
        if(bookOptional.isPresent()){
            Book book = bookOptional.get();

            //Remove the book from all users that have it
            book.getUserList().forEach(user -> user.getBookList().remove(book));
            userRepository.saveAll(book.getUserList());

            //now safe to delete the book
            repository.delete(book);
        }
        repository.deleteById(id);
    }
}
