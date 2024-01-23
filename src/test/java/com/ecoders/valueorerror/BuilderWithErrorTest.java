package com.ecoders.valueorerror;

import java.util.Objects;
import static java.util.function.Predicate.not;

public final class BuilderWithErrorTest {

  static final class Message {
    static final class Phases {
      interface Final {
        Message build();
      }

      interface A {
        B header(String header);
      }

      interface B {
        C content(String content);
      }

      interface C {
        Final footer(String footer);
      }
    }

    static final class Builder implements Phases.Final, Phases.A, Phases.B,
      Phases.C {
      public static Builder empty() {
        return new Builder();
      }

      private String header;
      private String content;
      private String footer;

      private Builder() {}

      @Override
      public Message build() {
        return new Message(header, content, footer);
      }

      @Override
      public Builder header(String header) {
        this.header = Objects.requireNonNull(header);
        return this;
      }

      @Override
      public Builder content(String content) {
        this.content = Objects.requireNonNull(content);
        if (content.isBlank()) {
          throw new IllegalArgumentException();
        }
        return this;
      }

      @Override
      public Builder footer(String footer) {
        this.footer = Objects.requireNonNull(footer);
        return this;
      }
    }

    public static Phases.A with() {
      return Builder.empty();
    }

    private final String header;
    private final String content;
    private final String footer;

    private Message(String header, String content, String footer) {
      this.header = header;
      this.content = content;
      this.footer = footer;
    }

    @Override
    public String toString() {
      return "Message{header='%s', content='%s', footer='%s'}".formatted(header,
        content,
        footer);
    }
  }

  public static final class SimpleExample {
    public static void main(String[] args) {
      var emptyPhaseBuilder = BuilderWithError.of(Message.with(),
        MessageBuilderError.class);
      var emptyBuilder = BuilderWithError.of(Message.Builder.empty(),
        MessageBuilderError.class);

      var header = ValueOrError.of("header", MessageBuilderError.class);
      var content = ValueOrError.of("content", MessageBuilderError.class);
      var footer = ValueOrError.of("footer", MessageBuilderError.class);

      var emptyHeader = ValueOrError.of("", MessageBuilderError.class)
        .filterValue(not(String::isEmpty), MessageBuilderError.EmptyHeaderError::new);
      var blankContent = ValueOrError.of(" \t", MessageBuilderError.class)
        .filterValue(not(String::isBlank), MessageBuilderError.BlankContentError::new);

      emptyPhaseBuilder.advanceValueOrErrorWithStep(
          Message.Phases.A::header,
          header)
        .advanceValueOrErrorWithStep(Message.Phases.B::content, content)
        .advanceValueOrErrorWithStep(Message.Phases.C::footer, footer)
        .build(Message.Phases.Final::build)
        .ifValueOrError(System.out::println, System.out::println);

      emptyBuilder.advanceValueOrErrorWithStep(Message.Builder::footer, footer)
        .advanceValueOrErrorWithStep(Message.Builder::header, header)
        .advanceValueOrErrorWithStep(Message.Builder::content, content)
        .build(Message.Builder::build)
        .ifValueOrError(System.out::println, System.out::println);

      emptyBuilder.advanceValueOrErrorWithStep(Message.Builder::footer, footer)
        .advanceValueOrErrorWithStep(Message.Builder::header, emptyHeader)
        .advanceValueOrErrorWithStep(Message.Builder::content, content)
        .build(Message.Builder::build)
        .ifValueOrError(System.out::println, System.out::println);

      emptyBuilder.advanceValueOrErrorWithStep(Message.Builder::footer, footer)
        .advanceValueOrErrorWithStep(Message.Builder::header, header)
        .advanceValueOrErrorWithStep(Message.Builder::content, blankContent)
        .build(Message.Builder::build)
        .ifValueOrError(System.out::println, System.out::println);
    }
  }

  public sealed interface MessageBuilderError {
    record BlankContentError(String content) implements MessageBuilderError {}

    record EmptyHeaderError(String header) implements MessageBuilderError {}

  }

}
