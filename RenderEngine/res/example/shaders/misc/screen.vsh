#version 150

#include math

uniform mat3 AnimMat;
uniform mat3 AnimMatLast;
uniform mat3 ProjMat;

in vec2 position;
in vec4 color;
in vec2 uv;

out vec4 vs_color;
out vec2 vs_uv;
out vec2 vs_uvLast;

void main() {
	
	gl_Position = vec4(ProjMat * vec3(position, 0), 1);
	vs_color = color;
	vs_uv = translateVec2(uv, AnimMat);
	vs_uvLast = translateVec2(uv, AnimMatLast);
	
}