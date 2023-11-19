#version 150

uniform int BorderWidth;

in vec2 vs_pxpos;
flat in ivec2 vs_pxsize;
in vec4 vs_color;
flat in int vs_pressed;

out vec4 glColor;

void main() {
	
	vec4 color = (vs_pressed == 1) ? vec4(0, 1, 1, 1) : vs_color;
	
	if (vs_pressed == 2) {
		
		bool borderPixel = 
			vs_pxpos.x <= BorderWidth || 
			vs_pxpos.y <= BorderWidth || 
			vs_pxpos.x >= vs_pxsize.x - BorderWidth || 
			vs_pxpos.y >= vs_pxsize.y - BorderWidth;
		
		glColor = borderPixel ? color : vec4(0, 0, 0, 0);
		
	} else {
		
		glColor = color;
		
	}
	
}