package com.springfilter.token;

import com.springfilter.compiler.token.IToken;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Word implements IToken {

  private String value;

}