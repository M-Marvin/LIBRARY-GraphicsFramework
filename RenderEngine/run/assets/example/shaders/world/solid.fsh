#version 150

#include math

uniform float Interpolation;
uniform sampler2D Texture;

in vec3 vs_normal;
in vec4 vs_color;
in vec2 vs_uv;
in vec2 vs_uvLast;

out vec4 glColor;

void main() {
	
	vec4 textureColorLast = texture2D(Texture, vs_uvLast);
	vec4 textureColor = texture2D(Texture, vs_uv);
	vec4 textureColorFinal = lerpVec4(textureColorLast, textureColor, Interpolation);
	
	glColor = vs_color * textureColorFinal;
	
}