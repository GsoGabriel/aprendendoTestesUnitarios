package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.machers.MatchersProprios;
import br.ce.wcaquino.runners.ParallelRunner;
import br.ce.wcaquino.servicos.EmailService;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {
	
	@InjectMocks @Spy
	private LocacaoService service;
	@Mock
	private SPCService spcService;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector errors = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("iniciando2...");
//		service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		spcService = Mockito.mock(SPCService.class);
//		service.setSPCService(spcService);
//		email = Mockito.mock(EmailService.class);
//		service.setEmailService(email);
	}
	
	@After
	public void tearDown() {
		System.out.println("finalizando2...");
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(120.00).agora(), umFilme().comValor(20.00).agora());

		Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();
		
		//acao
		Locacao loc = service.alugarFilme(usuario, filmes);
		
		// verificacao
		errors.checkThat(loc.getValor(), is(equalTo(140.00)));
		errors.checkThat(DataUtils.isMesmaData(loc.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		errors.checkThat(DataUtils.isMesmaData(loc.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		Assert.assertThat(valor, is(5.0));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testeLocacaoChecandoFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().semEstoque().agora());

		//acao
		service.alugarFilme(usuario, filmes);
		
		
	}
	
	@Test
	public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	@Test
	public void testeLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora(); 
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		//acao
		service.alugarFilme(usuario, null);
	}
		
	@Test
	public void naoDeveDevolverFilmeDomingo() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).obterData();
		
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// verificacao
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

		//acao
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario Negativado"));
		}
		
		//verificacao
		Mockito.verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){
		//cenrio
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = umUsuario().comNome("Atrsado tbm").agora();
		List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora());
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		//acao
		service.notificarAtrasos();
		
		//verificacao
		Mockito.verify(email, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
		Mockito.verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verify(email).notificarAtraso(usuario);
//		Mockito.verify(email).notificarAtraso(usuario3);
		Mockito.verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new RuntimeException("Falha catastrófica!"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarLocacao() {
		//cenario
		Locacao locacao= LocacaoBuilder.umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetorno = argCapt.getValue();
		
		errors.checkThat(locacaoRetorno.getValor(), CoreMatchers.is(12.0));
		errors.checkThat(locacaoRetorno.getDataLocacao(), MatchersProprios.ehHoje());
		errors.checkThat(locacaoRetorno.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));
	}
	
//	@Test
//	public void deveAlugarFilmeSemCalcularValor() throws Exception {
//		//cenario
//		Usuario usuario = umUsuario().agora();
//		List<Filme> filmes = Arrays.asList(umFilme().agora());
//		
//		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//verificacao
//		Assert.assertThat(locacao.getValor(), is(1.0));
//		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
//	}
//	
	@Test
	public void deveAlugarValorLocacao() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		Assert.assertThat(valor, is(5.0));
	}
//	@Test
//	public void testeLocacaoChecandoFilmeSemEstoque_2(){
//		//cenario
//		Usuario usuario = new Usuario("Gabriel");
//		Filme filme = new Filme("Miranha", 0, 120.00);
//		LocacaoService locSrv = new LocacaoService();
//
//		//acao
//		try {
//			locSrv.alugarFilme(usuario, filme);
//			Assert.fail("Deveria ter lançado uma exceção");
//		} catch (Exception e) {
//			Assert.assertThat(e.getMessage(), is("Filme sem estoque."));
//		}
//	}
//	
//	@Test
//	public void testeLocacaoChecandoFilmeSemEstoque_3() throws Exception{
//		//cenario
//		Usuario usuario = new Usuario("Gabriel");
//		Filme filme = new Filme("Miranha", 0, 120.00);
//		LocacaoService locSrv = new LocacaoService();
//		
//		exception.expect(Exception.class);
//		exception.expectMessage("Filme sem estoque.");
//
//		//acao
//		locSrv.alugarFilme(usuario, filme);
//		
//		
//	}
}
