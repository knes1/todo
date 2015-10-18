package io.github.knes1.todo.util;

import io.github.knes1.todo.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Random TODO generator. Used to create insert queries to fill in test database.
 *
 * @author knesek
 * Created on: 11/10/15
 */
public class TodoGenerator {

	private static String[] how = {"Casually", "Quickly", "Briefly", "It's important to", "Reluctantly", "Remember to",
			"Unfortunately it's necessary to", "Eagerly", "Happily", "Sadly, I will", "It's necessary", "Oh why must I"};
	private static String[] what = {"go shopping", "buy groceries", "clean the house", "take vacation", "walk pets",
			"vacuum", "finish project", "go party", "pay rent", "go exercise", "have a lunch", "write blog", "study"};
	private static String[] with = {"my friends", "John", "Mary", "my dad", "my mom", "my brother", "my sister", "my dog",
			"my neighbors", "my coworkers", "some random people", "feeling of great joy"};

	private static final Logger log = LoggerFactory.getLogger(RequestStatisticsInterceptor.class);

	private static final SimpleDateFormat fmt = new SimpleDateFormat("YYYY-MM-dd HH:mm");

	private TodoGenerator() {}

	public static Todo randomTodo() {
		String task = String.join(" ",
				how[(int) (Math.random() * how.length)],
				what[(int) (Math.random() * what.length)], "with",
				with[(int) (Math.random() * with.length)]);

		Todo todo = new Todo(task);
		todo.setDateCreated(Date.from(Instant.now().plusSeconds((int) (Math.random() * 3600 * 24 * 365 * 2))));
		todo.setCompleted(false);
		return todo;
	}

	public static void createRandomTodoInserts(OutputStream out, int amount) {
		final int singleStatementLimit = 1000;
		final String insert = "INSERT INTO todo (completed, date_created, description) VALUES ";
		OutputStreamWriter w = new OutputStreamWriter(out);
		try {
			w.write(insert);
			for (int i = 1; i <= amount; i++){
				Todo todo = randomTodo();
				w.write(String.format("(0,'%s','%s')", fmt.format(todo.getDateCreated()),
						todo.getDescription().replace("'", "\\'")));
				if (i != amount) {
					if (i % singleStatementLimit == 0) {
						w.write(";\n\n");
						w.write(insert);
					} else {
						w.write(",\n");
					}
				} else {
					w.write(";");
				}
			}
			w.flush();
			out.flush();
			w.close();
		} catch (IOException e) {
			log.debug("Exception happened while creating data script.", e);
		}
	}

	/*
	public static void main(String[] args) {
		try(FileOutputStream fout = new FileOutputStream("/tmp/script.sql")) {
			createRandomTodoInserts(fout, 1_000_000);
		} catch (IOException e) {
			log.debug("Exception occurred " + e.getMessage(), e);
		}

	}*/
}
