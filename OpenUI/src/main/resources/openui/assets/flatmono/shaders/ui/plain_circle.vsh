#version 150

uniform mat4 ProjMat;

in vec3 position;
in vec2 pxpos;
in ivec2 pxcenter;
in int innerrad;
in int outerrad;
in vec4 color;

out vec2 vs_pxpos;
flat out ivec2 vs_pxcenter;
flat out int vs_innerrad;
flat out int vs_outerrad;
out vec4 vs_color;

void main() {
	
	gl_Position = ProjMat * vec4(position, 1);
	vs_pxpos = pxpos;
	vs_pxcenter = pxcenter;
	vs_innerrad = innerrad;
	vs_outerrad = outerrad;
	vs_color = color;
	
}