package com.springfilter.token;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.token.IToken;
import com.springfilter.compiler.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class SpaceMatcher extends Matcher<IToken> {

  @Override
  public IToken match(StringBuilder input) {

    // just skip unnecessary spaces

    while (input.indexIs(' ') || input.indexIs('\t')) {
      input.take();
    }

    return null;

  }

}