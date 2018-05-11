/*
 * SQLFormatter.java
 *
 * Copyright (C) 2002-2015 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hhdb.csadmin.plugin.query.syntax;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

// borrowed and modified from org.hibernate.pretty.Formatter and DDLFormatter

/** 
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1487 $
 * @date     $Date: 2015-08-23 22:21:42 +1000 (Sun, 23 Aug 2015) $
 */
public class SQLFormatter {

    private static final String TOKEN_CHARS = "()+*/-=<>'`\"[],";

    private static final String WHITESPACE = " \n\r\f\t";
    
    private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
    private static final Set<String> END_CLAUSES = new HashSet<String>();
    private static final Set<String> LOGICAL = new HashSet<String>();
    private static final Set<String> QUANTIFIERS = new HashSet<String>();
    private static final Set<String> DML = new HashSet<String>();
    private static final Set<String> MISC = new HashSet<String>();

    static {
        
        BEGIN_CLAUSES.add("left");
        BEGIN_CLAUSES.add("right");
        BEGIN_CLAUSES.add("inner");
        BEGIN_CLAUSES.add("outer");
        BEGIN_CLAUSES.add("group");
        BEGIN_CLAUSES.add("order");

        END_CLAUSES.add("where");
        END_CLAUSES.add("set");
        END_CLAUSES.add("having");
        END_CLAUSES.add("join");
        END_CLAUSES.add("from");
        END_CLAUSES.add("by");
        END_CLAUSES.add("join");
        END_CLAUSES.add("into");
        END_CLAUSES.add("union");
        
        LOGICAL.add("and");
        LOGICAL.add("or");
        LOGICAL.add("when");
        LOGICAL.add("else");
        LOGICAL.add("end");
        
        QUANTIFIERS.add("in");
        QUANTIFIERS.add("all");
        QUANTIFIERS.add("exists");
        QUANTIFIERS.add("some");
        QUANTIFIERS.add("any");
        
        DML.add("insert");
        DML.add("update");
        DML.add("delete");
        
        MISC.add("select");
        MISC.add("on");
        //MISC.add("values");

    }
    
    String indentString = "    ";
    String initial = "\n    ";

    boolean beginLine = true;
    boolean afterBeginBeforeEnd = false;
    boolean afterByOrSetOrFromOrSelect = false;
    boolean afterValues = false;
    boolean afterOn = false;
    boolean afterBetween = false;
    boolean afterInsert = false;

    int inFunction = 0;
    int parensSinceSelect = 0;

    private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
    private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

    int indent = 1;

    StringBuilder result = new StringBuilder();
    StringTokenizer tokens;
    String lastToken;
    String token;
    String lcToken;
    
    private String sql;
    
    private boolean createTable;
    private boolean alterTable;
    private boolean commentOn;
    
    public SQLFormatter(String sql) {
        
        this.sql = removeBreaks(sql);

        String lowerCase = sql.toLowerCase();

        if (lowerCase.startsWith("create table")) {

            createTable = true;
            
            tokens = createTableTokenizer();

        } else if (lowerCase.startsWith("alter table")) {

            alterTable = true;
            
            tokens = alterTableTokenizer();

        } else if (lowerCase.startsWith("comment on")) {

            commentOn = true;
            
            tokens = commentOnTokenizer();

        }
        else {

            tokens = statementTokenizer();            
        }

    }

    private String removeBreaks(String text) {
        
        tokens = new StringTokenizer(text, "\n");
        
        StringBuilder sb = new StringBuilder();
        
        while (tokens.hasMoreTokens()) {
            
            token = tokens.nextToken().trim();
            
            int length = token.length();

            if (length > 0) {

                sb.append(token);

                String lastChar = token.substring(length - 1);
                
                if (!TOKEN_CHARS.contains(lastChar)) {
                    
                    sb.append(" ");
                }
                
            }

        }

        return sb.toString();
    }
    
    private StringTokenizer createTableTokenizer() {

        return new StringTokenizer(sql, "(,)'[]\"", true);
    }

    private StringTokenizer alterTableTokenizer() {

        return new StringTokenizer(sql, " (,)'[]\"", true);
    }

    private StringTokenizer commentOnTokenizer() {

        return new StringTokenizer(sql, " '[]\"", true);
    }

    private StringTokenizer statementTokenizer() {

        return new StringTokenizer(
                sql, 
                TOKEN_CHARS + WHITESPACE, 
                true
           );
    }

    public SQLFormatter setInitialString(String initial) {
        
        this.initial = initial;

        return this;
    }
    
    public SQLFormatter setIndentString(String indent) {
        
        this.indentString = indent;

        return this;
    }
    
    public String format() {
        
        String formattedSql = null; 
        
        if (createTable) {

            formattedSql = formatCreateTable();

        } else if (alterTable) {
            
            formattedSql = formatAlterTable();

        } else if (commentOn) {
            
            formattedSql = formatCommentOn();

        } else {
            
            formattedSql = formatStatement();
        }
     
        formattedSql = removeFirstFourChars(formattedSql);
        
        return formattedSql.trim();
    }
    
    // hack for now...    
    private String removeFirstFourChars(String formattedSql) {

        int beginIndex = 4;
        String fourChars = "    ";
        
        result.setLength(0);

        tokens = new StringTokenizer(formattedSql, "\n", true);
        
        while (tokens.hasMoreTokens()) {
            
            token = tokens.nextToken();
            
            if (token.startsWith(fourChars)) {
                
                result.append(token.substring(beginIndex));

            } else {
                
                result.append(token);
            }
            
        }
        
        return result.toString();
    }

    private String formatStatement() {
        
        result.append(initial);
        
        while (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            lcToken = token.toLowerCase();
            if ("'".equals(token)) {
                String t;
                do {
                    t = tokens.nextToken();
                    token += t;
                } 
                while (!"'".equals(t) && tokens.hasMoreTokens()); // cannot handle single quotes
            }       
            else if ("\"".equals(token)) {
                String t;
                do {
                    t = tokens.nextToken();
                    token += t;
                } 
                while (!"\"".equals(t));
            }
            
            if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
                commaAfterByOrFromOrSelect();
            }
            else if (afterOn && ",".equals(token)) {
                commaAfterOn();
            }
            
            else if ("(".equals(token)) {
                openParen();
            }
            else if (")".equals(token)) {
                closeParen();
            }

            else if (BEGIN_CLAUSES.contains(lcToken)) {
                beginNewClause();
            }

            else if (END_CLAUSES.contains(lcToken)) {
                endNewClause(); 
            }
            
            else if ("select".equals(lcToken)) {
            	newline();
                select();
            }

            else if (DML.contains(lcToken)) {
            	newline();
                updateOrInsertOrDelete();
            }
            
            else if ("values".equals(lcToken)) {
                values();
            }
            
            else if ("on".equals(lcToken)) {
                on();
            }
            
            else if (afterBetween && lcToken.equals("and")) {
                misc();
                afterBetween = false;
            }
            
            else if (LOGICAL.contains(lcToken)) {
                logical();
            }
            
            else if (isWhitespace(token)) {
                white();
            }
            
            else {
                misc();
            }
            
            if (!isWhitespace(token)) lastToken = lcToken;
            
        }

        return result.toString();
    }

    private void commaAfterOn() {
        out();
        indent--;
        newline();
        afterOn = false;
        afterByOrSetOrFromOrSelect = true;
    }

    private void commaAfterByOrFromOrSelect() {
        out();
        newline();
    }

    private void logical() {
        if ("end".equals(lcToken)) indent--;
        newline();
        out();
        beginLine = false;
    }

    private void on() {
        indent++;
        afterOn = true;
        newline();
        out();
        beginLine = false;
    }

    private void misc() {
        out();
        if ("between".equals(lcToken)) {
            afterBetween = true;
        }
        if (afterInsert) {
            newline();
            afterInsert = false;
        }
        else {
            beginLine = false;
            if ("case".equals(lcToken)) {
                indent++;
            }
        }
    }

    private void white() {
        if (!beginLine) {
            result.append(" ");
        }
    }
    
    private void updateOrInsertOrDelete() {
        out();
        indent++;
        beginLine = false;
        if ("update".equals(lcToken)) newline();
        if ("insert".equals(lcToken)) afterInsert = true;
    }

    private void select() {
        out();
        indent++;
        newline();
        parenCounts.addLast(new Integer(parensSinceSelect));
        afterByOrFromOrSelects.addLast(new Boolean(afterByOrSetOrFromOrSelect));
        parensSinceSelect = 0;
        afterByOrSetOrFromOrSelect = true;
    }

    private void out() {
        result.append(token);
    }

    private void endNewClause() {
        if (!afterBeginBeforeEnd) {
            indent--;
            if (afterOn) {
                indent--;
                afterOn=false;
            }
            
            if (!afterInsert) { 

                newline();
            }

        }
        out();
        if (!"union".equals(lcToken)) indent++;
        newline();
        afterBeginBeforeEnd = false;
        afterByOrSetOrFromOrSelect = "by".equals(lcToken) 
                || "set".equals(lcToken)
                || "from".equals(lcToken);
    }

    private void beginNewClause() {
        if (!afterBeginBeforeEnd) {
            if (afterOn) {
                indent--;
                afterOn=false;
            }
            indent--;
            newline();
        }
        out();
        beginLine = false;
        afterBeginBeforeEnd = true;
    }

    private void values() {
        indent--;
        
        if (")\n".equals(lastCharAsString(result))) {
            
            
        }
        
        newline();
        out();
        indent++;
        newline();
        afterValues = true;
    }

    private void closeParen() {
        parensSinceSelect--;
        if (parensSinceSelect<0) {
            indent--;
            parensSinceSelect = ((Integer) parenCounts.removeLast()).intValue();
            afterByOrSetOrFromOrSelect = ((Boolean) afterByOrFromOrSelects.removeLast()).booleanValue();
        }
        if (inFunction>0) {
            inFunction--;
            out();
        }
        else {
            if (!afterByOrSetOrFromOrSelect) {
                indent--;
                newline();
            }
            out();
        }
        beginLine = false;
    }

    private void openParen() {
        if (isFunctionName(lastToken) || inFunction>0) {
            inFunction++;
        }
        beginLine = false;
        if (inFunction>0) {
            out();
        }
        else {
            out();
            if (!afterByOrSetOrFromOrSelect) {
                indent++;
                newline();
                beginLine = true;
            }
        }
        parensSinceSelect++;
    }

    private boolean isFunctionName(String tok) {
        if (StringUtils.isBlank(tok)) {
            return false;
        }
        final char begin = tok.charAt(0);
        final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"'==begin;
        return isIdentifier && 
                !LOGICAL.contains(tok) && 
                !END_CLAUSES.contains(tok) &&
                !QUANTIFIERS.contains(tok) &&
                !DML.contains(tok) &&
                !MISC.contains(tok);
    }

    private boolean isWhitespace(String token) {
        
        return WHITESPACE.indexOf(token) >= 0;
    }
    
    private void newline() {

        result.append("\n");

        for (int i = 0; i < indent; i++) {

            result.append(indentString);
        }

        beginLine = true;
    }

    /**
     * Format an SQL statement using simple rules:
     *  a) Insert newline after each comma;
     *  b) Indent three spaces after each inserted newline;
     * If the statement contains single/double quotes return unchanged,
     * it is too complex and could be broken by simple formatting.
     */


    private String formatCommentOn() {

        StringBuilder result = new StringBuilder(60).append("\n    ");

        boolean quoted = false;

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            result.append(token);
            if (isQuote(token)) {
                quoted = !quoted;
            }
            else if (!quoted) {
                if ("is".equals(token)) {
                    result.append("\n       ");
                }
            }
        }
        
        return result.toString();
    }

    private String formatAlterTable() {

        String formattedSql = sql;
        
        if (formattedSql.indexOf("\n") != -1) {
            
            formattedSql = formattedSql.replaceAll("\n", " ");
        }
        
        return formattedSql;
        
        /*
        StringBuilder result = new StringBuilder(60).append("\n    ");

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
            }
            else if (!quoted) {
                if (isBreak(token)) {
                    result.append("\n        ");
                }
            }
            result.append(token);
        }
        
        return result.toString();
        */
    }

    private String formatCreateTable() {

        String indentOne = "\n    ";
        String indentTwo = "\n        ";

        StringBuilder result = new StringBuilder(60).append(indentOne);
        
        int depth = 0;
        boolean quoted = false;

        while (tokens.hasMoreTokens()) {

            String token = tokens.nextToken();

            if (isQuote(token)) {

                quoted = !quoted;
                result.append(token);

            } else if (quoted) {

                result.append(token);
                
            } else {

                if (")".equals(token)) {

                    depth--;
                    
                    if (depth==0) {
                        
                        result.append(indentOne);
                    }

                }

                if (token.startsWith(" ") 
                        && isWhitespace(lastCharAsString(result))) {

                    token = token.trim();
                }
                
                result.append(token);

                if (",".equals(token) && depth==1) {
                    
                    result.append(indentTwo);
                }

                if ("(".equals(token)) {

                    depth++;
                    
                    if (depth==1) {
                        
                        result.append(indentTwo);
                    }

                }

            }
        }
        
        return result.toString();
    }

    private String lastCharAsString(StringBuilder sb) {

        return String.valueOf(sb.charAt(sb.length() -1));        
    }
    
    /*
    private boolean isBreak(String token) {
        return "drop".equals(token) ||
            "add".equals(token) || 
            "references".equals(token) || 
            "foreign".equals(token) ||
            "on".equals(token);
    }
    */

    private boolean isQuote(String tok) {
        return "\"".equals(tok) || 
                "`".equals(tok) || 
                "]".equals(tok) || 
                "[".equals(tok) ||
                "'".equals(tok);
    }

    public static void main(String[] args) {
        
        String[] queries = {
                "select column1, column2 from table;",
                "CREATE TABLE public.candidate_request (candidate_request_id int8 NOT NULL, job int8, candidate int8, contact int8, job_application_details int8, message varchar(1000), status bpchar(3), datetime_created timestamptz DEFAULT now(), version_no int8 DEFAULT 1);",
                "insert into tableName (column1, column2) values ('1', '2');",
                "update tableName set column1 = 'value1', " +
                "column2 = 'valueXX' where columnX='Y' and id in (select id from table2);"
                };

        for (String query : queries) {
            System.out.println();
            System.out.println(new SQLFormatter(query).format());            
        }
                
    }
    
}











