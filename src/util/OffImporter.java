package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class OffImporter extends File3dImporter{

    private ArrayList<TriangleMesh> meshes = new ArrayList<>();
	private double minX=1001, maxX=0, minY=1001, maxY=0, minZ=0;

    public OffImporter(String objFileUrl) throws FileNotFoundException, IOException {
    	super(objFileUrl);
		File initialFile = new File(objFileUrl);
	    InputStream targetStream = new FileInputStream(initialFile);
        readFile(targetStream);
    }
    
    @SuppressWarnings({ "resource", "unused" })
	public long getFileLineNum(){
    	File test= new File(super.getFileUrl()); 
    	long fileLength = test.length(); 
    	LineNumberReader rf = null; 
    	try { 
	    	rf = new LineNumberReader(new FileReader(test)); 
	    	if (rf != null) { 
		    	int lines = 0; 
		    	rf.skip(fileLength); 
		    	lines = rf.getLineNumber(); 
		    	rf.close(); 
		    	return Math.max(200, lines);
	    	}
    	} catch (IOException e) { 
	    	if (rf != null) { 
		    	try { 
		    		rf.close();
		    		return Math.max(200, fileLength);
		    	} catch (IOException ee) { 
		    		return 200;
		    	} 
	    	}else{
	    		return 200;
	    	}
    	}
		return 200;
    }
    
    private ArrayList<PhongMaterial> randomMaterials(){
    	ArrayList<PhongMaterial> result = new ArrayList<PhongMaterial>();
    	for(int i=0; i<20; i++){
    		result.add(randomMaterial());
    	}
    	return result;
    }
    
	private PhongMaterial randomMaterial() {
		PhongMaterial material = new PhongMaterial();
		Random randomize = new Random();
		int randomRed = 3*((int)(6.0*randomize.nextDouble()));
		int randomGreen = 3*((int)(6.0*randomize.nextDouble()));
		int randomBlue = 3*((int)(6.0*randomize.nextDouble()));
		String specularString = String.format("#%X%X%X%X%X%X", randomRed, randomRed, randomGreen, randomGreen, randomBlue, randomBlue);
		int randomRed2 = randomRed/2;
		int randomGreen2 = randomGreen/2;
		int randomBlue2 = randomBlue/2;
		String diffuseString = String.format("#%X%X%X%X%X%X", randomRed2, randomRed2, randomGreen2, randomGreen2, randomBlue2, randomBlue2);
		Color specular = Color.web(specularString, 1.0);
		Color diffuse = Color.web(diffuseString, 1.0);
		material.setDiffuseColor(diffuse);
		material.setSpecularColor(specular);
		return material;
	}
    
    public OffImporter(String objFileUrl, 
    		ArrayList<Double> vertices,
    		ArrayList<MeshStart> key2meshStart, 
    		ArrayList<Integer> faces, 
    		ArrayList<Integer> faceNormals, 
    		ArrayList<Double> uvs, 
    		ArrayList<Double> normals,
    		PhongMaterial currentMaterial,
    		ArrayList<PhongMaterial> randomMaterials,
    		boolean randomColor,
    		ArrayList<Integer> smoothingGroups) throws FileNotFoundException, IOException {
    	super(objFileUrl);
    	super.setVertices(new ArrayList<Double>(vertices));
		this.key2meshStart = new ArrayList<MeshStart>(key2meshStart);
		this.faces = new ArrayList<Integer>(faces);
		this.faceNormals = new ArrayList<Integer>(faceNormals);
		this.uvs = new ArrayList<Double>(uvs);
		this.normals = new ArrayList<Double>(normals);
		this.currentMaterial = currentMaterial;
		this.randomMaterials = randomMaterials;
		this.randomColor = randomColor;
		this.smoothingGroups = new ArrayList<Integer>(smoothingGroups);
    }

    // 根据meshes中数据创建MeshView
    public MeshView createMeshView(Integer key, TriangleMesh triangleMesh) {
        MeshView meshView = new MeshView();
        if(randomColor){
            meshView.setMaterial(randomMaterials.get(key%randomMaterials.size()));
        }else{
            meshView.setMaterial(currentMaterial);
        }
        meshView.setMesh(triangleMesh);
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }
    
	protected ArrayList<Double> uvs = new ArrayList<Double>();
	protected ArrayList<Double> normals = new ArrayList<Double>();
    private ArrayList<Integer> faces = new ArrayList<Integer>();
    private ArrayList<Integer> smoothingGroups = new ArrayList<Integer>();
    private ArrayList<Integer> faceNormals = new ArrayList<Integer>();
    private ArrayList<MeshStart> key2meshStart = new ArrayList<MeshStart>();
    private PhongMaterial currentMaterial = new PhongMaterial(Color.RED);
    private ArrayList<PhongMaterial> randomMaterials;
    private boolean randomColor=true;
    private int facesStart = 0;
    private int facesNormalStart = 0;
    private static Integer DEFAULT_FACE = 0;
    private int smoothingGroupsStart = 0;

    @Override
	protected void readFile(InputStream inputStream) throws IOException {
    	long lineNum = getFileLineNum();
    	randomMaterials = randomMaterials();
    	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int currentSmoothGroup = 0;
        Integer key = DEFAULT_FACE;
        int vNum = 0, faceNum = 0;
		int currentV = 0, currentFace = 0;
        boolean start = false;
        while ((line = br.readLine()) != null) {
            try {
            	if (line.startsWith("OFF")){
            		// do nothing
            	} else if(start==false){
                	String[] split = line.trim().split(" +");
                    vNum = Integer.parseInt(split[0]);
                    faceNum = Integer.parseInt(split[1]);
                	start = true;
            	} else if (currentV < vNum) {
                	// 顶点
                    String[] split = line.trim().split(" +");
                    double x = Double.parseDouble(split[0]);
                    double y = Double.parseDouble(split[1]);
                    double z = Double.parseDouble(split[2]);
                    vertices.add(x);
                    vertices.add(y);
                    vertices.add(z);
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                    minZ = Math.min(minZ, z);
                    
                    currentV ++;
                    
                } else if (currentFace < faceNum) {
                	// 创建面
                    String[] split = line.trim().split(" +");
                    int length = Integer.parseInt(split[0]);
                    int[] points = new int[length];
                    for (int i = 0; i < length; i++) {
                        points[i] = Integer.parseInt(split[i+1]);
                    }
                    int v1 = points[0];
                    for (int i = 1; i < points.length - 1; i++) {
                        int v2 = points[i];
                        int v3 = points[i + 1];
                        // divide the polygon into triangles, then add into faces
                        faces.add(v1);
                        faces.add(-1);
                        faces.add(v2);
                        faces.add(-1);
                        faces.add(v3);
                        faces.add(-1);
                        faceNormals.add(-1);
                        faceNormals.add(-1);
                        faceNormals.add(-1);

                        smoothingGroups.add(currentSmoothGroup);
                    }
                    currentFace ++;
                    if(currentFace%(lineNum/100)==0){
	                	// 命名实体
	                    addMesh(key);
	                    key = new Integer(key+1);
                    }
                } else {
                	// 未知行，什么都不做
                }
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        addMesh(key);
    }
    
    private void addMesh(int key) {
    	// 若faceStart大于当前faces的大小，则没有必要创建网格
    	if (facesStart >= faces.size()) {
            smoothingGroupsStart = smoothingGroups.size();
            return;
        }
        // 给 meshStart 赋值
    	MeshStart meshStart = new MeshStart(facesStart, facesNormalStart, smoothingGroupsStart, 
    			faces.size(), faceNormals.size(), smoothingGroups.size(),
    			vertices.size(), uvs.size(), normals.size());
        
        TriangleMesh mesh = updateTriangleMesh(meshStart);

        while (meshes.size()>key) {
            key ++;
        }
        meshes.add(mesh);
        key2meshStart.add(meshStart);        
        facesStart = faces.size();
        facesNormalStart = faceNormals.size();
        smoothingGroupsStart = smoothingGroups.size();
    }

    @Override
    // 此函数用于顶点位置被修改后重新计算网格
    public void updateMeshes(){
        meshes.clear();
    	for(MeshStart meshStart: key2meshStart){
	        TriangleMesh mesh = updateTriangleMesh(meshStart);
	        meshes.add(mesh);
        }
    }
    
    // 创建三角网格组成的网格
	private TriangleMesh updateTriangleMesh(MeshStart meshStart) {
		Map<Integer, Integer> verticeMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> uvMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalMap = new HashMap<Integer, Integer>();
        ArrayList<Double> currentVertices = new ArrayList<Double>();
        ArrayList<Double> currentUVs = new ArrayList<Double>();
        ArrayList<Double> currentNormals = new ArrayList<Double>();
        ArrayList<Integer> faces = new ArrayList<Integer>(this.faces);
        ArrayList<Integer> faceNormals = new ArrayList<Integer>(this.faceNormals);
        boolean useNormals = true;

        for (int i = meshStart.getFacesStart(); i < meshStart.getFacesSize(); i += 2) {
            int vi = faces.get(i);
            Integer nvi = verticeMap.get(vi);
            if (nvi == null) {
                nvi = currentVertices.size() / 3;
                verticeMap.put(vi, nvi);
                currentVertices.add(vertices.get(vi * 3));
                currentVertices.add(vertices.get(vi * 3 + 1));
                currentVertices.add(vertices.get(vi * 3 + 2));
            }
            faces.set(i, nvi);

            int uvi = faces.get(i + 1);
            Integer nuvi = uvMap.get(uvi);
            if (nuvi == null) {
                nuvi = currentUVs.size() / 2;
                uvMap.put(uvi, nuvi);
                if (uvi >= 0) {
                    currentUVs.add(uvs.get(uvi * 2));
                    currentUVs.add(uvs.get(uvi * 2 + 1));
                } else {
                    currentUVs.add(0.0);
                    currentUVs.add(0.0);
                }
            }
            faces.set(i + 1, nuvi);
            
            if (useNormals) {
                int ni = faceNormals.get(i/2);
                Integer nni = normalMap.get(ni);
                if (nni == null) {
                    nni = currentNormals.size() / 3;
                    normalMap.put(ni, nni);
                    if (ni >= 0 && meshStart.getNormalSize() >= (ni+1)*3) {
                        currentNormals.add(normals.get(ni * 3));
                        currentNormals.add(normals.get(ni * 3 + 1));
                        currentNormals.add(normals.get(ni * 3 + 2));
                    } else {
                        useNormals = false;
                        currentNormals.add(0.0);
                        currentNormals.add(0.0);
                        currentNormals.add(0.0);
                    }
                }
                faceNormals.set(i/2, nni);
            }
        }

        // 创建网格
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(arrayList2DoubleArray(currentVertices));
        mesh.getTexCoords().setAll(arrayList2DoubleArray(currentUVs));
        mesh.getFaces().setAll(arrayList2IntArray(faces.subList(meshStart.getFacesStart(), meshStart.getFacesSize())));

        // 使用法向量
        int[] smGroups;
        if (useNormals) {
            int[] newFaces = arrayList2IntArray(faces.subList(meshStart.getFacesStart(), meshStart.getFacesSize()));
            int[] newFaceNormals = arrayList2IntArray(faceNormals.subList(meshStart.getFacesNormalStart(), meshStart.getFacesNormalSize()));
            smGroups = SmoothingGroups.calcSmoothGroups(mesh, newFaces, newFaceNormals, arrayList2DoubleArray(currentNormals));
        } else {
        	smGroups = arrayList2IntArray(smoothingGroups.subList(meshStart.getSmoothingGroupsStart(), meshStart.getSmoothingGroupSize()));
        }
        mesh.getFaceSmoothingGroups().setAll(smGroups);
		return mesh;
	}

	@Override
	public List<MeshView> getMeshViews() {
		List<MeshView> result = new ArrayList<MeshView>();
		int i = 0;
		for(TriangleMesh triangle: meshes){
			MeshView meshView = this.createMeshView(i, triangle);
			meshView.setDrawMode(DrawMode.FILL);
			result.add(meshView);
			i++;
		}
		return result;
	}
	
	@Override 
	public OffImporter clone(){
		try {
			return new OffImporter(super.getFileUrl(),     		
					vertices,
		    		key2meshStart, 
		    		faces, 
		    		faceNormals, 
		    		uvs, 
		    		normals,
		    		currentMaterial,
		    		randomMaterials,
		    		randomColor, 
		    		smoothingGroups);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public double getMinX() {
		return minX;
	}
	
	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMinZ() {
		return minZ;
	}

	public boolean isRandomColor() {
		return randomColor;
	}

	public Color getSpecularColor() {
		return this.currentMaterial.getSpecularColor();
	}

	public Color getDiffuseColor() {
		return this.currentMaterial.getDiffuseColor();
	}

	public void setMaterial(boolean randomColor, Color diffuseColor, Color specularColor) {
		this.randomColor = randomColor;
		this.currentMaterial.setSpecularColor(specularColor);
		this.currentMaterial.setDiffuseColor(diffuseColor);
	}
	
	
}
