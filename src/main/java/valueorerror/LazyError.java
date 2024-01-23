package valueorerror;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class LazyError<V, E> implements ValueOrError<V, E> {
  private final Supplier<E> supplier;
  private E error;

  LazyError(Supplier<E> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  @Override
  public ValueOrError<V, E> filterValue(Predicate<V> valuePredicate,
                                        Function<V, E> errorMapper) {
    return this;
  }

  @Override
  public <V2> ValueOrError<V2, E> mapValue(Function<V, V2> mapper) {
    return new LazyError<>(supplier);
  }

  @Override
  public <E2> ValueOrError<V, E2> mapError(Function<E, E2> mapper) {
    return new LazyError<>(() -> mapper.apply(supplier.get()));
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> mapBoth(Function<V, V2> valueMapper,
                                               Function<E, E2> errorMapper) {
    return new LazyError<>(() -> errorMapper.apply(supplier.get()));
  }

  @Override
  public <V2> ValueOrError<V2, E> flatMapValue(Function<V, ValueOrError<V2,
    E>> mapper) {
    return new LazyError<>(supplier);
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> flatMapBoth(Function<V,
    ValueOrError<V2, E2>> valueMapper, Function<E, E2> errorMapper) {
    return new LazyError<>(() -> errorMapper.apply(supplier.get()));
  }

  @Override
  public V value() {
    return null;
  }

  @Override
  public E error() {
    error = error == null ? supplier.get() : error;
    return error;
  }

  @Override
  public boolean isValue() {
    return false;
  }

  @Override
  public boolean isError() {
    return true;
  }

  @Override
  public void ifValue(Consumer<V> consumer) {
    // Do nothing
  }

  @Override
  public void ifValueOrError(Consumer<V> valueConsumer, Consumer<E> errorConsumer) {
    ifError(errorConsumer);
  }
}
