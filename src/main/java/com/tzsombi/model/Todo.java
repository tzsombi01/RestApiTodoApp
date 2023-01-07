package com.tzsombi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public boolean isItDueInADay(Clock clock) {
        return ChronoUnit.HOURS.between(LocalDateTime.now(clock), this.getDueDate()) <= 24;
    }
}