package com.ll; // cim.ll 패키지 안에 있음

public class Calc { // Cals 클래스

  public static boolean recursionDebug = true; // 내가 디버그 모드를 켜겠다 할때는 true로 변경

  public static int runCallCount = 0;

  public static int run(String exp) { // run 메서드 만듦.
    runCallCount++;
    exp = exp.trim(); // 빈 여백을 인지하는 trim 사용
    exp = stripOuterBracket(exp); //

    // 음수괄호 패턴이면, 기존에 갖고있던 해석 패턴과는 맞지 않으니 패턴을 변경
    int[] pos = null;
    while ((pos = findNegativeCaseBracket(exp)) != null) {
      exp = changeNegativeBracket(exp, pos[0], pos[1]);

    }
    exp = stripOuterBracket(exp);
    if (recursionDebug) {
      System.out.printf("exp(%d) : %s\n", runCallCount, exp);
    }

    // 연산기호가 없으면 바로 리턴
    if (!exp.contains(" ")) return Integer.parseInt(exp); // 여백이 없으면 parseInt로 리턴
    boolean needToMultiply = exp.contains(" * "); // 곱 기호가 있으면 참이다.
    boolean needToPlus = exp.contains(" + ") || exp.contains(" - "); // 덧셈 기호 또는 뺄셈 기호가 있으면 참이다
    boolean needToCompound = needToMultiply && needToPlus; // 곱 기호와 덧셈기호 모두있으면 참이다
    boolean needToSplit = exp.contains("(") || exp.contains(")"); // 괄호가 있으면 참이다

    if (needToSplit) {  // 괄호가 있어서 참이되면 실행된다.
      int splitPointIndex = findSplitPointIndex(exp); // int타입 변수에 findSplitPointIndex(exp)값을 넣겠다.

      String firstExp = exp.substring(0, splitPointIndex); // String타입에 firstExp변수를 만들었다
      String secondExp = exp.substring(splitPointIndex + 1); // ''


      char operator = exp.charAt(splitPointIndex); // exp가 가리키고 있는 문자열의 splitPointIndex번째 순서를 나타낸다

      exp = Calc.run(firstExp) + " " + operator + " " + Calc.run(secondExp); // exp는 firstExp값과 여백과 operator와 여백 두번째Exp값을 넣겠다

      return Calc.run(exp); // if문이 끝나면 run에다가 exp값을 리턴하겠다
    } else if (needToCompound) { // 만약 Compound가 참이라면 밑에 else if문 실행
      String[] bits = exp.split(" \\+ "); // string타입의 배열을 만들고 문자열을 배열로 자르는 split를 활용해 덧셈기호를 기준으로 잘랐다.

      return Integer.parseInt(bits[0]) + Calc.run(bits[1]); // 자르고 나면 0번배열과 1번배열을 더한값을 리턴한다
    }
    if (needToPlus) { // 덧셈기호 또는 뺄셈기호가 있어 Plus가 참이되면 if문이 실행된다.
      exp = exp.replaceAll("\\- ", "\\+ \\-"); // exp의 뺄셈기호를  +와 -로 치환한다
      String[] bits = exp.split(" \\+ "); // 배열을 +기호 기준으로 자르겠다.
      int sum = 0; // sum에 0이라는 값을 넣었다
      for (int i = 0; i < bits.length; i++) { // for문 실행
        sum += Integer.parseInt(bits[i]);
      }
      return sum; // for문이 끝나면 나온값을 sum으로 리턴
    } else if (needToMultiply) { // 만약 곱기호가 있어서 참이라면 밑에 else if문 실행
      String[] bits = exp.split(" \\* "); // 배열을 곱 기호 기준으로 자르겠다.
      int rs = 1; // rs에 1이라는 값을 넣겠다
      for (int i = 0; i < bits.length; i++) { // 반복문 실행
        rs *= Integer.parseInt(bits[i]);
      }
      return rs; // 반복문에서 나온값을 rs로 리턴
    }
    throw new RuntimeException("처리할 수 있는 계산식이 아닙니다"); // 위에 식 아무것도 포함되지않으면 콘솔에 ()값이뜬다.
  }

  private static String changeNegativeBracket(String exp, int startPos, int endPos) {
    String head = exp.substring(0, startPos);
    String body = "(" + exp.substring(startPos + 1, endPos + 1) + " * -1)";
    String tail = exp.substring(endPos + 1);

    exp = head + body + tail;

    return exp;
  }
  private static int[] findNegativeCaseBracket(String exp) {
    for (int i = 0; i < exp.length() - 1; i++) {
      if (exp.charAt(i) == '-' && exp.charAt(i + 1) == '(') {
        // 어? 마이너스 괄호 찾았다
        int bracketCount = 1;

        for (int j = i + 2; j < exp.length(); j++) {
          char c = exp.charAt(j);

          if (c == '(') {
            bracketCount++;
          } else if (c == ')') {
            bracketCount--;
          }

          if (bracketCount == 0) {
            return new int[]{i, j};
          }
        }
      }
    }
    return null;
  }

    private static int findSplitPointIndexBy (String exp,char findChar){
      int bracketCount = 0;

      for (int i = 0; i < exp.length(); i++) {
        char c = exp.charAt(i);

        if (c == '(') {
          bracketCount++;
        } else if (c == ')') {
          bracketCount--;
        } else if (c == findChar) {
          if (bracketCount == 0) return i;
        }
      }
      return -1;
    }

    private static int findSplitPointIndex (String exp){
      int index = findSplitPointIndexBy(exp, '+');

      if (index >= 0) return index;

      return findSplitPointIndexBy(exp, '*');
    }

    private static String stripOuterBracket (String exp){
      int outerBracketCount = 0;

      while (exp.charAt(outerBracketCount) == '(' && exp.charAt(exp.length() - 1 - outerBracketCount) == ')') {
        outerBracketCount++;
      }
      if (outerBracketCount == 0) return exp;
      return exp.substring(outerBracketCount, exp.length() - outerBracketCount);
    }
  }