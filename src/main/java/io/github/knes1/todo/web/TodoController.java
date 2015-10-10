package io.github.knes1.todo.web;

import io.github.knes1.todo.model.Todo;
import io.github.knes1.todo.repositories.TodoRepository;
import io.github.knes1.todo.util.RequestStatisticsInterceptor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
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
	public void exportTodosCSV(HttpServletResponse response) {
		response.addHeader("Content-Type", "application/csv");
		response.addHeader("Content-Disposition", "attachment; filename=todos.csv");
		response.setCharacterEncoding("UTF-8");
		try {
			//NOTE: we didn't open this writer, we don't need to close it
			PrintWriter out = response.getWriter();
			Stream<Todo> todoStream = todoRepository.streamAll();
				todoStream
						.map(todo -> String.join(",", "" + todo.getId(), "" + todo.getDateCreated(),
								"" + todo.getDescription(), "" + todo.isCompleted()))
						.forEach(rethrowConsumer(line -> {
							out.write(line);
							out.write("\n");
							out.flush();
						}));
		} catch (IOException e) {
			log.info("Exception occurred " + e.getMessage(), e);
			throw new RuntimeException("Exception occurred while exporting results", e);
		}
	}

	@RequestMapping(value = "/todos/generate", method = RequestMethod.GET)
	public String generateRandomTodos() {
		String[] how = {"Casually", "Quickly", "Briefly", "It's important to", "Reluctantly", "Remember to",
				"Unfortunately it's necessary to", "Eagerly", "Happily", "Sadly", "It's necessary", "Oh why must I"};
		String[] what = {"go shopping", "buy groceries", "clean the house", "take vacation", "walk pets",
				"vacuum", "finish project", "go party", "pay rent", "go exercise", "have a lunch", "write blog", "study"};
		String[] with = {"my friends", "John", "Mary", "my dad", "my mom", "my brother", "my sister", "my dog",
				"my neighbors", "my coworkers", "some random people", "feeling of great joy"};

		for (int i = 1; i <= 10_000; i++) {
			String task = String.join(" ", how[(int) (Math.random()*how.length)],
					what[(int) (Math.random()*what.length)], "with",
					with[(int) (Math.random()*with.length)]);
			Todo todo = new Todo(task);
			todo.setDateCreated(LocalDateTime.now().plusMinutes(i));
			todoRepository.save(todo);
			if (i % 1000 == 0) log.info("Created " + i + " todos so far.");
		}
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
