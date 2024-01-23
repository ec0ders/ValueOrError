package valueorerror;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class LazyValue<V, E> implements ValueOrError<V, E> {
  private final Supplier<V> supplier;
  private V value;

  LazyValue(Supplier<V> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  private V rawValue() {
    return supplier.get();
  }

  @Override
  public ValueOrError<V, E> filterValue(Predicate<V> valuePredicate,
                                        Function<V, E> errorMapper) {
    return new LazyProxy<>(() -> valuePredicate.test(value()) ? new LazyValue<>(
      this::value) : new LazyError<>(() -> errorMapper.apply(value())));
  }

  @Override
  public <V2> ValueOrError<V2, E> mapValue(Function<V, V2> mapper) {
    return new LazyValue<>(() -> mapper.apply(rawValue()));
  }

  @Override
  public <E2> ValueOrError<V, E2> mapError(Function<E, E2> mapper) {
    return new LazyValue<>(supplier);
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> mapBoth(Function<V, V2> valueMapper,
                                               Function<E, E2> errorMapper) {
    return new LazyValue<>(() -> valueMapper.apply(rawValue()));
  }

  @Override
  public <V2> ValueOrError<V2, E> flatMapValue(Function<V, ValueOrError<V2,
    E>> mapper) {
    return new LazyProxy<>(() -> mapper.apply(rawValue()));
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> flatMapBoth(Function<V,
    ValueOrError<V2, E2>> valueMapper, Function<E, E2> errorMapper) {
    return new LazyProxy<>(() -> valueMapper.apply(rawValue()));
  }

  @Override
  public V value() {
    value = value == null ? rawValue() : value;
    return value;
  }

  @Override
  public E error() {
    return null;
  }

  @Override
  public boolean isValue() {
    return true;
  }

  @Override
  public void ifError(Consumer<E> consumer) {
    // Do nothing
  }

  @Override
  public void ifValueOrError(Consumer<V> valueConsumer,
                             Consumer<E> errorConsumer) {
    ifValue(valueConsumer);
  }
}
