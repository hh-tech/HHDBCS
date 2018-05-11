package com.hhdb.csadmin.plugin.query.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTokenizer {
	//语法属性
	    private  List<Token> stringTokens;
	    private  List<Token> singleLineCommentTokens;
	    private  List<Token> multiLineCommentTokens;
	    private  Matcher stringMatcher;
	    public  Matcher singleLineCommentMatcher;
	    public  Matcher multiLineCommentMatcher;
	    private  final String QUOTE_REGEX = "'((?>[^']*+)(?>'{2}[^']*+)*+)'|'.*";
	    private  final String MULTILINE_COMMENT_REGEX = "/\\*(?:.|[\\n\\r])*?\\*/|/\\*.*"; 
	    public static final int UNRECOGNIZED = 0, WORD = 1, NUMBER = 2, COMMENT = 3, KEYWORD = 4, KEYWORD2 = 5, LITERAL = 6, STRING = 7, OPERATOR = 8, BRACKET = 9, SINGLE_LINE_COMMENT = 10,
				BRACKET_HIGHLIGHT = 11, BRACKET_HIGHLIGHT_ERR = 12;
	    public static final String SINGLE_LINE_COMMENT_REGEX = "--.*$";
	    {
	    	stringTokens = new ArrayList<Token>();
	        stringMatcher = Pattern.compile(QUOTE_REGEX).matcher("");
	        singleLineCommentTokens = new ArrayList<Token>();
	        singleLineCommentMatcher = Pattern.compile(
	                SINGLE_LINE_COMMENT_REGEX, Pattern.MULTILINE).
	                matcher("");
	        multiLineCommentTokens = new ArrayList<Token>();
	        multiLineCommentMatcher = Pattern.compile(
	                MULTILINE_COMMENT_REGEX, Pattern.DOTALL).
	                matcher("");
	    }    
	   
	    private  boolean withinMultiLineComment(int start, int end) {
	        return contains(multiLineCommentTokens, start, end);
	    }
	    private  boolean withinSingleLineComment(int start, int end) {
	        return contains(singleLineCommentTokens, start, end);
	    }
	    public   void extractSingleLineCommentTokens(String query) {
	        addTokensForMatcherWhenNotInString(singleLineCommentMatcher, query, singleLineCommentTokens);
	    }
	    public   void extractMultiLineCommentTokens(String query) {
	        addTokensForMatcherWhenNotInString(multiLineCommentMatcher, query, multiLineCommentTokens);
	    }
	    public   boolean notInAnyToken(int index) {
	        return !(withinMultiLineComment(index, index)) 
	            && !(withinSingleLineComment(index, index))
	            && !(withinQuotedString(index, index));
	    }
	    private   void addTokensForMatcherWhenNotInString(Matcher matcher, String query, List<Token> tokens) {
	        tokens.clear();
	        matcher.reset(query);
	        while (matcher.find()) {
	            int start = matcher.start();
	            int end = matcher.end();
	            int endOffset = end; 
	            if (isSingleLineMatcher(matcher)) {
	                endOffset = start + 2;
	            }
	            if (!withinQuotedString(start, endOffset)) {
	                tokens.add(new Token(COMMENT, start, end));
	            }
	        }
	    }
	    private   String removeTokensForMatcherWhenNotInString(Matcher matcher, String query) {
	        int start = 0, end = 0, endOffset = 0;
	        StringBuilder sb = new StringBuilder(query);
	        matcher.reset(query);
	        while (matcher.find(start)) {
	            start = matcher.start();
	            end = matcher.end();
	            extractQuotedStringTokens(sb.toString());
	            endOffset = end; 
	            if (isSingleLineMatcher(matcher)) {
	                endOffset = start + 2;
	            }
	            if (!withinQuotedString(start, endOffset)) {
	                sb.delete(start, end);
	                matcher.reset(sb);
	            } else {
	                start = end;
	            }
	        }
	        return sb.toString();
	    }
	    private   boolean isSingleLineMatcher(Matcher matcher) {
	        return (matcher == singleLineCommentMatcher);
	    }
	    private   boolean withinQuotedString(int start, int end) {
	        return contains(stringTokens, start, end);
	    }
	    private   boolean contains(List<Token> tokens, int start, int end) {
	        for (Token token : tokens) {
	            if (token.contains(start, end)) { 
	                return true;
	            }
	        }
	        return false;
	    }
	    public void extractQuotedStringTokens(String query) {
	        stringTokens.clear();
	        stringMatcher.reset(query);
	        while (stringMatcher.find()) {
	            stringTokens.add(new Token(STRING,stringMatcher.start(),stringMatcher.end()));
	        }
	    }
	    private   String removeMultiLineComments(String sql) {
	        return removeTokensForMatcherWhenNotInString(multiLineCommentMatcher, sql);       
	    }
	    private   String removeSingleLineComments(String sql) {
	        return removeTokensForMatcherWhenNotInString(singleLineCommentMatcher, sql);      
	    }
	    public   String removeAllCommentsFromQuery(String sql) {
	        String newSql = removeMultiLineComments(sql);
	        return removeSingleLineComments(newSql);
	    }
}
