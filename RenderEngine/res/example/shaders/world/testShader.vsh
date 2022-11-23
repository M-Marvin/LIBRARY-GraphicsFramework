#version 150

#include math

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 AnimMat;
uniform mat3 AnimMatLast;
uniform mat4 ObjectMat;

in vec3 position;
in vec4 color;
in vec3 normal;
in vec2 uv;

out vec4 vertexColor;
out vec3 vertexNormal;
out vec2 vertexUV;
out vec2 vertexUVLast;

void main() {
	
	gl_Position = ProjMat *  ModelViewMat * ObjectMat * vec4(position, 1.0);
	
	vertexUVLast = translateVec2(uv, AnimMatLast);
	vertexUV = translateVec2(uv, AnimMat);
	vertexColor = color;
		
}