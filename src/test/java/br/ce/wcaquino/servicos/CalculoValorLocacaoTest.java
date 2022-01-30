package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spcService;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	private static Filme filme1 = umFilme().comValor(4.0).agora();
	private static Filme filme2 = umFilme().comValor(4.0).agora();
	private static Filme filme3 = umFilme().comValor(4.0).agora();
	private static Filme filme4 = umFilme().comValor(4.0).agora();
	private static Filme filme5 = umFilme().comValor(4.0).agora();
	private static Filme filme6 = umFilme().comValor(4.0).agora();
	
	@Parameters
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2, filme3), 11.0},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0}
		});
	}
	
	@Test
	public void deveLancarDescontos_terceiroFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Gabriel");
		
		//acao
		Locacao loc = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(loc.getValor(), is(valorLocacao));
	}
	
	@Test
	public void print() {
		System.out.println(valorLocacao);
	}
	
}
