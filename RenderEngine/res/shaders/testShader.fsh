#version 150

uniform sampler2D Texture;

in vec4 vertexColor;
in vec3 vertexNormal;
in vec2 vertexUV;

out vec4 FragColor;

void main() {
	
	vec4 textureColor = texture2D(Texture, vertexUV);
	FragColor = textureColor * vertexColor;
	
}

