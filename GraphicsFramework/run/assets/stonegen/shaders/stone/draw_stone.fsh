#version 150

#include ..\glsl\math

in VS_OUT {
	vec4 color;
} vs_in;

out vec4 glColor;

void main() {
	
	glColor = vs_in.color;
	
}
