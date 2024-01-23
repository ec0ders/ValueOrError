package valueorerror;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class BuilderWithError<B, E> {

  public static <B, E> BuilderWithError<B, E> of(B builderInitialState,
                                                 Class<E> errorType) {
    return of(builderInitialState);
  }

  public static <B, E> BuilderWithError<B, E> of(B builderInitialState) {
    Objects.requireNonNull(builderInitialState);
    return new BuilderWithError<>(ValueOrError.of(builderInitialState));
  }
  private final ValueOrError<B, E> builderState;

  private BuilderWithError(ValueOrError<B, E> builderState) {
    this.builderState = Objects.requireNonNull(builderState);
  }

  public <B2, V> BuilderWithError<B2, E> advanceValueWithStep(BiFunction<B, ?
    super V, B2> step, V value) {
    return new BuilderWithError<>(builderState.mapValue(b -> step.apply(
      b,
      value)));
  }

  public <B2, V> BuilderWithError<B2, E> advanceValueOrErrorWithStep(BiFunction<B, ?
    super V, B2> step, ValueOrError<? extends V, E> valueOrError) {
    return new BuilderWithError<>(builderState.flatMapValue(b -> valueOrError.mapValue(
      v -> step.apply(b, v))));
  }

  public <B2, V> BuilderWithError<B2, E> advanceValueWithStepOrError(BiFunction<B, ?
    super V, ValueOrError<B2, E>> step, V value) {
    return new BuilderWithError<>(builderState.flatMapValue(b -> step.apply(
      b,
      value)));
  }

  public <B2, V> BuilderWithError<B2, E> advanceValueOrErrorWithStepOrError(BiFunction<B, ? super V, ValueOrError<B2, E>> step, ValueOrError<? extends V, E> valueOrError) {
    return new BuilderWithError<>(builderState.flatMapValue(b -> valueOrError.flatMapValue(
      v -> step.apply(b, v))));
  }

  public <V> ValueOrError<V, E> build(Function<B, V> finalStep) {
    return builderState.mapValue(finalStep);
  }
}
