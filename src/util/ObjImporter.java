/*
 * 此函数由 Rainorangelemon 在原代码基础上进行重构与更改，已完全理解原代码所含内容。
 * 
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * 
 * 
 * Modifier: Rainorangelemon
 *
 *
 *
 *
 *
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/* Obj file reader */
public class ObjImporter extends File3dImporter{

	// Obj file 中可能含有序号为负的顶点索引，转化为非负索引
    private int verticeIndex(int verticeIndex) {
        if (verticeIndex <= 0) {
            return verticeIndex + (vertices.size() / 3) - 1;
        } else {
            return verticeIndex - 1;
        }
    }

    // Obj file 中可能含有序号为负的纹理顶点索引，转化为非负索引
    private int uvIndex(int uvIndex) {
        if (uvIndex <= 0) {
            return uvIndex + (uvs.size() / 2) - 1;
        } else {
            return uvIndex - 1;
        }
    }

    // Obj file 中可能含有序号为负的法线索引，转化为非负索引
    private int normalIndex(int normalIndex) {
        if (normalIndex <= 0) {
            return normalIndex + (normals.size() / 3) - 1;
        } else {
            return normalIndex - 1;
        }
    }

    public Set<String> getMeshes() {
        return meshes.keySet();
    }

    private HashMap<String, TriangleMesh> meshes = new HashMap<>();
    private HashMap<String, Material> materials = new HashMap<>();
    private ArrayList<Map<String, Material>> materialLibrary = new ArrayList<>();

    public ObjImporter(String objFileUrl) throws Exception{
    	super(objFileUrl);
		File initialFile = new File(objFileUrl);
	    InputStream targetStream = new FileInputStream(initialFile);
        readFile(targetStream);
    }
    
    
    
    public ObjImporter(String objFileUrl, 
    		ArrayList<Double> vertices,
    		HashMap<String, MeshStart> key2meshStart, 
    		ArrayList<Integer> faces, 
    		ArrayList<Integer> faceNormals, 
    		ArrayList<Double> uvs, 
    		ArrayList<Double> normals, 
    		ArrayList<Map<String, Material>> materialLibrary, 
    		HashMap<String, Material> materials, 
    		ArrayList<Integer> smoothingGroups) throws FileNotFoundException, IOException {
    	super(objFileUrl);
    	super.setVertices(new ArrayList<Double>(vertices));
		this.key2meshStart = new HashMap<String, MeshStart>(key2meshStart);
		this.faces = new ArrayList<Integer>(faces);
		this.faceNormals = new ArrayList<Integer>(faceNormals);
		this.uvs = new ArrayList<Double>(uvs);
		this.normals = new ArrayList<Double>(normals);
		this.materialLibrary = new ArrayList<Map<String, Material>>(materialLibrary);
		this.materials = new HashMap<String, Material>(materials);
		this.smoothingGroups = new ArrayList<Integer>(smoothingGroups);
    }

    // 根据meshes中数据创建MeshView
    public MeshView createMeshView(String key) {
        MeshView meshView = new MeshView();
        meshView.setId(key);
        meshView.setMaterial(materials.get(key));
        meshView.setMesh(meshes.get(key));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }
    
	protected ArrayList<Double> uvs = new ArrayList<Double>();
	protected ArrayList<Double> normals = new ArrayList<Double>();
    private ArrayList<Integer> faces = new ArrayList<Integer>();
    private ArrayList<Integer> smoothingGroups = new ArrayList<Integer>();
    private ArrayList<Integer> faceNormals = new ArrayList<Integer>();
    private HashMap<String, MeshStart> key2meshStart = new HashMap<>();
    private Material currentMaterial = new PhongMaterial(Color.WHITE);
    private int facesStart = 0;
    private int facesNormalStart = 0;
    private static String DEFAULT_FACE = "defaultFace";
    private int smoothingGroupsStart = 0;

    @Override
	protected void readFile(InputStream inputStream) throws Exception {
        try {
	    	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        String line;
	        int currentSmoothGroup = 0;
	        String key = DEFAULT_FACE;
	        while ((line = br.readLine()) != null) {
	                if (line.startsWith("mtllib ")) {
	                    // 设置materialLib
	                    String[] split = line.substring("mtllib ".length()).trim().split(" +");
	                    for (String filename : split) {
	                        MtlReader mtlReader = new MtlReader(filename, this.directoryUrl);
	                        materialLibrary.add(mtlReader.getMaterials());
	                    }
	                } else if (line.startsWith("usemtl ")) {
	                    addMesh(key);
	                    // 设置之后mesh的材质
	                    String materialName = line.substring("usemtl ".length());
	                    for (Map<String, Material> mm : materialLibrary) {
	                        Material m = mm.get(materialName);
	                        if (m != null) {
	                            currentMaterial = m;
	                            break;
	                        }
	                    }
	                } else if (line.isEmpty() || line.startsWith("#")) {
	                    // 无视空行与注释
	                } else if (line.startsWith("v ")) {
	                	// 顶点
	                    String[] split = line.substring(2).trim().split(" +");
	                    double x = Double.parseDouble(split[0]);
	                    double y = Double.parseDouble(split[1]);
	                    double z = Double.parseDouble(split[2]);
	
	                    vertices.add(x);
	                    vertices.add(y);
	                    vertices.add(z);
	                    
	                } else if (line.startsWith("vt ")) {		
	                	// 材质顶点
	                    String[] split = line.substring(3).trim().split(" +");
	                    double u = Double.parseDouble(split[0]);
	                    double v = Double.parseDouble(split[1]);
	                    
	                    uvs.add(u);
	                    uvs.add(1 - v);
	                } else if (line.startsWith("vn ")) {		
	                	// 顶点处法线向量
	                    String[] split = line.substring(2).trim().split(" +");
	                    double x = Double.parseDouble(split[0]);
	                    double y = Double.parseDouble(split[1]);
	                    double z = Double.parseDouble(split[2]);
	                    normals.add(x);
	                    normals.add(y);
	                    normals.add(z);
	                } else if (line.startsWith("f ")) {
	                	// 创建面
	                    String[] split = line.substring(2).trim().split(" +");
	                    int[][] points = new int[split.length][];
	                    boolean uvGiven = true;
	                    boolean normalGiven = true;
	                    for (int i = 0; i < split.length; i++) {
	                        String[] split2 = split[i].split("/");
	                        if (split2.length < 2) {
	                            uvGiven = false;
	                        }
	                        if (split2.length < 3) {
	                            normalGiven = false;
	                        }
	                        points[i] = new int[split2.length];
	                        for (int j = 0; j < split2.length; j++) {
	                            if (split2[j].length() == 0) {
	                                points[i][j] = 0;
	                                if (j == 1) {
	                                    uvGiven = false;
	                                } else if (j == 2) {
	                                    normalGiven = false;
	                                }
	                            } else {
	                                points[i][j] = Integer.parseInt(split2[j]);
	                            }
	                        }
	                    }
	                    int v1 = verticeIndex(points[0][0]);
	                    int uv1 = -1;
	                    int n1 = -1;
	                    // 纹理点必须大于等于零
	                    if (uvGiven) {
	                        uv1 = uvIndex(points[0][1]);
	                        if (uv1 < 0) {
	                            uvGiven = false;
	                        }
	                    }
	                    // 法线必须大于等于零
	                    if (normalGiven) {
	                        n1 = normalIndex(points[0][2]);
	                        if (n1 < 0) {
	                            normalGiven = false;
	                        }
	                    }
	                    for (int i = 1; i < points.length - 1; i++) {
	                        int v2 = verticeIndex(points[i][0]);
	                        int v3 = verticeIndex(points[i + 1][0]);
	                        int uv2 = -1;
	                        int uv3 = -1;
	                        int n2 = -1;
	                        int n3 = -1;
	                        if (uvGiven) {
	                            uv2 = uvIndex(points[i][1]);
	                            uv3 = uvIndex(points[i + 1][1]);
	                        }
	                        if (normalGiven) {
	                            n2 = normalIndex(points[i][2]);
	                            n3 = normalIndex(points[i + 1][2]);
	                        }
	                        // divide the polygon into triangles, then add into faces
	                        faces.add(v1);
	                        faces.add(uv1);
	                        faces.add(v2);
	                        faces.add(uv2);
	                        faces.add(v3);
	                        faces.add(uv3);
	                        faceNormals.add(n1);
	                        faceNormals.add(n2);
	                        faceNormals.add(n3);
	
	                        smoothingGroups.add(currentSmoothGroup);
	                    }
	                } else if (line.startsWith("g ") || line.equals("g")) {
	                	// 命名实体
	                    addMesh(key);
	                    key = line.length() > 2 ? line.substring(2) : DEFAULT_FACE;
	                
	                } else if (line.startsWith("s ")) {
	                	// 平滑化
	                    if (line.substring(2).equals("off")) {
	                        currentSmoothGroup = 0;
	                    } else {
	                        currentSmoothGroup = Integer.parseInt(line.substring(2));
	                    }
	                } else {
	                	// 未知行，什么都不做
	                }
	        }
	        addMesh(key);
        } catch (Exception ex) {
        	throw ex;
        }
    }
    
    private void addMesh(String key) {
    	// 若faceStart大于当前faces的大小，则没有必要创建网格
    	if (facesStart >= faces.size()) {
            smoothingGroupsStart = smoothingGroups.size();
            return;
        }
        // 给 meshStart 赋值
    	MeshStart meshStart = new MeshStart(facesStart, facesNormalStart, smoothingGroupsStart, 
    			faces.size(), faceNormals.size(), smoothingGroups.size(),
    			vertices.size(), uvs.size(), normals.size());
        
        TriangleMesh mesh = updateTriangleMesh(meshStart, true);

        int keyIndex = 2;
        String keyBase = key;
        while (meshes.get(key) != null) {
            key = keyBase + " (" + keyIndex + ")";
            keyIndex++;
        }
        meshes.put(key, mesh);
        key2meshStart.put(key, meshStart);
        materials.put(key, currentMaterial);

        facesStart = faces.size();
        facesNormalStart = faceNormals.size();
        smoothingGroupsStart = smoothingGroups.size();
    }

    @Override
    // 此函数用于顶点位置被修改后重新计算网格
    public void updateMeshes(){
        meshes.clear();
    	for(String key: key2meshStart.keySet()){
	        TriangleMesh mesh = updateTriangleMesh(key2meshStart.get(key), false);
	        meshes.put(key, mesh);
        }
    }
    
    // 创建三角网格组成的网格
	private TriangleMesh updateTriangleMesh(MeshStart meshStart, boolean update) {
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
		for(String key: getMeshes()){
			MeshView meshView = this.createMeshView(key);
			meshView.setDrawMode(DrawMode.FILL);
			result.add(meshView);
		}
		return result;
	}
	
	@Override 
	public ObjImporter clone(){
		try {
			return new ObjImporter(super.getFileUrl(),     		
					vertices,
		    		key2meshStart, 
		    		faces, 
		    		faceNormals, 
		    		uvs, 
		    		normals, 
		    		materialLibrary, 
		    		materials, 
		    		smoothingGroups);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
