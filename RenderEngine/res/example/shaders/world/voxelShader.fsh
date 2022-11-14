#version 150

#include math

uniform sampler2D Texture;
uniform float Interpolation;

in GS_OUT {
	vec4 color;
	vec2 uv;
	vec2 uvLast;
} fs_in;

out vec4 FragColor;

void main() {

	vec4 textureColorLast = texture2D(Texture, fs_in.uvLast);
	vec4 textureColor = texture2D(Texture, fs_in.uv);
	vec4 textureColorFinal = lerp(textureColorLast, textureColor, Interpolation);
	
	FragColor = fs_in.color * textureColorFinal;
	
}
