package valueorerror;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ValueOrError<V, E> {

  static <V, E> ValueOrError<V, E> of(V value, Class<E> errorType) {
    return of(value);
  }

  static <V, E> ValueOrError<V, E> ofSupplier(Supplier<V> supplier,
                                              Class<E> errorType) {
    return ofSupplier(supplier);
  }

  static <V, E> ValueOrError<V, E> of(V value) {
    return new LazyValue<>(() -> value);
  }

  static <V, E> ValueOrError<V, E> ofSupplier(Supplier<V> supplier) {
    return new LazyValue<>(supplier);
  }

  static <V, E> ValueOrError<V, E> error(E error, Class<V> valueType) {
    return error(error);
  }

  static <V, E> ValueOrError<V, E> errorSupplier(Supplier<E> supplier,
                                                 Class<V> valueType) {
    return errorSupplier(supplier);
  }

  static <V, E> ValueOrError<V, E> error(E error) {
    return new LazyError<>(() -> error);
  }

  static <V, E> ValueOrError<V, E> errorSupplier(Supplier<E> supplier) {
    return new LazyError<>(supplier);
  }

  static <V, E> ValueOrError<V, E> ofNullable(V valueOrNull,
                                              Supplier<E> errorSupplier) {
    return valueOrNull != null ? of(valueOrNull) :
      new LazyError<>(errorSupplier);
  }

  static <V, E> ValueOrError<V, E> ofNullableSupplier(Supplier<V> valueOrNullSupplier, Supplier<E> errorSupplier) {
    return ValueOrError.<V, E>ofSupplier(valueOrNullSupplier)
      .filterValue(Objects::nonNull, v -> errorSupplier.get());
  }

  ValueOrError<V, E> filterValue(Predicate<V> valuePredicate,
                                 Function<V, E> errorMapper);

  <V2> ValueOrError<V2, E> mapValue(Function<V, V2> mapper);

  <E2> ValueOrError<V, E2> mapError(Function<E, E2> mapper);

  <V2, E2> ValueOrError<V2, E2> mapBoth(Function<V, V2> valueMapper,
                                        Function<E, E2> errorMapper);

  <V2> ValueOrError<V2, E> flatMapValue(Function<V, ValueOrError<V2, E>> mapper);

  <V2, E2> ValueOrError<V2, E2> flatMapBoth(Function<V, ValueOrError<V2, E2>> valueMapper, Function<E, E2> errorMapper);

  V value();

  E error();

  default boolean isValue() {
    return value() != null;
  }

  default boolean isError() {
    return error() != null;
  }

  default void ifValue(Consumer<V> consumer) {
    consumer.accept(value());
  }

  default void ifError(Consumer<E> consumer) {
    consumer.accept(error());
  }

  default void ifValueOrError(Consumer<V> valueConsumer,
                              Consumer<E> errorConsumer) {
    if (isValue()) {
      ifValue(valueConsumer);
    } else {
      ifError(errorConsumer);
    }
  }

}
