package com.grendelscan.data.database;

import com.almworks.sqlite4java.SQLiteConnection;

public class CommandJob extends DbJob<Object>
{

    public CommandJob(final String query, final Object[] values)
    {
        super(query, values);
    }

    @Override
    protected Object job(final SQLiteConnection connection) throws Throwable
    {
        handleBindings(connection);
        st.step();
        st.dispose();
        return null;
    }

}
