package com.ecoders.valueorerror;

import java.util.Objects;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import valueorerror.ValueOrError;
import static java.util.function.Function.identity;
import static valueorerror.ValueOrError.errorSupplier;
import static valueorerror.ValueOrError.of;
import static valueorerror.ValueOrError.ofSupplier;

public class ValueOrErrorTest {

  @Test
  public void failsIfNotLazy() {
    var lazyValue = ofSupplier(Assertions::fail);
    var lazyError = errorSupplier(Assertions::fail);

    var filteredValue = ofSupplier(Assertions::fail).filterValue(Objects::nonNull, Assertions::fail);

    var mappedValue = ofSupplier(Assertions::fail).mapValue(identity());
    var mappedError = ofSupplier(Assertions::fail).mapError(identity());
    var mappedBoth = ofSupplier(Assertions::fail).mapBoth(identity(), identity());

    var flatMappedValue = ofSupplier(Assertions::fail).flatMapValue(ValueOrError::of);
    var flatMappedBoth = ofSupplier(Assertions::fail).flatMapBoth(ValueOrError::of, identity());
  }

}
