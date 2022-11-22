
vec4 lerpVec4(vec4 value1, vec4 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec2 translate(vec2 vector, mat3 matrix) {
	return (matrix * vec3(vector, 1)).xy;
}
