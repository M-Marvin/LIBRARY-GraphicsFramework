package de.m_marvin.renderengine.resources;

import java.io.File;
import java.util.function.Consumer;

public class FileUtility {
	
	public static <R extends IResourceProvider<R>, FE extends ISourceFolder> void executeForEachFolder(ResourceLoader<R, FE> loader, FE folder, R location, Consumer<R> task) {
		task.accept(location);
		for (String entry : loader.listFoldersIn(folder, location)) {
			executeForEachFolder(loader, folder, location.locationOfFile(entry), task);
		}
	}
	
	public static void executeForEachFolder(File folderTreeStart, Consumer<String> task) {
		executeForEachFolder0(folderTreeStart, "", task);
	}
	
	protected static void executeForEachFolder0(File folderTreeStart, String path, Consumer<String> task) {
		task.accept(path);
		for (File entry : folderTreeStart.listFiles()) {
			if (entry.isDirectory()) executeForEachFolder0(entry, (path.isEmpty() ? path : path + "/") + entry.getName(), task);
		}
	}
	
}
