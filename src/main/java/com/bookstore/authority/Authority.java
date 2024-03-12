package com.bookstore.authority;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @SequenceGenerator(name = "authority_sequence",
                      sequenceName = "authority_sequence",
                      allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                generator = "authority_sequence"
    )
    private Integer id;
    private String name;
}
