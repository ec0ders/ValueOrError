package com.ecoders.valueorerror;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class LazyProxy<V, E> implements ValueOrError<V, E> {
  private final Supplier<ValueOrError<V, E>> supplier;
  private ValueOrError<V, E> valueOrError;

  LazyProxy(Supplier<ValueOrError<V, E>> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  private ValueOrError<V, E> rawValueOrError() {
    return supplier.get();
  }

  private ValueOrError<V, E> valueOrError() {
    valueOrError = valueOrError == null ? rawValueOrError() : valueOrError;
    return valueOrError;
  }

  @Override
  public ValueOrError<V, E> filterValue(Predicate<V> valuePredicate,
                                        Function<V, E> errorMapper) {
    return new LazyProxy<>(() -> rawValueOrError().filterValue(
      valuePredicate,
      errorMapper));
  }

  @Override
  public <V2> ValueOrError<V2, E> mapValue(Function<V, V2> mapper) {
    return new LazyProxy<>(() -> rawValueOrError().mapValue(mapper));
  }

  @Override
  public <E2> ValueOrError<V, E2> mapError(Function<E, E2> mapper) {
    return new LazyProxy<>(() -> rawValueOrError().mapError(mapper));
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> mapBoth(Function<V, V2> valueMapper,
                                               Function<E, E2> errorMapper) {
    return new LazyProxy<>(() -> rawValueOrError().mapBoth(
      valueMapper,
      errorMapper));
  }

  @Override
  public <V2> ValueOrError<V2, E> flatMapValue(Function<V, ValueOrError<V2,
    E>> mapper) {
    return new LazyProxy<>(() -> rawValueOrError().flatMapValue(mapper));
  }

  @Override
  public <V2, E2> ValueOrError<V2, E2> flatMapBoth(Function<V,
    ValueOrError<V2, E2>> valueMapper, Function<E, E2> errorMapper) {
    return new LazyProxy<>(() -> rawValueOrError().flatMapBoth(
      valueMapper,
      errorMapper));
  }

  @Override
  public V value() {
    return valueOrError().value();
  }

  @Override
  public E error() {
    return valueOrError().error();
  }
}
