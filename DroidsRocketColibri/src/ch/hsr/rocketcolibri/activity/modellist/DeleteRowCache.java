package ch.hsr.rocketcolibri.activity.modellist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.SystemClock;

public class DeleteRowCache {
	private final int DELETE_COUNT_DOWN_MILLIS = 10000;
	private Map<Integer, Long> tCache = new HashMap<Integer, Long>();
	private Thread tDeleteCountDown;
	private DeleteCountDownListener tDeleteCountDownListener;
	
	public DeleteRowCache(DeleteCountDownListener dcdl){
		tDeleteCountDownListener = dcdl;
	}
	
	public void deleteRow(int pos){
		synchronized (tCache) {
			tCache.put(pos, System.currentTimeMillis());
			if(tDeleteCountDown==null || !tDeleteCountDown.isAlive()){
				createCountDown();
			}
		}
	}
	
	public void deleteRowDefinitely(int pos){
		synchronized (tCache) {
			Object removed = tCache.remove(pos);
			if(removed!=null){
				tDeleteCountDownListener.delete(pos);
			}
		}
	}
	
	public boolean deleteCanceled(int pos){
		Object removed = null;
		synchronized (tCache) {
			removed = tCache.remove(pos);
		}
		return removed!=null;
	}
	
	private void createCountDown(){
		tDeleteCountDown = new Thread(){public void run(){
			int cacheSize = 0;
			do{
				SystemClock.sleep(2000);
				synchronized (tCache) {
					Iterator<Integer> it = tCache.keySet().iterator();
					while (it.hasNext()){
						Integer key = it.next();
						if(System.currentTimeMillis()-tCache.get(key).longValue()>DELETE_COUNT_DOWN_MILLIS){
							tDeleteCountDownListener.delete(key.intValue());
							it.remove();
						}
					}
					cacheSize = tCache.size();
				}
			}while(cacheSize!=0);
		}};
		tDeleteCountDown.start();
	}
	
}
