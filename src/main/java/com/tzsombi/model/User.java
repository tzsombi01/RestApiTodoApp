package com.tzsombi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ta_users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "admin", nullable = false)
    private boolean admin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @OneToOne(
            targetEntity = Image.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    @JsonBackReference
    private Image image;

    @OneToMany(
            targetEntity = Todo.class,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private List<Todo> todos;
}
