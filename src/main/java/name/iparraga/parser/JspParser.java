package name.iparraga.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import name.iparraga.model.MainClass;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JspParser implements ANTLRErrorListener {
	private static Logger logger = LoggerFactory.getLogger(JspParser.class);

	private final List<SyntacticProblem> errors = new ArrayList<>();

	private final Reader input;
	private final String filePath;
	private final String package_;
	private final String apiPath;
	private final String className;
	private final MainClass mainClass;
	private final String ejbName;

	public JspParser(Reader contentToParse, String filePath, String package_, String apiPath)
			throws FileNotFoundException {

		input = contentToParse;
		this.filePath = filePath;
		this.package_ = package_;
		this.apiPath = apiPath;
		className = calculateClassName();
		ejbName = package_ + "-" + className;
		mainClass = createMainClass();
	}

	private String calculateClassName() {
		int extensionBeginning = apiPath.lastIndexOf('.');
		String classNameFromFile = apiPath.substring(0, extensionBeginning);
		int fileBegining = classNameFromFile.lastIndexOf('/');
		classNameFromFile = classNameFromFile.substring(fileBegining + 1);
		return camelizeClassname(classNameFromFile);
	}

	private String camelizeClassname(String notCamelizedClassName) {
		if (!notCamelizedClassName.contains("_")) {
			return notCamelizedClassName;
		}

		String[] parts = notCamelizedClassName.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	private String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private MainClass createMainClass() {
		MainClass mainClass = new MainClass(
				package_, className, apiPath, filePath);

		return mainClass;
	}



	public MainClass run() throws IOException {
		ANTLRInputStream antlrStream;
		try {
			antlrStream = new ANTLRInputStream(input);
			jsplexer lexer = new jsplexer(antlrStream);
			Helper helper = new Helper(mainClass);
			BufferedTokenStream tokensStream = new BufferedTokenStream(lexer);
			jspparserParser parser = new jspparserParser(tokensStream, helper);
			parser.addErrorListener(this);
			parser.jspFile();

			if (hasErrors()) {
				throw new RuntimeException(
					"Problems parsing! " + Arrays.toString(errors.toArray()));
			}
			return parser.helper.getMainClass();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			input.close();
		}
	}

	private boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		errors.add(new SyntacticProblem(line, charPositionInLine, msg));
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
			int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, BitSet conflictingAlts,
			ATNConfigSet configs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
		// TODO Auto-generated method stub
	}

	private static class SyntacticProblem {
		int line;
		int charPositionInLine;
		String msg;

		public SyntacticProblem(int line, int charPositionInLine, String msg) {
			this.line = line;
			this.charPositionInLine = charPositionInLine;
			this.msg = msg;
		}

		@Override
		public String toString() {
			return "SyntacticProblem [line=" + line + ", charPositionInLine="
					+ charPositionInLine + ", msg=" + msg + "]";
		}
	}
}
