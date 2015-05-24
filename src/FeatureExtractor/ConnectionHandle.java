package FeatureExtractor;

import java.util.List;

import IDSmain.IDSmain;

public class ConnectionHandle implements Runnable{

	private RealTimeFeature realTimeFeature;
	
	public ConnectionHandle(RealTimeFeature realTimeFeature) {
		this.realTimeFeature = realTimeFeature;
	}

	@Override
	public void run() {
		if(IDSmain.connFeature.isEmpty()){
			this.realTimeFeature.setTimeBaseFeature(new TimeBaseFeature());
			IDSmain.connFeature.addConnection(realTimeFeature);
			IDSmain.featureEntries.addFeatureEntry(realTimeFeature);

		}
		else{
			List<RealTimeFeature> ConnFeatureInLast2s = IDSmain.connFeature.getListRealTimeFeature();
			TimeBaseFeature timeBaseFeature = new TimeBaseFeature();
			for(RealTimeFeature e : ConnFeatureInLast2s){
				if(e.getSrcIp().equals(this.realTimeFeature.getSrcIp())){
					timeBaseFeature.addNumSrc();
					if(e.getDstPort() == this.realTimeFeature.getDstPort())
						timeBaseFeature.addNumSrcSamePort();
					else
						timeBaseFeature.addNumSrcDiffPort();
					
					if(e.getNumTcpSyn() > 0)
						timeBaseFeature.addNumSYNSameSrc();
					if(e.getNumTcpReset() > 0)
						timeBaseFeature.addNumRSTSameSrc();
				}
				
				if(e.getDstIp().equals(this.realTimeFeature.getDstIp())){
					timeBaseFeature.addNumDst();
					if(e.getDstPort() == this.realTimeFeature.getDstPort())
						timeBaseFeature.addNumDstSamePort();
					else
						timeBaseFeature.addNumDstDiffPort();
					
					if(e.getNumTcpSyn() > 0)
						timeBaseFeature.addNumSYNSameDst();
					if(e.getNumTcpReset() > 0)
						timeBaseFeature.addNumRSTSameDst();
				}
				
			}
			realTimeFeature.setTimeBaseFeature(timeBaseFeature);
			IDSmain.connFeature.addConnection(realTimeFeature);
			IDSmain.featureEntries.addFeatureEntry(realTimeFeature);
		}
	}
	
}

