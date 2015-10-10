package io.github.knes1.todo.repositories;

import io.github.knes1.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * @author knesek
 * Created on: 07/07/15
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

	@Transactional(readOnly = true)
	@Query("select t from Todo t")
	Stream<Todo> streamAll();

}
