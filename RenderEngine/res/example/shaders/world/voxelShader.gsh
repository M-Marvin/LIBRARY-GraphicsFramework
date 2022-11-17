#version 150

layout (points) in;

in VS_OUT {
	vec4 color;
	vec2 uv;
	vec2 uvLast;
} gs_in[];

layout (triangle_strip, max_vertices = 4) out;

out GS_OUT {
	vec4 color;
	vec2 uv;
	vec2 uvLast;
} gs_out;

void main() {
	
	gl_Position = gl_in[0].gl_Position + vec4(1, -1, 0, 1);
	gs_out.uv = gs_in[0].uv + vec2(0.25, -0.25);
	gs_out.uvLast = gs_in[0].uvLast + vec2(0.25, -0.25);
	gs_out.color = gs_in[0].color;
    EmitVertex();
	
	gl_Position = gl_in[0].gl_Position + vec4(-1, -1, 0, 1);
	gs_out.uv = gs_in[0].uv + vec2(-0.25, -0.25);
	gs_out.uvLast = gs_in[0].uvLast + vec2(-0.25, -0.25);
	gs_out.color = gs_in[0].color;
    EmitVertex();
	
	gl_Position = gl_in[0].gl_Position + vec4(1, 1, 0, 1);
	gs_out.uv = gs_in[0].uv + vec2(0.25, 0.25);
	gs_out.uvLast = gs_in[0].uvLast + vec2(0.25, 0.25);
	gs_out.color = gs_in[0].color;
    EmitVertex();
	
	gl_Position = gl_in[0].gl_Position + vec4(-1, 1, 0, 1);
	gs_out.uv = gs_in[0].uv + vec2(-0.25, 0.25);
	gs_out.uvLast = gs_in[0].uvLast + vec2(-0.25, 0.25);
	gs_out.color = gs_in[0].color;
    EmitVertex();
    
    EndPrimitive();
	
}
