#version 150

#include math

uniform sampler2D Texture;
uniform float Interpolation;

in vec4 vertexcolor;
in vec2 vertexuv;
in vec2 vertexuvLast;

out vec4 FragColor;

void main() {

	vec4 textureColorLast = texture2D(Texture, vertexuvLast);
	vec4 textureColor = texture2D(Texture, vertexuv);
	vec4 textureColorFinal = lerp(textureColorLast, textureColor, Interpolation);
	
	FragColor = vertexcolor * textureColorFinal;
	
}
