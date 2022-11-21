#version 150

#include math

uniform mat3 AnimMat;
uniform mat3 AnimMatLast;

in vec3 position;
in vec4 color;
in vec2 uv;

out vec4 vertexcolor;
out vec2 vertexuv;
out vec2 vertexuvLast;

void main() {
	
	gl_Position = vec4(position, 1.0);
	
	vertexuvLast = translate(uv, AnimMatLast);
	vertexuv = translate(uv, AnimMat);
	vertexcolor = color;
	
}