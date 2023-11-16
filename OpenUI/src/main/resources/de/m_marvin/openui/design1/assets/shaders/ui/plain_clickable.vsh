#version 150

uniform mat4 ProjMat;

in vec3 position;
in vec2 pxpos;
in ivec2 pxsize;
in vec4 color;
in int pressed;

out vec2 vs_pxpos;
flat out ivec2 vs_pxsize;
out vec4 vs_color;
flat out int vs_pressed;

void main() {
	
	gl_Position = ProjMat * vec4(position, 1);
	vs_pxpos = pxpos;
	vs_pxsize = pxsize;
	vs_color = color;
	vs_pressed = pressed;
	
}