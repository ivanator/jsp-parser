package name.iparraga.parser;

import name.iparraga.model.Code;
import name.iparraga.model.Comment;
import name.iparraga.model.Import;
import name.iparraga.model.MainClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);
	private final MainClass class_;
	private final boolean startCode = false;

	public Helper(MainClass mainClass) {
		class_ = mainClass;
	}

	public void addComment(String comment) {
		logger.debug("adding comment " + comment);
		String commentCode = comment.replace("<%--", "").replace("--%>","");
		commentCode = "/*\n" + commentCode + "\n*/";

		if (startCode) {
			class_.addCode(new Code(commentCode));
		} else {
			class_.addComment(new Comment(commentCode));
		}
	}


	public void addImport(String import_) {
		logger.debug("adding import " + import_);
		class_.addImport(new Import(import_));
	}

	public void addCode(String code) {
		logger.debug("adding code " + code);
		class_.addCode(new Code(code.replace("<%", "").replace("%>","")));
	}

	public void debug(String msg) {
		logger.debug("-------------> " + msg);
	}

	public MainClass getMainClass() {
		return class_;
	}
}
