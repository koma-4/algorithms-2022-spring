package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import representation.Clause;



public class DimacsParser implements Parser {

    private static final String PROBLEM_LINE_PREFIX = "p";
    private static final String COMMENT_PREFIX = "c";

    public List<Clause> parse(String filePath) throws IOException {

        List<Clause> clauses = null;

        File file = new File(filePath);
        List<String> lines = FileUtils.readLines(file);
        boolean passedByProblemLine = false;
        int expectedNumberOfClauses = -1;

        for (String line : lines) {

            line = line.trim();

            if (line.startsWith(COMMENT_PREFIX)) continue;

            if (line.startsWith(PROBLEM_LINE_PREFIX)) {
                checkValidFormat(line);
                expectedNumberOfClauses = getNumberOfClauses(line);
                passedByProblemLine = true;
                clauses = new ArrayList<>(expectedNumberOfClauses);
                continue;
            }

            if (passedByProblemLine && clauses.size() < expectedNumberOfClauses) {

                String[] clauseStrings = line.split(" +");
                List<Integer> literals = new ArrayList<>(clauseStrings.length - 1); // Не считаем 0 в конце
                for (String token : clauseStrings) {
                    Integer literal = new Integer(token);
                    if (literal != 0) literals.add(literal);
                }

                Clause clause = new Clause(literals);
                clauses.add(clause);

            }


        }

        return clauses;
    }

    private Integer getNumberOfClauses(String line) {
        String numberOfClauses = getPiece(line, 3);
        return new Integer(numberOfClauses);
    }

    private String getPiece(String line, int position) {
        return line.split(" +")[position];
    }

    private void checkValidFormat(String line) {
        String format = getPiece(line, 1);
        if (!"cnf".equalsIgnoreCase(format) && !"dnf".equalsIgnoreCase(format)) {
            throw new RuntimeException("Unexpected clauses format " + format);
        }
    }

    public static void main(String[] args) throws IOException {
        DimacsParser dimacsParser = new DimacsParser();
        List<Clause> parse = dimacsParser.parse("src/examples/satisfiable_by_up.cnf");
        System.out.println(parse);
    }
}
