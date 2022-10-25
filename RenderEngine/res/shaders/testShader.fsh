#version 150

#include math

uniform sampler2D Texture;
uniform float Interpolation;

in vec4 vertexColor;
in vec3 vertexNormal;
in vec2 vertexUV;
in vec2 vertexUVLast;

out vec4 FragColor;

void main() {
	
	vec4 textureColorLast = texture2D(Texture, vertexUVLast);
	vec4 textureColor = texture2D(Texture, vertexUV);
	vec4 textureColorFinal = lerp(textureColorLast, textureColor, Interpolation);
	
	FragColor = vertexColor * textureColorFinal;
	
}
