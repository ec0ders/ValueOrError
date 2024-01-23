package com.ecoders.valueorerror;

import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import valueorerror.ValueOrError;
import static java.util.function.Function.identity;
import static valueorerror.ValueOrError.errorSupplier;
import static valueorerror.ValueOrError.ofSupplier;

public class ValueOrErrorTest {

  public static final class SimpleExample {
    public static void main(String[] args) {
      var value = ValueOrError.of("value");
      var error = ValueOrError.error("error");
      var errorIfNull = ValueOrError.ofNullable(null, () -> "error");

      System.out.println(value.value()); // value
      System.out.println(value.error()); // null

      System.out.println(error.value()); // null
      System.out.println(error.error()); // error

      System.out.println(errorIfNull.value()); // null
      System.out.println(errorIfNull.error()); // "error"

      ValueOrError.ofNullableSupplier(() -> null, () -> "supplier gave null")
        .ifValueOrError(System.out::println, System.out::println);
    }
  }

  @Test
  public void failsIfNotLazy() {
    var lazyValue = ofSupplier(Assertions::fail);
    var lazyError = errorSupplier(Assertions::fail);

    var filteredValue = ofSupplier(Assertions::fail).filterValue(
      Objects::nonNull,
      v -> Assertions.fail());

    var mappedValue = ofSupplier(Assertions::fail).mapValue(identity());
    var mappedError = ofSupplier(Assertions::fail).mapError(identity());
    var mappedBoth = ofSupplier(Assertions::fail).mapBoth(
      identity(),
      identity());

    var flatMappedValue =
      ofSupplier(Assertions::fail).flatMapValue(ValueOrError::of);
    var flatMappedBoth = ofSupplier(Assertions::fail).flatMapBoth(
      ValueOrError::of,
      identity());
  }

}
