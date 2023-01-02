package com.tzsombi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    @Column(name = "todo_id", nullable = false, updatable = false)
    private Long todoId;

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