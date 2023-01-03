package com.tzsombi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @SequenceGenerator(
            name = "ta_users_sequence",
            sequenceName= "ta_users_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ta_users_sequence"
    )
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
            targetEntity = ImageData.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    @JsonBackReference
    @ToString.Exclude
    private ImageData image;

    @OneToMany(
            targetEntity = Todo.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private List<Todo> todos;
}
