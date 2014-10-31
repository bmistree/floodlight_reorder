package Reorder;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class MVar<TypeName>
{
    protected TypeName internal = null;
    protected boolean has_been_written = false;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();

    public void set(TypeName to_set)
    {
        lock.lock();
        has_been_written = true;
        internal = to_set;
        condition.signal();
    }
    
    public TypeName blocking_get()
    {
        lock.lock();
        try
        {
            while (! has_been_written)
                condition.await();

            has_been_written = false;
            return internal;
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();
            Util.force_assert("MVar received interrupted exception");
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }
}