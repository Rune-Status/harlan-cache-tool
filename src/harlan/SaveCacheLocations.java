package harlan;

public class SaveCacheLocations  extends Thread {
	SaveCacheLocations(CacheViewer panel) {
		this.panel = panel;
	}
	CacheViewer panel;
	@Override
	public void run() {
		panel.saveCacheLocs();
	}
}