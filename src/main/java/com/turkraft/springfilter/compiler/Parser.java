package com.turkraft.springfilter.compiler;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.node.FieldMatcher;
import com.turkraft.springfilter.compiler.node.Filter;
import com.turkraft.springfilter.compiler.node.FilterMatcher;
import com.turkraft.springfilter.compiler.node.FunctionMatcher;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.InputMatcher;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.node.predicate.ConditionMatcher;
import com.turkraft.springfilter.compiler.node.predicate.OperationMatcher;
import com.turkraft.springfilter.compiler.node.predicate.PriorityMatcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.exception.ParserException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class Parser {

  public static Matcher<?>[] matchers = new Matcher<?>[] {

      PriorityMatcher.INSTANCE, ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE,
      FunctionMatcher.INSTANCE, FieldMatcher.INSTANCE, InputMatcher.INSTANCE,

  };

  private Parser() {}

  public static Filter parse(String input) {
    return parse(Tokenizer.tokenize(input));
  }

  public static Filter parse(LinkedList<IToken> tokens) throws ParserException {
    return FilterMatcher.INSTANCE.match(tokens, new LinkedList<>()).transform(null);
  }

  public static IExpression walk(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    LinkedList<IToken> tokenBackup = tokens.copy();
    LinkedList<IExpression> nodeBackup = nodes.copy();

    while (true) {

      int count = tokens.size();

      for (Matcher<?> matcher : matchers) {

        LinkedList<IToken> innerTokenBackup = tokens.copy();
        LinkedList<IExpression> innerNodeBackup = nodes.copy();

        IExpression node = matcher.match(tokens, nodes);

        if (node != null) {
          return node;
        }

        else {
          tokens.replaceWith(innerTokenBackup);
          nodes.replaceWith(innerNodeBackup);
        }

      }

      if (tokens.size() == count) {
        break;
      }

    }

    tokens.replaceWith(tokenBackup);
    nodes.replaceWith(nodeBackup);

    return null;

  }

  public static IExpression run(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    while (tokens.size() > 0) {

      IExpression node = walk(tokens, nodes);

      if (node == null) {
        break;
      }

      nodes.add(node);

    }

    return nodes.pollLast();

  }

}
