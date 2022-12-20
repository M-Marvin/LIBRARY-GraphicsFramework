
vec4 lerpVec4(vec4 value1, vec4 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec3 lerpVec3(vec3 value1, vec3 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec2 lerpVec2(vec2 value1, vec2 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

float lerp(float value1, float value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}



vec2 translateVec2(vec2 vector, mat3 matrix) {
	return (matrix * vec3(vector, 1)).xy;
}

vec3 translateVec3(vec3 vector, mat4 matrix) {
	return (matrix * vec4(vector, 1)).xyz;
}



vec4 quatFromAxisAndAngle(vec3 axis, float angle)
{ 
  vec4 qr;
  float half_angle = (angle * 0.5) * 3.14159 / 180.0;
  qr.x = axis.x * sin(half_angle);
  qr.y = axis.y * sin(half_angle);
  qr.z = axis.z * sin(half_angle);
  qr.w = cos(half_angle);
  return qr;
}

vec4 quatConj(vec4 q)
{ 
  return vec4(-q.x, -q.y, -q.z, q.w); 
}
  
vec4 quatMul(vec4 q1, vec4 q2)
{ 
  vec4 qr;
  qr.x = (q1.w * q2.x) + (q1.x * q2.w) + (q1.y * q2.z) - (q1.z * q2.y);
  qr.y = (q1.w * q2.y) - (q1.x * q2.z) + (q1.y * q2.w) + (q1.z * q2.x);
  qr.z = (q1.w * q2.z) + (q1.x * q2.y) - (q1.y * q2.x) + (q1.z * q2.w);
  qr.w = (q1.w * q2.w) - (q1.x * q2.x) - (q1.y * q2.y) - (q1.z * q2.z);
  return qr;
}

vec3 transformByQuat(vec3 position, vec4 qr)
{ 
  vec4 qr_conj = quatConj(qr);
  vec4 q_pos = vec4(position.x, position.y, position.z, 0);
  
  vec4 q_tmp = quatMul(qr, q_pos);
  qr = quatMul(q_tmp, qr_conj);
  
  return vec3(qr.x, qr.y, qr.z);
}
