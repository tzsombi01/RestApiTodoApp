package com.tzsombi.repositories;

import com.tzsombi.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository
        extends JpaRepository<Todo, Long> {

}
