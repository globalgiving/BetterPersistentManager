package org.globalgiving.session;

import java.io.IOException;

import org.apache.catalina.Session;
import org.apache.catalina.session.PersistentManagerBase;
import org.apache.catalina.session.StandardSession;

/**
 * Implementation of the <b>Manager</b> interface that makes use of a Store to swap active Sessions to disk. It can be
 * configured to achieve several different goals:
 * <ul>
 * <li>Persist sessions across restarts of the Container</li>
 * <li>Fault tolerance, keep sessions backed up on disk to allow recovery in the event of unplanned restarts.</li>
 * <li>Limit the number of active sessions kept in memory by swapping less active sessions out to disk.</li>
 * </ul>
 * GlobalGiving Modifications:
 * <ul>
 * <li>Don't persist session when swapping if the backup time is less than the swap time (it is already persisted).</li>
 * <li>Run backups before swapping.</li>
 * </ul>
 *
 * @author Justin Rupp (jrupp@globalgiving.org)
 */
public final class BetterPersistentManager extends PersistentManagerBase
{
   /**
    * The descriptive name of this Manager implementation (for logging).
    */
   private static final String name = "BetterPersistentManager";

   /** {@inheritDoc} */
   @Override
   public String getName()
   {
      return name;
   }

   /** {@inheritDoc} */
   @Override
   public void processPersistenceChecks()
   {
      super.processMaxIdleBackups();
      super.processMaxIdleSwaps();
      super.processMaxActiveSwaps();
   }

   /**
    * Remove the session from the Manager's list of active sessions and write it out out to the Store if it has not
    * already been backed up. If the session is past expiration or invalid, this method does nothing.
    */
   @Override
   protected void swapOut(Session session) throws IOException
   {
      if (super.store == null || !session.isValid())
      {
         return;
      }

      ((StandardSession) session).passivate();
      // Only write the session if idle backup is disabled, or set to run at the same or greater interval as swapping.
      if (super.getMaxIdleBackup() < 0 || super.getMaxIdleBackup() >= super.getMaxIdleSwap())
      {
         super.writeSession(session);
      }
      super.removeSuper(session);
      session.recycle();
   }
}
