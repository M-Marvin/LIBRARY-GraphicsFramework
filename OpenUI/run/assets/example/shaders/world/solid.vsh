#version 150

#include ..\glsl\math

uniform mat3 AnimMat;
uniform mat3 AnimMatLast;
uniform mat4 ProjMat;
uniform mat4 ObjectMat;
uniform mat4 ModelViewMat;

in vec3 position;
in vec3 normal;
in vec4 color;
in vec2 uv;

out vec3 vs_normal;
out vec4 vs_color;
out vec2 vs_uv;
out vec2 vs_uvLast;

void main() {
	
	gl_Position = (ProjMat * ModelViewMat * ObjectMat) * vec4(position, 1);
	vs_normal = normal;
	vs_color = color;
	vs_uv = translateVec2(uv, AnimMat);
	vs_uvLast = translateVec2(uv, AnimMatLast);
	
}