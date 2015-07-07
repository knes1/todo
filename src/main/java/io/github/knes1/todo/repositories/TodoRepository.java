package io.github.knes1.todo.repositories;

import io.github.knes1.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author knesek
 * Created on: 07/07/15
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
