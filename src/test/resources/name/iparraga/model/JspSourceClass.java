/*
 * This class was automatically generated when transforming PPI to a JEE app
 * on November 2013.
 * 
 * The code of the generator can be found at:
 * https://github.com/ivanator/jsp-parser
 * 
 * Class derived from this source JSP:
 * modules/war/jsp/tests.jsp
 */
package com.test;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Writer;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
@Stateless
@Path("/test.jsp")
public class JspSourceClass {
	@GET
	@Produces("application/json; charset=UTF-8")
	public String doRun(
			@Context PageContext pageContext,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		Writer stringOut = new StringWriter();
		PrintWriter out = new PrintWriter(stringOut);
		return stringOut.toString();
	}
}
/*
Original JSP code as follows (block comments replaced by: "START-COMMENT" and END-COMMENT):
---- ---- ----
<% START-COMMENT original code END-COMMENT %>
---- ---- ----
*/