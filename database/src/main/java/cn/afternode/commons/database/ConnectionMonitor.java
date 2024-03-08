package cn.afternode.commons.database;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ConnectionMonitor {
    private final long lifetime;
    private final HashMap<Connection, Long> stack = new HashMap<>();
    private final Thread thread;
    private boolean run;

    ConnectionMonitor(String name, long lifetime) {
        this.lifetime = lifetime;

        run = true;
        thread = new Thread(this::run);
        thread.setName("ConnectionMonitor-%s".formatted(name));
        thread.start();
    }

    public void add(Connection conn) {
        stack.put(conn, System.currentTimeMillis());
    }

    private void run() {
        while (run) {
            try {
                TimeUnit.SECONDS.sleep(5);

                var it = stack.keySet().iterator();
                while (it.hasNext()) {
                    Connection conn = it.next();
                    if (System.currentTimeMillis() - stack.get(conn) >= lifetime) {
                        conn.close();
                        it.remove();
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    public void close() {
        run = false;
        thread.interrupt();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }
}
