#version 150

#include ..\glsl\math

in GS_OUT {
	vec4 color;
} fs_in;

out vec4 glColor;

void main() {
	
	glColor = fs_in.color;
	
}
