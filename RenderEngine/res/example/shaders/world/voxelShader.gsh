#version 150

#include math

uniform mat4 ViewMat;
uniform mat4 ProjMat;
uniform mat4 TranMat;
uniform mat3 AnimMat;
uniform mat3 AnimMatLast;
uniform float HalfVoxelSize;

layout (points) in;

in VS_OUT {
	ivec3 voxel;
	vec4 color;
	vec2 textureUVatlasSize;
	vec2 voxelUVatlasSize;
	ivec2 textureSize;
	vec2 textureUVatlas;
} gs_in[];

layout (triangle_strip, max_vertices = 24) out;

out GS_OUT {
	vec4 color;
	vec2 uv;
	vec2 uvLast;
} gs_out;

vec4 transform(vec4 vector) {
	return (ProjMat * ViewMat * TranMat) * vector;
}

void makeQuadVertex(vec2 textureUV, vec3 offset) {
	gl_Position = transform(gl_in[0].gl_Position + vec4(offset.x * HalfVoxelSize, offset.y * HalfVoxelSize, offset.z * HalfVoxelSize, 0));
	gs_out.uv = translate(textureUV, AnimMat);
	gs_out.uvLast = translate(textureUV, AnimMatLast);
	gs_out.color = gs_in[0].color;
    EmitVertex();
}

void makeQuadNorth() {
	vec2 v = mod(vec2(gs_in[0].textureSize.x - (gs_in[0].voxel.x + 1), gs_in[0].voxel.y), gs_in[0].textureSize) / gs_in[0].textureSize;
	vec2 pixelUVposition = v * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(1, -1, -1));
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, -1));
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(1, 1, -1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadSouth() {
	vec2 pixelUVposition = (mod(gs_in[0].voxel.xy, gs_in[0].textureSize) / gs_in[0].textureSize) * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(1, 1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, 1));
	EndPrimitive();
}

void makeQuadEast() {
	vec2 pixelUVposition = (mod(gs_in[0].voxel.zy, gs_in[0].textureSize) / gs_in[0].textureSize) * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, -1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadWest() {
	vec2 v = mod(vec2(gs_in[0].textureSize.x - (gs_in[0].voxel.z + 1), gs_in[0].voxel.y), gs_in[0].textureSize) / gs_in[0].textureSize;
	vec2 pixelUVposition = v * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(1, -1, -1));
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(1, 1, 1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(1, 1, -1));
	EndPrimitive();
}

void makeQuadUp() {
	vec2 v = mod(vec2(gs_in[0].voxel.x, gs_in[0].textureSize.y - (gs_in[0].voxel.z + 1)), gs_in[0].textureSize) / gs_in[0].textureSize;
	vec2 pixelUVposition = v * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, 1, -1));
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(1, 1, 1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(1, 1, -1));
	EndPrimitive();
}

void makeQuadDown() {
	vec2 pixelUVposition = (mod(gs_in[0].voxel.xz, gs_in[0].textureSize) / gs_in[0].textureSize) * gs_in[0].textureUVatlasSize + gs_in[0].textureUVatlas;
	makeQuadVertex(pixelUVposition + vec2(0, 1) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(0, 0) * gs_in[0].voxelUVatlasSize, vec3(-1, -1, -1));
	makeQuadVertex(pixelUVposition + vec2(1, 1) * gs_in[0].voxelUVatlasSize, vec3(1, -1, 1));
	makeQuadVertex(pixelUVposition + vec2(1, 0) * gs_in[0].voxelUVatlasSize, vec3(1, -1, -1));
	EndPrimitive();
}

void main() {
	
	makeQuadNorth();
	makeQuadSouth();
	makeQuadEast();
	makeQuadWest();
	makeQuadUp();
	makeQuadDown();
}
