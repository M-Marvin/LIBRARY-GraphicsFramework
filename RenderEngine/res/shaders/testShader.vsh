#version 150

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

in vec3 position;
in vec4 color;
in vec3 normal;
in vec2 uv;

out vec4 vertexColor;
out vec3 vertexNormal;
out vec2 vertexUV;

void main() {
	
	gl_Position = ProjMat *  ModelViewMat * vec4(position, 1.0);
	
	vertexUV = uv;
	vertexColor = color;
	
	
}