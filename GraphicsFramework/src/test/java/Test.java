import java.io.File;
import java.io.FileInputStream;

import de.m_marvin.gframe.models.OBJParser;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;

public class Test {

	public static void main(String...strings) {
		
		OBJParser<ResourceLocation> parser = new OBJParser<>();
		
		try {
			parser.parseOBJFile(new FileInputStream(new File("E:\\GitHub\\APP-OpenPortal\\OpenPortal\\run\\assets\\openportal\\models\\test\\container2.obj")));
			parser.parseMTLFile(new FileInputStream(new File("E:\\GitHub\\APP-OpenPortal\\OpenPortal\\run\\assets\\openportal\\models\\test\\container2.mtl")));
			parser.makeModelData(new ResourceLocation("test:test/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
