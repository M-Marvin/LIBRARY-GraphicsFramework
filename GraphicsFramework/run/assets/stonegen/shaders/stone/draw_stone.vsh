#version 150

#include ..\glsl\math

uniform mat4 ProjMat;
uniform mat4 ViewMat;

in vec3 position;
in vec4 color;
in float size;

out VS_OUT {
	vec4 color;
} vs_out;

void main() {
	
	gl_Position = ProjMat * ViewMat * vec4(position, 1);
	vs_out.color = color;
	
}
