package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.shape.MeshView;

public abstract class File3dImporter {
	protected ArrayList<Double> vertices = new ArrayList<Double>();
    protected String directoryUrl;
    protected String fileUrl;
	
	public File3dImporter(String fileUrl) throws FileNotFoundException, IOException{
		this.fileUrl = fileUrl;
		File initialFile = new File(fileUrl);
		this.directoryUrl = initialFile.getParent();
	}
	
	protected abstract void readFile(InputStream targetStream) throws IOException;
    
    // 根据meshes中数据创建MeshView
    public abstract List<MeshView> getMeshViews();
	
    public ArrayList<Double> getVertices() {
		return vertices;
	}
	
    public void setVertices(ArrayList<Double> vertices) {
		this.vertices = vertices;
	}
    
    
    public static int[] arrayList2IntArray(List<Integer> arrayList){
    	int[] result = new int[arrayList.size()];
        Iterator<Integer> iterator = arrayList.iterator();
        for (int i = 0; i < result.length; i++)
        {
            result[i] = iterator.next().intValue();
        }
        return result;
    }
    
    public static float[] arrayList2DoubleArray(List<Double> arrayList){
    	float[] result = new float[arrayList.size()];
        Iterator<Double> iterator = arrayList.iterator();
        for (int i = 0; i < result.length; i++)
        {
            result[i] = iterator.next().floatValue();
        }
        return result;
    }

	public abstract void updateMeshes();

	public String getFileUrl() {
		return fileUrl;
	}
	
	@Override
	public abstract File3dImporter clone();
}
