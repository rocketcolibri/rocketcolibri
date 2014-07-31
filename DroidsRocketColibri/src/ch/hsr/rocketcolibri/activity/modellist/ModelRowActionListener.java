package ch.hsr.rocketcolibri.activity.modellist;

public interface ModelRowActionListener {
	void icon(int position);
	void edit(int position);
	/**return true if successfully*/
	boolean deleteItem(int position);
	/**return true if successfully*/
	boolean saveItem(int position, String name);
	void cancelItem(int position);
}
