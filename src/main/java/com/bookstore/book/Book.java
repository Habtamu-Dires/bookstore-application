package com.bookstore.book;

import com.bookstore.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @SequenceGenerator(name = "book_sequence",
                    sequenceName = "book_sequence",
                    allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                generator = "book_sequence"
    )
    private Integer id;
    private String name;
    private String author;
    private BigDecimal price;

    @ManyToMany(mappedBy = "bookList")
    List<User> userList;

}
