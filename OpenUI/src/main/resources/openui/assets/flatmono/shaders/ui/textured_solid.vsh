#version 150

uniform mat4 ProjMat;

in vec3 position;
in vec2 uv;
in vec4 color;

out vec2 vs_uv;
out vec4 vs_color;

void main() {
	
	gl_Position = ProjMat * vec4(position, 1);
	vs_uv = uv;
	vs_color = color;
	
}