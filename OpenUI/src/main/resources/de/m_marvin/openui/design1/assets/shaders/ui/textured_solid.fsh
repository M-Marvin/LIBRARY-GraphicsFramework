#version 150

uniform sampler2D Texture;

in vec2 vs_uv;
in vec4 vs_color;

out vec4 gl_Color;

void main() {
	
	vec4 textureColor = texture2D(Texture, vs_uv);
	
	gl_Color = vs_color * textureColor;
	
}