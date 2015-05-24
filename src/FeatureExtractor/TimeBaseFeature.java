package FeatureExtractor;

public class TimeBaseFeature {
    private int numSrc;
    private int numSrcSamePort;
    private int numSrcDiffPort;
    private int numSYNSameSrc;
    private int numRSTSameSrc;
    
    private int numDst;
    private int numDstSamePort;
    private int numDstDiffPort;
    private int numSYNSameDst;
    private int numRSTSameDst;
    
    public TimeBaseFeature() {
		// TODO Auto-generated constructor stub
        numSrc = 0;
        numSrcSamePort = 0;
        numSrcDiffPort = 0;
        numSYNSameSrc = 0;
        numRSTSameSrc = 0;
        
        numDst = 0;
        numDstSamePort = 0;
        numDstDiffPort = 0;
        numSYNSameDst = 0;
        numRSTSameDst = 0;
	}
    
	public void setNumSrc(int num){this.numSrc = num;}
    public void setNumSrcSamePort(int num){this.numSrcSamePort = num;}    
    public void setNumSrcDiffPort(int num){this.numSrcDiffPort = num;}    
    public void setNumSYNSameSrc(int num){this.numSYNSameSrc = num;}    
    public void setNumRSTSameSrc(int num){this.numRSTSameSrc = num;}  
    
    public void setNumDst(int num){this.numDst = num;}    
    public void setNumDstSamePort(int num){this.numDstSamePort = num;}    
    public void setNumDstDiffPort(int num){this.numDstDiffPort = num;}    
    public void setNumSYNSameDst(int num){this.numSYNSameDst = num;}    
    public void setNumRSTSameDst(int num){this.numRSTSameDst = num;}    

	public void addNumSrc(){this.numSrc++;}
    public void addNumSrcSamePort(){this.numSrcSamePort++;}    
    public void addNumSrcDiffPort(){this.numSrcDiffPort++;}    
    public void addNumSYNSameSrc(){this.numSYNSameSrc++;}    
    public void addNumRSTSameSrc(){this.numRSTSameSrc ++;}  
    
    public void addNumDst(){this.numDst ++;}    
    public void addNumDstSamePort(){this.numDstSamePort++;}    
    public void addNumDstDiffPort(){this.numDstDiffPort ++;}    
    public void addNumSYNSameDst(){this.numSYNSameDst ++;}    
    public void addNumRSTSameDst(){this.numRSTSameDst++;}    
    
    public int getNumSrc(){return this.numSrc;}
    public int getNumSrcSamePort(){return this.numSrcSamePort;}    
    public int getNumSrcDiffPort(){return this.numSrcDiffPort ;}    
    public int getNumSYNSameSrc(){return this.numSYNSameSrc;}    
    public int getNumRSTSameSrc(){return this.numRSTSameSrc;}  
    
    public int getNumDst(){return this.numDst;}    
    public int getNumDstSamePort(){return this.numDstSamePort;}    
    public int getNumDstDiffPort(){return this.numDstDiffPort;}    
    public int getNumSYNSameDst(){return this.numSYNSameDst;}    
    public int getNumRSTSameDst(){return this.numRSTSameDst;}
    
    
}
