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
    @SequenceGenerator(
            name = "ta_users_sequence",
            sequenceName=  "ta_users_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ta_users_sequence"
    )
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

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
    @JoinColumn(name = "fk_image_id", referencedColumnName = "image_id")
    @JsonBackReference
    private ImageData image;

    @OneToMany(
            targetEntity = Todo.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private List<Todo> todos;
}
