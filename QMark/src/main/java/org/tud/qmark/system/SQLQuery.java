package org.tud.qmark.system;
/**
 * 
 * @author: Daniel Schaller
 */
public class SQLQuery
{

    private String sql = "";
    private Object[] parameters = new Object[1];


/**
 * SQLQuery constructor comment.
 */
public SQLQuery(String sql, Object[] o)
{
    super();
    this.sql = sql;
    this.parameters = o;
}



/**
 * 
 * @return java.lang.Object[]
 */
public java.lang.Object[] getParameters() {
    return parameters;
}



/**
 * 
 * @return java.lang.String
 */
public java.lang.String getSql() {
    return sql;
}
}