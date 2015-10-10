package io.github.knes1.todo.util;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class to avoid try/catch boiler plate by PaoloC (http://stackoverflow.com/users/2365724/paoloc).
 *
 * Taken from here:
 * http://stackoverflow.com/questions/27644361/how-can-i-throw-checked-exceptions-from-inside-java-8-streams/30974991#30974991
 *
 * Created on: 10/10/15
 */
public final class LambdaExceptionUtil {

	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}

	@FunctionalInterface
	public interface Function_WithExceptions<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	/**
	 * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
	 */
	public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) throws E {
		return t -> {
			try {
				consumer.accept(t);
			} catch (Exception exception) {
				throwActualException(exception);
			}
		};
	}

	/**
	 * .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName))
	 */
	public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function) throws E  {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception exception) {
				throwActualException(exception);
				return null;
			}
		};
	}

	@SuppressWarnings("unchecked")
	private static <E extends Exception> void throwActualException(Exception exception) throws E {
		throw (E) exception;
	}

}
