package org.tud.qmark.system;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * @author Sebastian Richy - maquiz@googlemail.com
 * Factory for SQL Queries to load mass data 
 */

@Named("boFactory")
@RequestScoped
@Stateful
public abstract class BOFactory
{
    @Inject
    private transient Logger logger;

    private Boolean createLightBO;

    private static Boolean isMSSQL = null;

    public static final int DEFAUT_ID_CHUNK_SIZE = 50;
    private static final String IS_NULL = "IS NULL";

    private static Pattern pattern;
    private static final String PATTERN_STRING = "=\\s*\\?";
    private static final PatternCompiler patternCompiler = new Perl5Compiler();
    private static final PatternMatcher patternMatcher = new Perl5Matcher();
    private static final String PROPERTIES_PATH = "de/jexam/properties/sql.properties";
    protected static final Properties sql = new Properties();

    protected static Integer sqlCommandCounter = new Integer(0);

    private static String url = "jdbc:mysql://141.76.65.164:3306/";
    private static String dbName = "QMark";
    private static String driver = "com.mysql.jdbc.Driver";
    private static String userName = "QMark"; 
    private static String password = "tigermilk";
    
    static
    {
        InputStream inSQL = BOFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
        try
        {
            sql.load(inSQL);
        }
        catch(IOException e)
        {
            System.out.println("Error trying to load properties file " + PROPERTIES_PATH);
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(inSQL != null) inSQL.close();
            }
            catch(IOException e)
            {
                System.out.println("Could not close input stream after loading properties file"
                        + PROPERTIES_PATH);
                e.printStackTrace();
            }
        }
        
        try
        {
            pattern = patternCompiler.compile(PATTERN_STRING);
        }
        catch(MalformedPatternException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * BOFactory constructor comment.
     */
    public BOFactory()
    {
        super();
        createLightBO = new Boolean(false);
        lookup();
    }

    /**
     * 
     * @param propKey
     * @param whereQuery
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected SQLQuery buildSqlQuery(String propKey, Query whereQuery)
    {
        //get prefix of sql statement with the given property key
        String select = sql.getProperty(propKey);
        //get boolean operator form query
        String booleanOperation = whereQuery.getBoolOperator();
        //get parameter name form query
        Iterator it = whereQuery.getParameterNames().iterator();
        //create empty list for values, 
        //this list will contain the values in right order for replacing all questions marks in composed sql statement
        LinkedList values = new LinkedList();
        //check for empty select statement
        if(select != null)
        {
            //iterate over parameter name
            while(it.hasNext())
            {
                //fetch current parameter name
                String key = (String)it.next();
                //fetch corresponding value
                Object value = whereQuery.get(key);
                //check for empty value
                if(value != null)
                {
                    //get condition statement for parameter name, called <propertyKey>.<parameterName> in poperties file
                    String condition = sql.getProperty(propKey + "." + key);
                    //get number of question marks in condition statement, number is also a property called <propertyKey>.<parameterName>.number in poperties file
                    int number = new Integer(sql.getProperty(propKey + "." + key + ".number")).intValue();
                    //iterate over question marks in condition statement
                    for(int i = 1; i <= number; i++)
                    {
                        //house number of current question mark in condition statement as string
                        String numberKey = Integer.toString(i);
                        //parameter name of the current question marks value, called <propertyKey>.<parameterName>.<questionMarkHouseNumber> in poperties file
                        String referedKey = sql.getProperty(propKey + "." + key + "." + numberKey);
                        //check for empty key
                        if(referedKey != null)
                        {
                            //get the current question marks value
                            Object referedValue = whereQuery.get(referedKey);
                            //check for empty value
                                
                            //add current value to the list of values
                            values.addLast(referedValue);

                        }
                        else
                        {
                            throw new IllegalArgumentException("subquery refers to an non existing key: "
                                    + key + " ---> " + referedKey);
                        }
                    }
                    //append the queries booleanOperation and the current condition statement to sql statement 
                    select += " " + booleanOperation + " " + condition;
                }
            }
            //if given, add order by statement to sql statement
            String order = sql.getProperty(propKey + ".order");
            if(order != null) select += " " + order;
        }
        //transfer value list into an array of objects
        Object[] parameters = new Object[values.size()];
        it = values.iterator();
        int i = 0;
        while(it.hasNext())
        {
            parameters[i] = it.next();
            i++;
        }
        //create SQLQuery object
        if(containsNULL(parameters))
            return prepareQuery(select, parameters);
        else
            return new SQLQuery(select, parameters);
    }

    /**
     * Methode simply calls <code>close()</code> on the given parameters if they are not null.
     * 
     * @param con
     * @param rs
     * @param stmt
     */
    protected void close(Connection con, ResultSet rs, Statement stmt)
    {
        try
        {
            if(rs != null) rs.close();
            if(stmt != null) stmt.close();
            if(con != null && !con.isClosed()) con.close();
        }
        catch(Exception ex)
        {
            System.out.println("Exception during closing database connection, statement or result set.");
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @param args
     * @return
     */
    private boolean containsNULL(Object[] args)
    {
        for(int i = 0; i < args.length; i++)
        {
            if(args[i] == null)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
    protected abstract List create(ResultSet rs) throws SQLException;

    /**
     * 
     * @param sqlQuery
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List executeQuery(SQLQuery sqlQuery)

    {
        List result = new LinkedList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String query = sqlQuery.getSql();
        Object[] params = sqlQuery.getParameters();
        try
        {
            con = getConnection();
            if(con != null && query != null)
            {
                stmt = con.prepareStatement(query);
                for(int i = 0; i < params.length; i++)
                {
                    stmt.setObject(i + 1, params[i]);
                }
                rs = stmt.executeQuery();
                //sqlcounter
                synchronized(sqlCommandCounter)
                {
                    sqlCommandCounter = new Integer(sqlCommandCounter.intValue() + 1);
                }
                rs.setFetchSize(200);
                result = create(rs);
            }
            else
                logger.info("Connection or query was null: connection = " + con + "; query = " + query);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        close(con, rs, stmt);
        return result;
    }

    /**
     * 
     * @param query
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List executeQuery(String query)
    {

        List result = new LinkedList();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {

            con = getConnection();
            if(con != null && query != null)
            {
                //Special treatment for SP-invokation in MSSQL
                DatabaseMetaData dbmd = con.getMetaData();
                if(dbmd.getDatabaseProductName().equals("Microsoft SQL Server") && query.startsWith("exec"))
                {
                    CallableStatement cstmt = con.prepareCall(query);
                    cstmt.execute();
                    boolean done = false;
                    while(!done)
                    {
                        boolean more = cstmt.getMoreResults();
                        if((more == false) && (cstmt.getUpdateCount() == -1)) done = true;
                        if(more)
                        {
                            rs = cstmt.getResultSet();
                            result = create(rs);
                        }
                    }
                    rs.close();
                    cstmt.close();
                    con.close();
                }
                else
                {
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);

                    rs.setFetchSize(200);
                    result = create(rs);
                }
                //sqlcounter
                synchronized(sqlCommandCounter)
                {
                    sqlCommandCounter = new Integer(sqlCommandCounter.intValue() + 1);
                }
            }
            else
                logger.info("Connection or query was null: connection = " + con + "; query = " + query);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        close(con, rs, stmt);
        return result;
    }

    /**
     * Executes a big query splitted into many little queries with only one connection
     * 
     * @param queries
     * @param params
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected List executeSplittedQuery(List queries, Object[] params, boolean lightBO)
    {
        List result = new LinkedList();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = null;

        try
        {
            con = getConnection();

            // iterate over the single queries
            for(Iterator it = queries.iterator(); it.hasNext();)
            {
                query = (String)it.next();

                if(con != null && query != null)
                {
                    DatabaseMetaData dbmd = con.getMetaData();
                    if(dbmd.getDatabaseProductName().equals("Microsoft SQL Server")
                            && query.startsWith("exec"))
                    {
                        CallableStatement cstmt = con.prepareCall(query);
                        if(params != null)
                        {
                            for(int i = 1; i <= params.length; i++)
                            {
                                cstmt.setObject(i, params[(i - 1)]);
                            }
                        }
                        cstmt.execute();
                        boolean done = false;
                        while(!done)
                        {
                            boolean more = cstmt.getMoreResults();
                            if((more == false) && (cstmt.getUpdateCount() == -1)) done = true;
                            if(more)
                            {
                                rs = cstmt.getResultSet();
                                result = create(rs);
                            }
                        }
                        rs.close();
                        cstmt.close();
                        con.close();
                    }
                    else
                    {
                        if(containsNULL(params))
                        {
                            SQLQuery sqlQuery = prepareQuery(query, params);
                            query = sqlQuery.getSql();
                            params = sqlQuery.getParameters();
                        }
                        stmt = con.prepareStatement(query);
                        for(int i = 0; i < params.length; i++)
                        {
                            //if(params[i] != null)
                            stmt.setObject(i + 1, params[i]);
                            //else stmt.setNull(i + 1, getSQLType(params[i]));
                        }
                        setCreateLightBO(lightBO);
                        rs = stmt.executeQuery();
                        //sqlcounter
                        /*synchronized (sqlCommandCounter)
                        {
                        sqlCommandCounter = new Integer(sqlCommandCounter.intValue() + 1);
                        }*/
                        rs.setFetchSize(200);
                        result.addAll(create(rs));
                    }
                }
                else
                {
                    logger.info("Connection or query was null: connection = " + con + "; query = " + query);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception during execution of sql query.");
            e.printStackTrace();
        }
        close(con, rs, stmt);
        return result;
    }

    /**
     * 
     * @param query
     * @param params
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List executeQuery(String query, Object[] params)
    {
        List result = new LinkedList();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try
        {
            con = getConnection();
            if(con != null && query != null)
            {
                DatabaseMetaData dbmd = con.getMetaData();
                if(dbmd.getDatabaseProductName().equals("Microsoft SQL Server") && query.startsWith("exec"))
                {
                    if(containsNULL(params))
                    {
                        SQLQuery sqlQuery = prepareQuery(query, params);
                        query = sqlQuery.getSql();
                        params = sqlQuery.getParameters();
                    }
                    CallableStatement cstmt = con.prepareCall(query);
                    if(params != null)
                    {
                        for(int i = 1; i <= params.length; i++)
                        {
                            cstmt.setObject(i, params[(i - 1)]);
                        }
                    }
                    cstmt.execute();
                    boolean done = false;
                    while(!done)
                    {
                        boolean more = cstmt.getMoreResults();
                        if((more == false) && (cstmt.getUpdateCount() == -1)) done = true;
                        if(more)
                        {
                            rs = cstmt.getResultSet();
                            result = create(rs);
                        }
                    }
                    rs.close();
                    cstmt.close();
                    con.close();
                }
                else
                {
                    if(containsNULL(params))
                    {
                        SQLQuery sqlQuery = prepareQuery(query, params);
                        query = sqlQuery.getSql();
                        params = sqlQuery.getParameters();
                    }
                    stmt = con.prepareStatement(query);
                    for(int i = 0; i < params.length; i++)
                    {
                        //if(params[i] != null)
                        stmt.setObject(i + 1, params[i]);
                        //else stmt.setNull(i + 1, getSQLType(params[i]));
                    }
                    rs = stmt.executeQuery();
                    rs.setFetchSize(200);
                    result = create(rs);
                }
            }
            else
            {
                logger.info("Connection or query was null: connection = " + con + "; query = " + query);
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception during execution of sql query.");
            e.printStackTrace();
        }
        close(con, rs, stmt);
        return result;
    }


    @SuppressWarnings("rawtypes")
    protected List executeQuery(String query, Object param)
    {
        return executeQuery(query, new Object[] {param});
    }

    @SuppressWarnings("rawtypes")
    protected Object executeQuerySingle(SQLQuery query)
    {
        try
        {
            List result = executeQuery(query);
            if(result != null && !result.isEmpty())
            {
                return result.get(0);
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    protected Object executeQuerySingle(String query)
    {
        try
        {
            List result = executeQuery(query);
            if(result != null && !result.isEmpty())
            {
                return result.get(0);
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    protected Object executeQuerySingle(String query, Object[] params)
    {
        try
        {
            List result = executeQuery(query, params);
            if(result != null && !result.isEmpty())
            {
                return result.get(0);
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    protected Object executeQuerySingle(String query, Object param)
    {
        return executeQuerySingle(query, new Object[] {param});
    }

    protected ResultSet executeStatement(PreparedStatement statement, String query) throws SQLException
    {
        ResultSet rs = statement.executeQuery();
        return rs;
    }

    protected ResultSet executeStatement(Statement statement, String query) throws SQLException
    {
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    /**
     * 
     * @return
     * @throws java.net.ConnectException
     */
    protected Connection getConnection() throws Exception
    {
        try
        {
            Class.forName(driver).newInstance();
            return DriverManager.getConnection(url+dbName,userName,password);
        }
        catch(Exception exception)
        {
            System.out.println("Cant get connection");
            exception.printStackTrace();
            throw exception;
        }
    }
    
    @SuppressWarnings("rawtypes")
    protected String getIDsAsCommaSeparatedList(Collection collection)
    {
        String idString = "";
        if(collection.size() > 0)
        {
            boolean first = true;
            Iterator iterator = collection.iterator();
            while(iterator.hasNext())
            {
                Object object = iterator.next();
                Long id = null;
                if(object instanceof Long)
                {
                    id = (Long)object;
                }
                else if(object instanceof Number)
                {
                    id = new Long(((Number)object).longValue());
                }
                if(id != null)
                {
                    if(first)
                    {
                        first = false;
                    }
                    else
                    {
                        idString += ",";
                    }
                    idString += id;
                }
            }
        }
        return idString;
    }

    /**
     * 
     *
     */
    protected void lookup()
    {
    }

    /**
     * 
     * @param input
     * @return
     */
    protected boolean parseToBoolean(String input)
    {
        boolean result = false;
        if(input != null)
        {
            if(trim(input).equals("1"))
            {
                result = true;
            }
            else
            {
                result = new Boolean(input).booleanValue();
            }
        }
        return result;
    }

    protected Long getLong(ResultSet rs, int index) throws SQLException
    {
        long value = rs.getLong(index);
        if(rs.wasNull())
        {
            return null;
        }

        return new Long(value);
    }

    protected Long getLong(ResultSet rs, String columnName) throws SQLException
    {
        long value = rs.getLong(columnName);
        if(rs.wasNull())
        {
            return null;
        }

        return new Long(value);
    }

    protected Date getDate(ResultSet rs, int index) throws SQLException
    {
        java.sql.Date ts = rs.getDate(index);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected Date getDate(ResultSet rs, String columnName) throws SQLException
    {
        java.sql.Date ts = rs.getDate(columnName);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected Date getTime(ResultSet rs, int index) throws SQLException
    {
        java.sql.Time ts = rs.getTime(index);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected Date getTime(ResultSet rs, String columnName) throws SQLException
    {
        java.sql.Time ts = rs.getTime(columnName);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected Date getDateTime(ResultSet rs, int index) throws SQLException
    {
        Timestamp ts = rs.getTimestamp(index);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected Date getDateTime(ResultSet rs, String columnName) throws SQLException
    {
        Timestamp ts = rs.getTimestamp(columnName);
        if(rs.wasNull())
        {
            return null;
        }

        return new Date(ts.getTime());
    }

    protected String getString(ResultSet rs, int index) throws SQLException
    {
        String value = rs.getString(index);
        if(value != null && value.equals(" "))
        {
            value = "";
        }
        return value;
    }

    protected String getString(ResultSet rs, String columnName) throws SQLException
    {
        String value = rs.getString(columnName);
        if(value != null && value.equals(" "))
        {
            value = "";
        }
        return value;
    }

    /**
     * 
     * @param query
     * @param args
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private SQLQuery prepareQuery(String query, Object[] args)
    {
        PatternMatcherInput input = new PatternMatcherInput(query);
        StringBuffer sb = new StringBuffer(query);
        int offsetCorrection = 0;
        ArrayList argsList = new ArrayList();
        int count = 0, match = 0;
        MatchResult result = null;
        while(patternMatcher.contains(input, pattern))
        {
            int resultLength = 0;
            result = patternMatcher.getMatch();
            if(args[count] == null)
            {
                int begin = result.beginOffset(0) + (match * IS_NULL.length()) - offsetCorrection;
                int end = result.endOffset(0) + (match * IS_NULL.length()) - offsetCorrection;
                sb.replace(begin, end, IS_NULL);
                match++;
                resultLength = result.toString().length();
            }
            else
            {
                argsList.add(args[count]);
            }
            offsetCorrection = offsetCorrection + resultLength;
            count++;
        }
        return new SQLQuery(sb.toString(), argsList.toArray());
    }

    /**
     * 
     * @param param
     * @return
     */
    protected String trim(String param)
    {
        if(param != null) return param.trim();
        return null;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedList getChunkedIDSetlist(Collection idSet)
    {
        return getChunkedIDSetlist(idSet, DEFAUT_ID_CHUNK_SIZE);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected LinkedList getChunkedIDSetlist(Collection idSet, int chunksize)
    {
        int count = 0;

        LinkedList idSetList = new LinkedList();

        if(idSet != null)
        {
            Iterator setIt = idSet.iterator();
            HashSet ids = null;
            Object obj;
            Long id;
            while(setIt.hasNext())
            {
                obj = setIt.next();
                if(obj instanceof Long)
                {
                    id = (Long)obj;
                }
                else if(obj instanceof Number)
                {
                    id = new Long(((Number)obj).longValue());
                }
                else if(obj instanceof Set)
                {
                    if(count % chunksize == 0 && (((Set)obj).size() == chunksize || !setIt.hasNext()))
                    {
                        idSetList.add(obj);
                        count += chunksize;
                    }
                    else
                    {
                        Iterator iter = ((Set)obj).iterator();
                        while(iter.hasNext())
                        {
                            obj = iter.next();
                            if((count % chunksize) == 0 || ids == null)
                            {
                                ids = new HashSet();
                                idSetList.add(ids);
                            }
                            count++;
                            ids.add(obj);
                        }
                    }
                    continue;
                }
                else
                {
                    continue;
                }
                if((count % chunksize) == 0)
                {
                    ids = new HashSet();
                    idSetList.add(ids);
                }
                count++;
                ids.add(id);

            }
        }
        return idSetList;
    }

    protected boolean isCreateLightBO()
    {
        return createLightBO;
    }

    protected void setCreateLightBO(boolean createLightBo)
    {
        createLightBO = new Boolean(createLightBo);
    }

    public void ejbCreate() throws CreateException
    {
    }

    public void ejbRemove() throws EJBException, RemoteException
    {
    }

    public void sortLongList(List<Long> col)
    {
        Collections.sort(col, new Comparator<Long>()
        {
            public int compare(Long o1, Long o2)
            {
                return o1.compareTo(o2);
            }
        });
    }

    public boolean isMSSQL()
    {
        if(isMSSQL == null)
        {
            Connection con = null;
            try
            {
                con = getConnection();
                DatabaseMetaData dbmd = con.getMetaData();
                if(dbmd.getDatabaseProductName().equals("Microsoft SQL Server"))
                    isMSSQL = Boolean.TRUE;
                else
                    isMSSQL = Boolean.FALSE;
                return isMSSQL.booleanValue();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    con.close();
                }
                catch(SQLException e)
                {

                    e.printStackTrace();
                }
            }
        }
        else
        {
            return isMSSQL.booleanValue();
        }
        return false;
    }
}