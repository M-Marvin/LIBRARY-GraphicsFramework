package de.m_marvin.stonegenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.quickhull3d.Point3d;
import com.github.quickhull3d.QuickHull3D;

import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.IBufferSource;
import de.m_marvin.gframe.buffers.defimpl.RenderMode;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.gframe.translation.PoseStack;
import de.m_marvin.univec.impl.Vec3f;

public class Stone {
	
	public Vec3f pos;
	public Vec3f rotation;
	public Color color;
	public final List<Vec3f> vertecies;
	public final int[] indecies;
	
	/**
	 * @param pos position
	 * @param color color of this "object"
	 * @param smin random points sphere min distance to center
	 * @param smax random poitns sphere max distance to center
	 * @param srange max distance of sphere center to position
	 * @param n total number of random points
	 * @param ns number of random sphere positions (each with point count = n / ns)
	 */
	public Stone(Color color, float smin, float smax, float srange, int n, int ns) {
		
		this.pos = new Vec3f();
		this.rotation = new Vec3f();
		this.color = color;
		
		System.out.println("generate convex shape");
		System.out.println("num_initial_points: " + n);
		System.out.println("num_soheres: " + ns);
		System.out.println("rand_sphere_min_dist: " + smin);
		System.out.println("rand_sphere_max_dist: " + smax);
		System.out.println("rand_sphere_range: " + srange);
		
		Random rand = new Random();
		List<Vec3f> points = new ArrayList<>();

		int np = n / ns;
		float sx = 0;
		float sy = 0;
		float sz = 0;
		for (int is = 0; is < ns; is++) {
			sx += rand.nextFloat(-srange, +srange);
			sy += rand.nextFloat(-srange, +srange);
			sz += rand.nextFloat(-srange, +srange);
			for (int ip = 0; ip < np; ip++) {
				float x = rand.nextFloat(sx + smin, sx + smax) * (rand.nextBoolean() ? -1 : 1);
				float y = rand.nextFloat(sy + smin, sy + smax) * (rand.nextBoolean() ? -1 : 1);
				float z = rand.nextFloat(sz + smin, sz + smax) * (rand.nextBoolean() ? -1 : 1);
				points.add(new Vec3f(x, y, z));
			}
		}
		
		QuickHull3D hull = new QuickHull3D();
		hull.build(points.stream().map(v -> new Point3d(v.x, v.y, v.z)).toArray(Point3d[]::new));
		
		this.vertecies = Stream.of(hull.getVertices()).map(p -> new Vec3f((float) p.x, (float) p.y, (float) p.z)).toList();
		this.indecies = Stream.of(hull.getFaces()).flatMapToInt(f -> IntStream.of(f)).toArray();
		
		System.out.println("num_vertecies: " + this.vertecies.size());
		System.out.println("num_indecies: " + this.indecies.length);
		System.out.println("num_triangles: " + this.indecies.length / 3);
		
	}
	
	public void drawStone(IBufferSource<RenderMode<ResourceLocation>> bufferSource, PoseStack matrix) {
		
		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		
		BufferBuilder buffer = bufferSource.startBuffer(RenderTypes.stone());
		matrix.push();
		
		matrix.translate(this.pos.x, this.pos.y, this.pos.z);
		matrix.rotateDegrees(this.rotation.x, this.rotation.y, this.rotation.z);
		
		for (int i = 0; i < this.vertecies.size(); i++) {
			buffer.vertex(matrix, vertecies.get(i).x, vertecies.get(i).y, vertecies.get(i).z).color(r, g, b, 1).endVertex();
		}
		buffer.indecies(this.indecies);
		buffer.end();
		matrix.pop();
		
	}
	
}
