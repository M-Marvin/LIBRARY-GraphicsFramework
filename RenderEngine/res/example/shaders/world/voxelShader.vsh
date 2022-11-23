#version 150

in vec3 position; // Position of the voxel in 3D space
in ivec3 voxel; // Position of the voxel in its component
in uint sides; // Bitmap of the visibility of the six sides
in vec4 color; // Color of the voxel
in vec4 texuv; // Position of the texture in the atlas (XY) and its width and height in the atlas (ZW)
in ivec2 texsize; // Size of the texture in pixels

out VS_OUT {
	ivec3 voxel;
	uint sides;
	vec4 color;
	vec2 textureUVatlasSize;
	vec2 voxelUVatlasSize;
	ivec2 textureSize;
	vec2 textureUVatlas;
} vs_out;

void main() {
	
	gl_Position = vec4(position, 1.0);
	vs_out.voxel = voxel;
	vs_out.sides = sides;
	vs_out.color = color;
	vs_out.textureUVatlasSize = texuv.zw;
	vs_out.voxelUVatlasSize = vs_out.textureUVatlasSize / texsize;
	vs_out.textureSize = texsize;
	vs_out.textureUVatlas = texuv.xy;
	
}