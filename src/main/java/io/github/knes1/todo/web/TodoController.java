package io.github.knes1.todo.web;

import io.github.knes1.todo.model.Todo;
import io.github.knes1.todo.repositories.TodoRepository;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author knesek
 * Created on: 07/07/15
 */
@Controller
public class TodoController {

	private final TodoRepository todoRepository;

	@Autowired
	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@RequestMapping("/")
	public String todos(Model model) {
		model.addAttribute("todos", todoRepository.findAll());
		return "todos";
	}

	@RequestMapping(value = "/todos/{id}/delete")
	public String deleteTodo(@PathVariable("id") Long id) {
		todoRepository.delete(id);
		return "redirect:/";
	}

	@RequestMapping(value = "/todos/{id}/completed")
	public String deleteTodo(@PathVariable("id") Todo todo) {
		todo.setCompleted(true);
		todoRepository.save(todo);
		return "redirect:/";
	}

	@RequestMapping(value = "/todos", method = RequestMethod.POST)
	public String createTodo(@Valid TodoDto todoDto) {
		todoRepository.save(todoDto.toTodo());
		return "redirect:/";
	}

	/**
	 * TO DO DTO TO DO DTO
	 * TRANSFERS DATA FROM YOU TO TODO
	 * WHEN YOU CALL TO TODO
	 *
	 * SHOOBIE DOOBIE YEAAA!
	 */
	protected static class TodoDto {

		@NotEmpty
		@Length(min = 1, max = 100)
		private String description;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Todo toTodo() {
			return new Todo(description);
		}
	}

}
