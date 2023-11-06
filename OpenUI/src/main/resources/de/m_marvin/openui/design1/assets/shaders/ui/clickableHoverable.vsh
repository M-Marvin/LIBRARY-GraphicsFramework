#version 150

uniform mat4 ProjMat;

in vec3 position;
in vec4 color;
in uint pressed;

out vec2 vs_pos;
out vec4 vs_color;
flat out uint vs_pressed;

void main() {
	
	gl_Position = ProjMat * vec4(position, 1);
	vs_pos = position.xy;
	vs_color = color;
	vs_pressed = pressed;
	
}