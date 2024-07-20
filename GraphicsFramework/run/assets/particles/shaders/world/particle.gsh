#version 150

#include ..\glsl\math

uniform mat4 ViewMat;
uniform mat4 ProjMat;
uniform mat4 TranMat;
uniform float HalfVoxelSize;

layout (points) in;

in VS_OUT {
	vec4 color;
	float size;
} gs_in[];

layout (triangle_strip, max_vertices = 24) out;

out GS_OUT {
	vec4 color;
} gs_out;

// Creating a single vertex of a voxel-side

vec4 transform(vec4 vector) {
	return (ProjMat * ViewMat * TranMat) * vector;
}

void makeQuadVertex(vec3 offset) {
	vec3 offsetRotated = offset * HalfVoxelSize  * gs_in[0].size;
	gl_Position = transform(gl_in[0].gl_Position + vec4(offsetRotated.xyz, 0));
	gs_out.color = gs_in[0].color;
    EmitVertex();
}

// Creating the verteices of the voxel-sides

void makeQuadNorth() {
	makeQuadVertex(vec3(1, -1, -1));
	makeQuadVertex(vec3(-1, -1, -1));
	makeQuadVertex(vec3(1, 1, -1));
	makeQuadVertex(vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadSouth() {
	makeQuadVertex(vec3(1, 1, 1));
	makeQuadVertex(vec3(-1, 1, 1));
	makeQuadVertex(vec3(1, -1, 1));
	makeQuadVertex(vec3(-1, -1, 1));
	EndPrimitive();
}

void makeQuadEast() {
	makeQuadVertex(vec3(-1, 1, 1));
	makeQuadVertex(vec3(-1, 1, -1));
	makeQuadVertex(vec3(-1, -1, 1));
	makeQuadVertex(vec3(-1, -1, -1));
	EndPrimitive();
}

void makeQuadWest() {
	makeQuadVertex(vec3(1, -1, 1));
	makeQuadVertex(vec3(1, -1, -1));
	makeQuadVertex(vec3(1, 1, 1));
	makeQuadVertex(vec3(1, 1, -1));
	EndPrimitive();
}

void makeQuadUp() {
	makeQuadVertex(vec3(1, 1, 1));
	makeQuadVertex(vec3(1, 1, -1));
	makeQuadVertex(vec3(-1, 1, 1));
	makeQuadVertex(vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadDown() {
	makeQuadVertex(vec3(-1, -1, 1));
	makeQuadVertex(vec3(-1, -1, -1));
	makeQuadVertex(vec3(1, -1, 1));
	makeQuadVertex(vec3(1, -1, -1));
	EndPrimitive();
}

// Create all sides depending on the sides attribute

void main() {
	
	makeQuadNorth();
	makeQuadSouth();
	makeQuadEast();
	makeQuadWest();
	makeQuadUp();
	makeQuadDown();
	
}
