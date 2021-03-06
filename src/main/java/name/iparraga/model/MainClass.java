package name.iparraga.model;

import java.util.LinkedList;
import java.util.List;

public class MainClass {
	private final List<Comment> comments = new LinkedList<>();
	private final List<Import> imports = new LinkedList<>();
	private final LinkedList<Code> codes = new LinkedList<>();
	private final List<Declaration> declarations = new LinkedList<>();
	private final List<Bundle> scopeVariables = new LinkedList<>();

	private static final String STANDARD_IMPORTS =
		"import java.io.StringWriter;\n" +
		"import java.io.PrintWriter;\n" +
		"import java.io.Writer;\n" +
		"import javax.ejb.Stateless;\n" +
		"import javax.servlet.http.HttpServletRequest;\n" +
		"import javax.servlet.http.HttpServletResponse;\n" +
		"import javax.servlet.http.HttpSession;\n" +
		"import javax.servlet.jsp.PageContext;\n" +
		"import javax.ws.rs.GET;\n" +
		"import javax.ws.rs.POST;\n" +
		"import javax.ws.rs.Path;\n" +
		"import javax.ws.rs.Produces;\n" +
		"import javax.ws.rs.core.Context;\n";

	private final String package_;
	private String className;
	private final String apiPath;
	private final String jspSourcePath;
	private String sourceJspCode;
	private String ejbName;

	private StringBuilder code;

	public MainClass(String package_, String className,
			String apiPath, String jspSourcePath) {

		if (apiPath.startsWith("/")) {
			this.apiPath = apiPath;
		} else {
			this.apiPath = "/" + apiPath;
		}

		this.jspSourcePath = jspSourcePath;
		this.package_ = package_;
		this.className = className;
	}

	public String toCode() {
		code = new StringBuilder();
		writeMetadata();
		writePackage();
		writeStandardImports();
		writeAddedImports();
		writeComments();
		writeClassAnnotations();
		writeClassBody();
		writeSourceJspCodeIfPresent();

		return code.toString();
	}

	private void writeMetadata() {
		code.append(
			"/*\n" +
			" * This class was automatically generated when transforming PPI to a JEE app\n" +
			" * on November 2013.\n" +
			" * \n" +
			" * The code of the generator can be found at:\n" +
			" * https://github.com/ivanator/jsp-parser\n" +
			" * \n" +
			" * Class derived from this source JSP:\n" +
			" * ");

		code.append(jspSourcePath);
		code.append("\n */\n");
	}

	private void writeComments() {
		for (Comment comment : comments) {
			comment.toCode(code);
		}
	}

	private void writeDeclarations() {
		for (Declaration declaration : declarations) {
			declaration.toCode(code);
		}
	}

	private void writePackage() {
		if (!"".equalsIgnoreCase(package_)) {
			code.append("package ");
			code.append(package_);
			code.append(";\n");
		}
	}

	private void writeStandardImports() {
		code.append(STANDARD_IMPORTS);
	}

	private void writeAddedImports() {
		for (Import import_ : imports) {
			import_.toCode(code);
		}
	}

	private void writeClassAnnotations() {
		writeStatelesAnnotation();
		writePathAnnotation();
	}

	private void writeStatelesAnnotation() {
		code.append("@Stateless");
		if (ejbName != null) {
			code.append("(name=\"");
			code.append(ejbName);
			code.append("\")");
		}
		code.append("\n");
	}

	private void writePathAnnotation() {
		code.append("@Path(\"");
		code.append(apiPath);
		code.append("\")\n");
	}

	private void writeClassBody() {
		writeClassStart();
		writeDoRunMethod();
		writeDeclarations();
		writeClassEnd();
	}

	private void writeClassStart() {
		code.append("public class ");
		code.append(className);
		code.append(" {\n");
	}

	private void writeDoRunMethod() {
		writeDoRunMethodBegining();
		writeScopeVariables();
		writeCodes();
		writeDoRunMethodEnding();
	}

	private void writeDoRunMethodBegining() {
		code.append("\t@GET @POST\n");
		code.append("\t@Produces(\"application/json; charset=UTF-8\")\n");
		code.append("\tpublic String doRun(\n");
		code.append("\t\t\t@Context HttpServletRequest request,\n");
		code.append("\t\t\t@Context HttpServletResponse response");
		code.append(") throws Exception {\n");
		code.append("\t\tHttpSession session = request.getSession(false);\n");
		code.append("\t\tWriter stringOut = new StringWriter();\n");
		code.append("\t\tPrintWriter out = new PrintWriter(stringOut);\n");
	}

	private void writeScopeVariables() {
		for (Bundle token : scopeVariables) {
			token.toCode(code);
		}
	}

	private void writeCodes() {
		for (Code token : codes) {
			token.toCode(code);
		}
	}

	private void writeDoRunMethodEnding() {
		code.append("\t\treturn stringOut.toString();\n");
		code.append("\t}\n");
	}

	private void writeClassEnd() {
		code.append("}");
	}

	private void writeSourceJspCodeIfPresent() {
		if (sourceJspCode != null) {
			code.append("\n/*\nOriginal JSP code as follows\n");
			code.append("(block comments replaced by: \"START-COMMENT\" ");
			code.append("and \"END-COMMENT\"):\n");
			code.append("---- ---- ----\n");
			code.append(escapeComments(sourceJspCode));
			code.append("\n---- ---- ----\n*/");
		}
	}

	private String escapeComments(String sourceJspCode) {
		sourceJspCode = sourceJspCode.replaceAll("/\\*\\*", "START-COMMENT");
		sourceJspCode = sourceJspCode.replaceAll("/\\*", "START-COMMENT");
		return sourceJspCode.replaceAll("\\*/", "END-COMMENT");
	}


	public void addImport(Import import_) {
		imports.add(import_);
	}

	@Override
	public int hashCode() {
		return toCode().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MainClass other = (MainClass) obj;

		String thisCode = toCode();
		String otherCode = other.toCode();

		return thisCode.equals(otherCode);
	}

	public void addCode(Code code) {
		codes.add(code);
	}

	public void addSourceJsp(String sourceJspCode) {
		this.sourceJspCode = sourceJspCode;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void addDeclaration(Declaration declaration) {
		declarations.add(declaration);
	}

	public String getEjbName() {
		return ejbName;
	}

	public void setEjbName(String ejbName) {
		this.ejbName = ejbName;
	}

	public void addBundle(Bundle scopeVariable) {
		this.scopeVariables.add(scopeVariable);
	}

	public void addHtml(HtmlCode code) {
		Code lastCode = codes.peekLast();
		if (lastCode instanceof HtmlCode) {
			((HtmlCode)lastCode).appendCode(code.code);
		} else {
			codes.add(code);
		}
	}
}
