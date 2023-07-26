#version 150

uniform mat4 ProjMat;

in vec2 position;
in vec4 color;

out vec4 vs_color;

void main() {
	
	gl_Position = ProjMat * vec4(position, 0, 1);
	vs_color = color;
	
}