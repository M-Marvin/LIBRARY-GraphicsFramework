#version 150

in vec2 vs_pxpos;
flat in ivec2 vs_pxcenter;
flat in int vs_innerrad;
flat in int vs_outerrad;
in vec4 vs_color;

out vec4 glColor;

void main() {
	
	float dist = length(vs_pxpos - vs_pxcenter);
	
	glColor = (dist >= vs_innerrad && dist <= vs_outerrad) ? vs_color : vec4(0, 0, 0, 0);
	
}