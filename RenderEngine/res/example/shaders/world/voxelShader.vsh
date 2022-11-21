#version 150

#include math

uniform mat4 ViewMat;
uniform mat4 ProjMat;
uniform mat4 TranMat;
uniform mat3 AnimMat;
uniform mat3 AnimMatLast;

in vec3 position;
in vec4 color;
in vec4 uv;

out VS_OUT {
	vec4 color;
	vec2 uvNS;
	vec2 uvNSLast;
	vec2 uvEW;
	vec2 uvEWLast;
	vec2 uvUD;
	vec2 uvUDLast;
} vs_out;

void main() {
	
	gl_Position = vec4(position, 1.0);
	
	vs_out.uvEWLast = translate(uv.xy, AnimMatLast);
	vs_out.uvEW = translate(uv.xy, AnimMat);
	vs_out.uvNSLast = translate(uv.yz, AnimMatLast);
	vs_out.uvNS = translate(uv.yz, AnimMat);
	vs_out.uvUDLast = translate(uv.xw, AnimMatLast);
	vs_out.uvUD = translate(uv.xw, AnimMat);
	
	vs_out.color = color;
	
}