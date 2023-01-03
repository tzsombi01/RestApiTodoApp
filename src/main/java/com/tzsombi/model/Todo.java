package com.tzsombi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "ta_todos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Todo implements Serializable {

    @Id
    @SequenceGenerator(
            name = "ta_todo_sequence",
            sequenceName = "ta_todo_sequence",
            allocationSize = 10
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ta_todo_sequence"
    )
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean notified;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "user_id")
    private Long userId;
}