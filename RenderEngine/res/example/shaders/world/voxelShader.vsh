#version 150

#include math

uniform mat4 ViewMat;
uniform mat4 ProjMat;
uniform mat4 TranMat;
uniform mat3 AnimMat;
uniform mat3 AnimMatLast;

in vec3 position;
in vec4 color;
in vec2 uv;

out VS_OUT {
	vec4 color;
	vec2 uv;
	vec2 uvLast;
} vs_out;

void main() {
	
	gl_Position = ProjMat *  ModelViewMat * ObjectMat * vec4(position, 1.0);
	
	vs_out.uvLast = translate(uv, AnimMatLast);
	vs_out.uv = translate(uv, AnimMat);
	vs_out.color = color;
	
}