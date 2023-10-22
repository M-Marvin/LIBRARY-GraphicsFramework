#version 150

#include math

in vec3 position;
in vec4 color;
in float size;

out VS_OUT {
	vec4 color;
	float size;
} vs_out;

void main() {
	
	gl_Position = vec4(position, 1);
	vs_out.color = color;
	vs_out.size = size;
	
}
