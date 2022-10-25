
vec4 lerp(vec4 color1, vec4 color2, float interpolation) {
	return color2 * (1 - interpolation) + color1 * interpolation;
}

vec2 translate(vec2 vector, mat3 matrix) {
	return (matrix * vec3(vector, 1)).xy;
}
