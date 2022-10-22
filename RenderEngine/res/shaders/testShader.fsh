#version 150

uniform sampler2D Texture;

in vec4 vertexColor;
in vec3 vertexNormal;
in vec2 vertexUV;

out vec4 FragColor;

void main() {
	
	FragColor = vec4(vertexUV, 0, 1) * vec4(255);
}

