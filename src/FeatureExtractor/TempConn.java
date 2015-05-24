package FeatureExtractor;

public class TempConn {
	public long timeHandle;
	public RealTimeFeature realTimeFeature;
	
	public TempConn() {
		// TODO Auto-generated constructor stub
		timeHandle = 0;
		realTimeFeature = new RealTimeFeature();
	}
	
	public TempConn(long time, RealTimeFeature  realTimeFeature) {
		// TODO Auto-generated constructor stub
		timeHandle = time;
		this.realTimeFeature = realTimeFeature;
	}
}
