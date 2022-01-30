package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;

	private UsuarioBuilder() {}
	
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder usuarioBuilder = new UsuarioBuilder();
		usuarioBuilder.usuario = new Usuario();
		usuarioBuilder.usuario.setNome("Usuario1");
		return usuarioBuilder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		return this;
	}
	
	public Usuario agora(){
		return usuario;
	}

}
