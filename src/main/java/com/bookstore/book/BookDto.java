package com.bookstore.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class BookDto {
    public Integer id;
    public String name;
    public String author;
    public BigDecimal price;
    public Boolean notAdded;
}
