package neprowaet.jpcw.net;

import neprowaet.jpcw.io.Packet;

public class FuturePacket<T extends Packet> {
    private volatile T result = null;
    volatile boolean done = false;

    public T get() throws InterruptedException {
        if(!done) {
            synchronized (this) {
                if(!done) wait();
            }
        }
        return result;
    }

    public boolean set (Packet result) {
        if (done) return false;
        synchronized (this) {
            this.result = (T) result;
            this.done = true;

            notifyAll();
            return true;
        }
    }
}
