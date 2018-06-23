package util;

public class MeshStart{
	private int facesStart, facesNormalStart, smoothingGroupsStart;
	private int facesSize, facesNormalSize, smoothingGroupSize;
	private int verticeSize, uvSize, normalSize;
	public MeshStart(int facesStart, 
			int facesNormalStart, 
			int smoothingGroupsStart,
			int facesSize, 
			int facesNormalSize, 
			int smoothingGroupSize,
			int verticeSize, 
			int uvSize, 
			int normalSize){
		this.facesStart = facesStart;
		this.facesNormalStart = facesNormalStart;
		this.smoothingGroupsStart = smoothingGroupsStart;
		this.facesSize = facesSize;
		this.facesNormalSize = facesNormalSize;
		this.smoothingGroupSize = smoothingGroupSize;
		this.verticeSize = verticeSize;
		this.uvSize = uvSize;
		this.normalSize =  normalSize;
	}
	public int getFacesStart() {
		return facesStart;
	}
	public int getFacesNormalStart() {
		return facesNormalStart;
	}
	public int getSmoothingGroupsStart() {
		return smoothingGroupsStart;
	}
	public int getFacesSize() {
		return facesSize;
	}
	public int getFacesNormalSize() {
		return facesNormalSize;
	}
	public int getSmoothingGroupSize() {
		return smoothingGroupSize;
	}
	public int getVerticeSize() {
		return verticeSize;
	}
	public int getUvSize() {
		return uvSize;
	}
	public int getNormalSize() {
		return normalSize;
	}
}
