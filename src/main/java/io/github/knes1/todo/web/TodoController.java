package io.github.knes1.todo.web;

import io.github.knes1.todo.model.Todo;
import io.github.knes1.todo.repositories.TodoRepository;
import io.github.knes1.todo.util.RequestStatisticsInterceptor;
import io.github.knes1.todo.util.TodoGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.stream.Stream;

import static io.github.knes1.todo.util.LambdaExceptionUtil.rethrowConsumer;

/**
 * @author knesek
 * Created on: 07/07/15
 */
@Controller
public class TodoController {

	private static final Logger log = LoggerFactory.getLogger(RequestStatisticsInterceptor.class);

	private final TodoRepository todoRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@RequestMapping("/")
	public String todos(Model model) {
		model.addAttribute("todos", todoRepository.findAll(new PageRequest(0, 50)).getContent());
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

	@RequestMapping(value = "/todos.csv", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public void exportTodosCSV(HttpServletResponse response) {
		response.addHeader("Content-Type", "application/csv");
		response.addHeader("Content-Disposition", "attachment; filename=todos.csv");
		response.setCharacterEncoding("UTF-8");
		try(Stream<Todo> todoStream = todoRepository.streamAll()) {
			PrintWriter out = response.getWriter();
			todoStream.forEach(rethrowConsumer(todo -> {
				String line = todoToCSV(todo);
				out.write(line);
				out.write("\n");
				entityManager.detach(todo);
			}));
			out.flush();
		} catch (IOException e) {
			log.info("Exception occurred " + e.getMessage(), e);
			throw new RuntimeException("Exception occurred while exporting results", e);
		}
	}

	@RequestMapping(value = "/todos2.csv", method = RequestMethod.GET)
	public void exportTodosCSVSlicing(HttpServletResponse response) {
		final int PAGE_SIZE = 1000;
		response.addHeader("Content-Type", "application/csv");
		response.addHeader("Content-Disposition", "attachment; filename=todos.csv");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter out = response.getWriter();
			int page = 0;
			Slice<Todo> todoPage;
			do {
				todoPage = todoRepository.findAllBy(new PageRequest(page, PAGE_SIZE));
				for (Todo todo : todoPage) {
					String line = todoToCSV(todo);
					out.write(line);
					out.write("\n");
				}
				entityManager.clear();
				page++;
			} while (todoPage.hasNext());
			out.flush();
		} catch (IOException e) {
			log.info("Exception occurred " + e.getMessage(), e);
			throw new RuntimeException("Exception occurred while exporting results", e);
		}
	}

	private String todoToCSV(Todo todo) {
		return String.join(",", "" + todo.getId(), "" + todo.getDateCreated(),
			"" + todo.getDescription(), "" + todo.isCompleted());
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
