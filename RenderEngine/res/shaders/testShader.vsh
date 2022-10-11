#version 150

in vec3 position;
in vec4 color;
in vec3 normal;
in vec2 uv;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec3 vertexNormal;
out vec2 vertexUV;

void main() {
	
	gl_Position = ModelViewMat * ProjMat * vec4(position, 1.0);
	
	vertexColor = color;
	vertexNormal = normal;
	vertexUV = uv;
	
}