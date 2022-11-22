#version 150

uniform mat4 ViewMat;
uniform mat4 ProjMat;
uniform mat4 TranMat;
uniform vec2 HalfVoxelUVSize;
uniform float HalfVoxelSize;

layout (points) in;

in VS_OUT {
	vec4 color;
	vec2 uvNS;
	vec2 uvNSLast;
	vec2 uvEW;
	vec2 uvEWLast;
	vec2 uvUD;
	vec2 uvUDLast;
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

void makeQuadVertex(vec2 uv, vec2 uvLast, vec2 uvOffset, vec3 offset) {
	gl_Position = transform(gl_in[0].gl_Position + vec4(offset.x * HalfVoxelSize, offset.y * HalfVoxelSize, offset.z * HalfVoxelSize, 0));
	gs_out.uv = uv + uvOffset * HalfVoxelUVSize;
	gs_out.uvLast = uvLast + uvOffset * HalfVoxelUVSize;
	gs_out.color = gs_in[0].color;
    EmitVertex();
}

void makeQuadNorth() {
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(1, -1), vec3(1, -1, -1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(-1, -1), vec3(-1, -1, -1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(1, 1), vec3(1, 1, -1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(-1, 1), vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadSouth() {
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(1, -1), vec3(1, -1, 1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(-1, -1), vec3(-1, -1, 1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(1, 1), vec3(1, 1, 1));
	makeQuadVertex(gs_in[0].uvNS, gs_in[0].uvNSLast, vec2(-1, 1), vec3(-1, 1, 1));
	EndPrimitive();
}

void makeQuadEast() {
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(1, -1), vec3(1, -1, 1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(-1, -1), vec3(1, -1, -1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(1, 1), vec3(1, 1, 1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(-1, 1), vec3(1, 1, -1));
	EndPrimitive();
}

void makeQuadWest() {
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(1, -1), vec3(-1, -1, 1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(-1, -1), vec3(-1, -1, -1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(1, 1), vec3(-1, 1, 1));
	makeQuadVertex(gs_in[0].uvEW, gs_in[0].uvEWLast, vec2(-1, 1), vec3(-1, 1, -1));
	EndPrimitive();
}

void makeQuadUp() {
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(1, -1), vec3(1, 1, -1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(-1, -1), vec3(-1, 1, -1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(1, 1), vec3(1, 1, 1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(-1, 1), vec3(-1, 1, 1));
	EndPrimitive();
}

void makeQuadDown() {
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(1, -1), vec3(1, -1, -1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(-1, -1), vec3(-1, -1, -1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(1, 1), vec3(1, -1, 1));
	makeQuadVertex(gs_in[0].uvUD, gs_in[0].uvUDLast, vec2(-1, 1), vec3(-1, -1, 1));
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
