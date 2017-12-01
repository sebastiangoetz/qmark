package org.tud.qmark.system;

import java.util.Map;

/**
 * 
 * @author: Daniel Schaller
 */
@SuppressWarnings("rawtypes")
public interface Query extends Map
{
    public static final String AND = "AND";
    public static final String OR = "OR";

    /**
     * @return short type of boolean operator, eiter OR or AND
     */
    public String getBoolOperator();

    /**
     * 
     * @return java.util.Collection
     */
    public java.util.Collection getParameterNames();
}