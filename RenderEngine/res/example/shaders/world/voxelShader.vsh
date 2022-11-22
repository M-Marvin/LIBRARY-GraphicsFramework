#version 150

in vec3 position;
in ivec3 voxel;
in vec4 color;
in vec4 texuv;
in ivec2 texsize;

out VS_OUT {
	ivec3 voxel;
	vec4 color;
	vec2 textureUVatlasSize;
	vec2 voxelUVatlasSize;
	ivec2 textureSize;
	vec2 textureUVatlas;
} vs_out;

void main() {
	
	gl_Position = vec4(position, 1.0);
	vs_out.voxel = voxel;
	vs_out.color = color;
	vs_out.textureUVatlasSize = texuv.zw;
	vs_out.voxelUVatlasSize = vs_out.textureUVatlasSize / texsize;
	vs_out.textureSize = texsize;
	vs_out.textureUVatlas = texuv.xy;
	
}