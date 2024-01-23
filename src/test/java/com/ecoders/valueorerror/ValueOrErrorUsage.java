package com.ecoders.valueorerror;

import valueorerror.ValueOrError;

public final class ValueOrErrorUsage {

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

  public static final class ParserExample {
    public sealed interface Error {
      record FormatError(String value) implements Error {}

      record UnknownValueError(String value) implements Error {}
    }

    public static void main(String[] args) {

    }
  }
}
