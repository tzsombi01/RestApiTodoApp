package com.tzsombi.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "ta_todos")
@Table(name = "ta_todos")
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
    @Column(
            name = "todo_id",
            nullable = false,
            updatable = false
    )
    private Long todoId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private boolean completed;

    @Column(name = "user_id")
    private Long userId;
    public Todo() {
    }

    public Todo(
                Long userId,
                String title,
                String description,
                boolean completed) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "todoId=" + todoId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                '}';
    }
}