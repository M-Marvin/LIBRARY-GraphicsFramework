#version 150

uniform vec2 CursorPos;
uniform sampler2D Texture;

in vec2 vs_uv;
in vec2 vs_pos;
in vec4 vs_color;
flat in uint vs_pressed;

out vec4 glColor;

void main() {
	
	float cursorDist = distance(vs_pos, CursorPos);
	float overlayDensitity = max(0, (50 - cursorDist) / 50);
	
	vec4 textureColor = texture2D(Texture, vs_uv);
	
	vec4 color = textureColor * ((vs_pressed == 0U) ? vs_color : vec4(1, 1, 1, 1));
	
	glColor = color + (vec4(1, 1, 1, 1) * overlayDensitity);
	
}