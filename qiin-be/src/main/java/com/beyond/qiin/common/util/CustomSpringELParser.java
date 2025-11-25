package com.beyond.qiin.common.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

  //생성 제공 x -> util이므로 static 메서드를 통해 활용
  private CustomSpringELParser() {
  }

  //메서드 파라미터 이름, 실제 전달 값, 키
  public static Object getDynamicValue(String[] parameterNames, Object[] args, String key){
    ExpressionParser parser = new SpelExpressionParser(); //string -> SpEL
    StandardEvaluationContext context = new StandardEvaluationContext();

    for(int i = 0; i < parameterNames.length; i++){
      context.setVariable(parameterNames[i], args[i]); //변수들에 값 대입
    }
    return parser.parseExpression(key).getValue(context, Object.class);
  }
}
