/* Generated By:JJTree: Do not edit this line. ASTScript.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTScript extends SimpleNode {
  public ASTScript(int id) {
    super(id);
  }

  public ASTScript(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=aaa1d25fd97067956be653c90f309276 (do not edit this line) */