package de.m_marvin.voxelengine.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.simplelogging.printing.LogType;
import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.world.VoxelComponent;
import de.m_marvin.voxelengine.world.VoxelMaterial;

public class VoxelComponentLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public static final String VOXEL_COMPONENT_FORMAT = "vxl";
	
	protected final Gson gson = new GsonBuilder().create();
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;
	
	public VoxelComponentLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	@SuppressWarnings("unchecked")
	public VoxelComponent load(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		JsonObject json = gson.fromJson(reader, JsonObject.class);
		reader.close();
		int[][][][] voxelArr = gson.fromJson(json.get("voxels"), int[][][][].class);
		List<int[][][]> voxels = new ArrayList<>();
		for (int[][][] arr : voxelArr) {
			voxels.add(arr);
		}
		List<String> materials = gson.fromJson(json.get("materials"), ArrayList.class);
		List<VoxelMaterial> materialList = new ArrayList<>();
		for (String materialName : materials) {
			materialList.add(VoxelEngine.getInstance().getMaterialRegistry().getMaterial(new ResourceLocation(materialName)));
		}
		return new VoxelComponent(voxels, materialList);
	}
	
	public boolean save(File path, VoxelComponent object) throws IOException {
		if (object != null) {
			JsonObject json = new JsonObject();
			json.add("voxels", gson.toJsonTree(object.getVoxels()));
			List<String> materials = new ArrayList<>();
			for (VoxelMaterial voxelMaterial : object.getMaterials()) {
				materials.add(VoxelEngine.getInstance().getMaterialRegistry().nameOfMaterial(voxelMaterial).nameString());
			}
			json.add("materials", gson.toJsonTree(materials));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
			writer.write(gson.toJson(json));
			writer.close();
		}
		return false;
	}
	
	public VoxelComponent get(R location) {
		try {
			return load(new File(resourceLoader.resolveLocation(sourceFolder, location).getPath() + "." + VOXEL_COMPONENT_FORMAT));
		} catch (IOException e) {
			Logger.defaultLogger().logError("Failed to load component '" + location.nameString() + "'!");
			Logger.defaultLogger().printException(LogType.ERROR, e);
			return null;
		}
	}

	public boolean store(R location, VoxelComponent component) {
		try {
			return save(new File(resourceLoader.resolveLocation(sourceFolder, location).getPath() + "." + VOXEL_COMPONENT_FORMAT), component);
		} catch (IOException e) {
			Logger.defaultLogger().logError("Failed to save component '" + location.nameString() + "'!");
			Logger.defaultLogger().printException(LogType.ERROR, e);
			return false;
		}
	}
	
}
