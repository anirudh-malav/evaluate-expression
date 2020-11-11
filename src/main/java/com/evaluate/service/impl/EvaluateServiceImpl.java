package com.evaluate.service.impl;

import com.evaluate.common.constants.Constants;
import com.evaluate.common.exceptions.InvalidDataException;
import com.evaluate.dto.request.EvaluateRequestDto;
import com.evaluate.dto.response.EvaluateResponseDto;
import com.evaluate.dto.response.UserResponseDTO;
import com.evaluate.model.Expressions;
import com.evaluate.repository.ExpressionsRepository;
import com.evaluate.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    ExpressionsRepository expressionsRepository;

    @Override
    public EvaluateResponseDto getEvaluation(EvaluateRequestDto evaluateRequestDto) {
        String exp = evaluateRequestDto.getExpression();
        char[] tokens = exp.toCharArray();
        int n = tokens.length;
        int[] operatorCount = new int[4];

        Stack<Long> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < n; i++) {
            if (tokens[i] == ' ')
                continue;

            // If current token is a number, push it into the stack for numbers
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                ////validate next character
                if(i<n-1 && tokens[i+1] == '(') throw new InvalidDataException("Expression is invalid.");
                StringBuffer sb = new StringBuffer();

                // get all the digits and push the number to values stack
                while (i < n && tokens[i] >= '0' && tokens[i] <= '9')
                    sb.append(tokens[i++]);
                values.push(Long.parseLong(sb.toString()));

                i--;
            }

            // check for negative numbers
            if(i<n-2 && tokens[i]=='(' && (tokens[i+1] == '-' || tokens[i+1] == '+')){


                // for cases such as -- (-(+(-6))*(+8))
                if(tokens[i+2]=='(')  {
                    operators.push('(');
                    updateOperatorCount(tokens[i+1], operatorCount);
                    values.push(tokens[i+1] == '-' ? -1L : 1L);
                    operators.push('*');
                    i++;
                    continue;
                }
                else if(tokens[i+2] >= '0' && tokens[i+2] <= '9') {
                    operators.push('(');
                    updateOperatorCount(tokens[i + 1], operatorCount);
                    StringBuffer sb = new StringBuffer(tokens[i+1] == '-' ? "-" : "");
                    i+=2;
                    while (i < n && tokens[i] >= '0' && tokens[i] <= '9')
                        sb.append(tokens[i++]);
                    values.push(Long.parseLong(sb.toString()));
                    i--;
                }
                else throw new InvalidDataException("Expression is invalid.");
            }

            // If Current token is an opening brace, push it to operators stack
            else if (tokens[i] == '('){
                //validate next character
                if(i>n-3 || tokens[i+1]=='*' || tokens[i+1]=='/') throw new InvalidDataException("Expression is invalid.");

                operators.push(tokens[i]);
            }

            // if current token is closing braces, solve this expression
            else if (tokens[i] == ')') {
                if(i!=n-1 && !(isOperator(tokens[i+1]) || tokens[i+1]==')')) throw new InvalidDataException("Expression is invalid.");
                while (!operators.isEmpty() && operators.peek() != '(')
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                if(!operators.isEmpty()) operators.pop();
            }

            // Current token is an operator.
            else if (isOperator(tokens[i])) {
                //validate next character
                if(i<n-1 && ( isOperator(tokens[i+1]) || tokens[i+1]==')' )){
                    throw new InvalidDataException("Expression is invalid.");
                }
                // update operate count to the operatorCount array
                updateOperatorCount(tokens[i], operatorCount);
                // when starting with - number. ex --> "-6+5"
                if((tokens[i] == '-' || tokens[i] == '+') && values.isEmpty()){
                    values.push(tokens[i] == '-' ? -1L : 1L);
                    operators.push('*');
                    continue;
                }
                // solve the expression if current operator has less then or equal precedence then top of the operator stack value.
                while (!operators.empty() && hasPrecedence(tokens[i], operators.peek()))
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                // Push current token to 'operators' stack.
                operators.push(tokens[i]);
            }
        }

        //apply remaining operators to remaining values
        while (!operators.empty())
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

        Long result = values.pop();

        //get the count of expressions from DB for current user and add the new operators count
        // from the current expression string
        Expressions expression = expressionsRepository.findByUserEmail(getLoggedInUserId());
        if(expression == null){
            expression = new Expressions();
            expression.setUserEmail(getLoggedInUserId());
        }
        expression.setAddCount(expression.getAddCount()+operatorCount[0]);
        expression.setSubtractCount(expression.getSubtractCount()+operatorCount[1]);
        expression.setMultiplyCount(expression.getMultiplyCount()+operatorCount[2]);
        expression.setDivideCount(expression.getDivideCount()+operatorCount[3]);

        //commit the changes to DB
        expressionsRepository.save(expression);

    return new EvaluateResponseDto(result);
}

    @Override
    public UserResponseDTO getUserInfo(String userEmail) {
        Expressions expression = expressionsRepository.findByUserEmail(userEmail);
        int addCount = expression.getAddCount();
        int SubtractCount = expression.getSubtractCount();
        int multiplyCount = expression.getMultiplyCount();
        int divideCount = expression.getDivideCount();

        int maxCount = findMaxCount(addCount, SubtractCount, multiplyCount, divideCount);

        Map<Character,Integer> countMap = new HashMap<>();
        countMap.put('+', addCount);
        countMap.put('-', SubtractCount);
        countMap.put('*', multiplyCount);
        countMap.put('/', divideCount);

        //check if more then one operators has same max count
        List<Character> maxChars = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
            if (entry.getValue().equals(maxCount)){
                maxChars.add(entry.getKey());
            }
        }
        UserResponseDTO userResponseDTO = buildUserResponseDto(userEmail,countMap, maxChars);
        return userResponseDTO;
    }

    private static void updateOperatorCount(char c, int[] operatorCount){
        switch(c){
            case '+':
                operatorCount[0]+=1;
                break;
            case '-':
                operatorCount[1]+=1;
                break;
            case '*':
                operatorCount[2]+=1;
                break;
            case '/':
                operatorCount[3]+=1;
                break;
        }
    }

    // get the logged in user
    private static String getLoggedInUserId(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        return request.getHeader(Constants.Header.USER_EMAIL);
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') &&
                (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    private static Long applyOperation(char op, Long b, Long a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new InvalidDataException("Cannot divide by zero");
                return a / b;
        }
        return 0L;
    }

    private int findMaxCount(int addCount, int subtractCount, int multiplyCount, int divideCount){
        return Math.max(Math.max(Math.max(addCount,subtractCount),multiplyCount),divideCount);
    }

    private UserResponseDTO buildUserResponseDto(String userEmail,Map<Character,Integer> countMap,
                                                 List<Character> maxChars){
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserEmail(userEmail);
        StringBuilder message = new StringBuilder();
        if(maxChars.size() == 1){
            message.append("Operator most used by the user is '"+maxChars.get(0)+"' " +
                    "and it is used "+countMap.get(maxChars.get(0))+" times");
        }
        else{
            message.append("Most used operators by this user are "+Arrays.toString(maxChars.toArray())+
                    " and these operators are used "+countMap.get(maxChars.get(0))+" times");
        }
        userResponseDTO.setMessage(message.toString());
        return userResponseDTO;
    }

    // method to check if given character is an operator
    private boolean isOperator(char c){
        if(c == '+' || c == '-' ||  c == '*' || c == '/') return true;
        return false;
    }
}

