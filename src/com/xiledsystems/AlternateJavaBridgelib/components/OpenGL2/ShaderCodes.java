package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

public class ShaderCodes {

	private ShaderCodes() {		
	}
	
	public class Vertex {
		public final static String BASIC = 	"uniform mat4 uMVPMatrix;" +
											"attribute vec4 vPosition;" +
											"void main() {" +
											"  gl_Position = vPosition * uMVPMatrix;" +
											"}";
		
		public final static String SPRITE = "uniform mat4 uMVMatrix;" +
											"uniform mat4 uMVPMatrix;" +
											"attribute vec4 aPosition;" +
											"attribute vec2 aTexCoordinate;" +
											"varying vec3 v_Position;" +
											"varying vec2 v_TexCoordinate;" +
											"void main() {" +
											"  v_Position = vec3(uMVMatrix * aPosition);" +
											"  v_TexCoordinate = aTexCoordinate;" +
											"  gl_Position = aPosition * uMVPMatrix;" +											
											"}";
	}
	
	public class Fragment {
		public final static String BASIC = "precision mediump float;" +
											"uniform vec4 vColor;" +
											"void main() {" +
											"  gl_FragColor = vColor;" +
											"}";
		
		public final static String SPRITE = "precision mediump float;" +
											"uniform sampler2D u_Texture;" +											
											"uniform vec3 v_Position;" +
											"varying vec2 v_TexCoordinate;" +
											"void main() {" +
											"  gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));" +
											"}";
	}
	
}
